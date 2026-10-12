import torch
from PIL import Image
import torchvision.transforms as transforms
from scipy.interpolate import PchipInterpolator
import matplotlib.pyplot as plt
import numpy as np

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

def regression_quantile(model: torch.nn.Module, image: Image.Image, transform: transforms) -> torch.Tensor:
    img = transform(image).unsqueeze(0)
    img_tensor = img.to(device)

    model.eval()
    with torch.no_grad():
        output = model(img_tensor)

    return output

def make_quntile_chart(quantiles: torch.Tensor):
    z_grid = np.linspace(quantiles.min(), quantiles.max(), 200)
    tau = np.linspace(0.05, 0.95, 19)

    cdf = PchipInterpolator(quantiles.flatten(), tau)
    cdf_vals = cdf(z_grid)

    pdf = np.gradient(cdf_vals, z_grid)
    pdf = np.maximum(pdf, 0)
    pdf /= np.trapz(pdf, z_grid)

    plt.plot(z_grid, pdf)
    plt.xlabel("z", fontsize=14)
    plt.ylabel("PDF", fontsize=14)
    plt.xticks(fontsize=12)
    plt.yticks(fontsize=12)
    plt.title("PDF для photo-z CNN", fontsize=14)
    plt.legend()
    plt.show()