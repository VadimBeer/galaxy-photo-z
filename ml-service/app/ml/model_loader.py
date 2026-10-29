from typing import Any
from pathlib import Path
import torch
from app.ml.model_CNN import CNN

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

models = [CNN_V1, CNN_V2]

def find_model(model_id: int) -> ModelDTO:
    for model in models:
        if model.id == model_id:
            return model

    raise Exception(f"Модель с id = {model_id} не найдена")

def load_model(model_id: int) -> Any:
    dto = find_model(model_id)
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
