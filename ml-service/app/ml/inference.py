import torch
from PIL import Image
import torchvision.transforms as transforms


device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

def regression_quantile(model: torch.nn.Module, image: Image.Image, transform: transforms) -> torch.Tensor:
    img = transform(image).unsqueeze(0)
    img_tensor = img.to(device)

    model.eval()
    with torch.no_grad():
        output = model(img_tensor)

    return output

