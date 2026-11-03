from typing import Any
from pathlib import Path
import torch
from app.ml.model_CNN import CNN
import json
from functools import lru_cache
from catboost import CatBoostRegressor

num_quantiles = 19
WEIGHTS_DIR = Path(__file__).resolve().parents[2] / "weights"

class ModelDTO:
    def __init__(self, id: int, model_name: str):
        self.id = id
        self.model_name = model_name

# слабая модель
CNN_V1 = ModelDTO(1, "CNN_V1")
# более мощная модель
CNN_V2 = ModelDTO(2, "CNN_V2")
CATBOOST_V1 = ModelDTO(3, "CATBOOST_V1")

models = [CNN_V1, CNN_V2, CATBOOST_V1]

def find_model(model_id: int) -> ModelDTO:
    for model in models:
        if model.id == model_id:
            return model

    raise Exception(f"Модель с id = {model_id} не найдена")

@lru_cache(maxsize=1)
def load_catboost_metadata() -> dict:
    metadata_path = WEIGHTS_DIR / "metadata.json"

    if not metadata_path.is_file():
        raise FileNotFoundError(
            f"Не найден файл метаданных CatBoost: {metadata_path}"
        )

    with metadata_path.open(encoding="utf-8") as file:
        metadata = json.load(file)

    feature_cols = metadata.get("feature_cols")
    taus = metadata.get("taus")

    if not isinstance(feature_cols, list) or not feature_cols:
        raise ValueError("В metadata.json отсутствует список feature_cols")

    if not isinstance(taus, list) or len(taus) != num_quantiles:
        raise ValueError(
            f"В metadata.json ожидается {num_quantiles} уровней квантилей"
        )

    return metadata

@lru_cache(maxsize=8)
def load_model(model_id: int) -> Any:
    dto = find_model(model_id)
    if dto.model_name == "CATBOOST_V1":
        weights_path = WEIGHTS_DIR / "catboost_photoz.cbm"

        if not weights_path.is_file():
            raise FileNotFoundError(
                f"Не найден файл модели CatBoost: {weights_path}"
            )

        metadata = load_catboost_metadata()

        model = CatBoostRegressor()
        model.load_model(str(weights_path))

        if model.feature_names_ != metadata["feature_cols"]:
            raise ValueError(
                "Названия или порядок признаков модели "
                "не совпадают с metadata.json"
            )

        return model

    model = None

    if dto.model_name == "CNN_V1":
        model = CNN(num_quantiles)
        # model.load_state_dict(torch.load("app/ml/model_CNN/CNN_V1.pth"))

    if dto.model_name == "CNN_V2":
        model = CNN(num_quantiles)

        weights_path = WEIGHTS_DIR / "CNN_V2.pth"

        model.load_state_dict(
            torch.load(
                weights_path,
                map_location=torch.device("cpu"),
                weights_only=True,
            )
        )


    return model
