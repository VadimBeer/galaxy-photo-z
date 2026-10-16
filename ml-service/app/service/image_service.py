from PIL import Image
import io
import matplotlib.pyplot as plt

def preprocessing_image(image: bytes) -> Image.Image:
    img = Image.open(io.BytesIO(image))

    if img.width != 128 or img.height != 128:
        img = img.resize((128, 128))

    return img

def fig_to_image(fig: plt.Figure, dpi: int = 150) -> Image.Image:
    buf = io.BytesIO()
    fig.savefig(buf, format="png", dpi=dpi, bbox_inches="tight")
    buf.seek(0)
    img = Image.open(buf)
    img.load()
    return img

def chart_to_bytes(fig, dpi: int = 150) -> io.BytesIO:
    buf = io.BytesIO()
    fig.savefig(buf, format="png", dpi=dpi, bbox_inches="tight")
    buf.seek(0)
    return buf
