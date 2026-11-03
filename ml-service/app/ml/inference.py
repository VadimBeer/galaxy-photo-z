import torch
from PIL import Image
import torchvision.transforms as transforms
from scipy.interpolate import PchipInterpolator
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from catboost import CatBoostRegressor

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

def regression_quantile(model: torch.nn.Module, image: Image.Image, transform: transforms) -> torch.Tensor:
    img = transform(image).unsqueeze(0)
    img_tensor = img.to(device)

    model.eval()
    with torch.no_grad():
        output = model(img_tensor)

    return output

def regression_quantile_catboost(
    model: CatBoostRegressor,
    features: pd.DataFrame,
    taus: np.ndarray,
) -> np.ndarray:
    """Возвращает квантили одного объекта с формой (1, число квантилей)."""
    if len(features) != 1:
        raise ValueError("Ожидаются признаки ровно одного объекта")

    ordered_features = features.loc[:, model.feature_names_]
    prediction = np.asarray(
        model.predict(ordered_features),
        dtype=float,
    )

    expected_shape = (1, len(taus))
    if prediction.shape != expected_shape:
        raise ValueError(
            f"Неверная форма прогноза: {prediction.shape}, "
            f"ожидалась {expected_shape}"
        )

    if not np.isfinite(prediction).all():
        raise ValueError("Модель вернула некорректные значения квантилей")

    return np.sort(prediction, axis=1)

def make_quantile_chart(quantiles, taus=None) -> plt.Figure:
    """Условная плотность внутри предсказанного квантильного интервала."""
    if isinstance(quantiles, torch.Tensor):
        quantiles = quantiles.detach().cpu().numpy()

    q = np.asarray(quantiles, dtype=float)

    if q.ndim == 2 and q.shape[0] == 1:
        q = q[0]

    if q.ndim != 1 or len(q) < 2:
        raise ValueError("Ожидаются квантили одного объекта")

    if taus is None:
        taus = np.linspace(0.05, 0.95, len(q))

    taus = np.asarray(taus, dtype=float)

    if taus.shape != q.shape:
        raise ValueError("Количество квантилей и уровней tau не совпадает")

    if not np.isfinite(q).all() or not np.isfinite(taus).all():
        raise ValueError("Квантили и tau должны быть конечными числами")

    if (
        np.any(np.diff(taus) <= 0)
        or taus[0] <= 0
        or taus[-1] >= 1
    ):
        raise ValueError("Уровни tau должны возрастать внутри интервала (0, 1)")

    q = np.sort(q)

    # Небольшое сглаживание совпавших квантилей для интерполяции.
    eps = 1e-8 * max(1.0, float(np.ptp(q)))
    for i in range(1, len(q)):
        q[i] = max(q[i], q[i - 1] + eps)

    conditional_cdf = (taus - taus[0]) / (taus[-1] - taus[0])
    interpolator = PchipInterpolator(
        q, conditional_cdf, extrapolate=False
    )

    z_grid = np.unique(
        np.concatenate([np.linspace(q[0], q[-1], 1000), q])
    )
    pdf = np.maximum(interpolator.derivative()(z_grid), 0.0)

    area = np.trapezoid(pdf, z_grid)
    if not np.isfinite(area) or area <= 0:
        raise ValueError("Не удалось нормировать плотность")

    pdf /= area

    fig, ax = plt.subplots(figsize=(7, 4))
    ax.plot(z_grid, pdf, label="Условная плотность")
    ax.set_xlabel("z", fontsize=14)
    ax.set_ylabel("Плотность", fontsize=14)
    ax.set_title(
        f"Интервал квантилей {taus[0]:.2f}–{taus[-1]:.2f}"
    )
    ax.tick_params(labelsize=12)
    ax.legend()
    fig.tight_layout()

    return fig