import torch
from PIL import Image
import torchvision.transforms as transforms
from scipy.interpolate import PchipInterpolator
import matplotlib.pyplot as plt
import numpy as np
import io

from app.service.image_service import fig_to_image, chart_to_bytes

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

def regression_quantile(model: torch.nn.Module, image: Image.Image, transform: transforms) -> torch.Tensor:
    img = transform(image).unsqueeze(0)
    img_tensor = img.to(device)

    model.eval()
    with torch.no_grad():
        output = model(img_tensor)

    return output

def make_quantile_chart(quantiles: torch.Tensor) -> plt.Figure:
    q = quantiles.detach().cpu().numpy().flatten()
    q = np.sort(q)                      # PCHIP требует возрастающих значений
    q = q + np.arange(len(q)) * 1e-9    # защита от одинаковых квантилей

    tau = np.linspace(0.05, 0.95, len(q))
    z_grid = np.linspace(q.min(), q.max(), 200)

    cdf_vals = PchipInterpolator(q, tau)(z_grid)

    pdf = np.gradient(cdf_vals, z_grid)
    pdf = np.maximum(pdf, 0)
    pdf /= np.trapezoid(pdf, z_grid)

    fig, ax = plt.subplots(figsize=(7, 4))
    ax.plot(z_grid, pdf, label="PDF")
    ax.set_xlabel("z", fontsize=14)
    ax.set_ylabel("PDF", fontsize=14)
    ax.tick_params(labelsize=12)
    # ax.set_title("PDF для photo-z CNN", fontsize=14)
    ax.legend()

    return fig
