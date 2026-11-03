import io

import numpy as np
import pandas as pd


def prepare_catboost_features(
    data: pd.DataFrame,
    metadata: dict,
) -> pd.DataFrame:
    """Подготовка признаков по тем же правилам, что и при обучении."""
    raw_cols = metadata["raw_feature_cols"]
    feature_cols = metadata["feature_cols"]

    missing = sorted(set(raw_cols) - set(data.columns))
    if missing:
        raise ValueError(
            "В CSV отсутствуют обязательные столбцы: "
            + ", ".join(missing)
        )

    features = data.loc[:, raw_cols].copy()

    for column in raw_cols:
        try:
            features[column] = pd.to_numeric(
                features[column], errors="raise"
            )
        except (ValueError, TypeError) as exc:
            raise ValueError(
                f"Столбец '{column}' должен содержать числа "
                "или пустые значения"
            ) from exc

    # Служебные значения убираем до расчёта цветов.
    features = features.replace(
        [-9999, np.inf, -np.inf], np.nan
    )

    for band in metadata["bands"]:
        features[f"m_{band}"] = (
            features[f"mag_{band}"]
            - features[f"extinction_{band}"]
        )

    for a, b in metadata["color_pairs"]:
        features[f"c_{a}{b}"] = (
            features[f"m_{a}"] - features[f"m_{b}"]
        )

    # Порядок столбцов должен совпадать с обучением.
    result = features.loc[:, feature_cols].replace(
        [np.inf, -np.inf], np.nan
    )

    if result.isna().all(axis=1).any():
        raise ValueError(
            "У объекта отсутствуют все признаки для прогноза"
        )

    return result


def read_catboost_csv(
    file_bytes: bytes,
    metadata: dict,
) -> pd.DataFrame:
    """CSV в UTF-8: заголовок и одна строка данных, разделитель — запятая."""
    if not file_bytes:
        raise ValueError("Загружен пустой файл")

    try:
        # Достаточно двух строк, чтобы обнаружить несколько объектов.
        data = pd.read_csv(
            io.BytesIO(file_bytes),
            encoding="utf-8-sig",
            sep=",",
            nrows=2,
        )
    except (
        UnicodeDecodeError,
        pd.errors.EmptyDataError,
        pd.errors.ParserError,
    ) as exc:
        raise ValueError(
            "Не удалось прочитать CSV. "
            "Нужен файл UTF-8 с разделителем-запятой"
        ) from exc

    if data.empty:
        raise ValueError("В CSV нет строк с данными")

    if len(data) != 1:
        raise ValueError(
            "Для одного эксперимента CSV должен содержать "
            "ровно один объект"
        )

    return prepare_catboost_features(data, metadata)