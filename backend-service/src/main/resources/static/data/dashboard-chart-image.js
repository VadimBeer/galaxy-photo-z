const image = document.getElementById("dashboard-image");
const loading = document.getElementById("image-loading");
const error = document.getElementById("image-error");

const modelName = document.getElementById("dashboard-model-name");
const modelVersion = document.getElementById("dashboard-model-version");

async function loadDashboardImage() {
    try {
        const response = await fetch("api/v1/dashboard/chart");

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const chart = await response.json();

        // Отображаем название и версию модели
        modelName.textContent = chart.modelName ?? "Не указана";

        modelVersion.textContent = chart.modelVersion
            ? `(${chart.modelVersion})`
            : "";

        // Загружаем изображение
        image.onload = () => {
            image.style.display = "block";
            loading.style.display = "none";
        };

        image.onerror = () => {
            loading.style.display = "none";
            error.style.display = "block";
        };

        // urlObjectKey должен содержать доступный URL изображения
        image.src = chart.urlObjectKey;

    } catch (e) {
        console.error("Ошибка загрузки изображения:", e);

        loading.style.display = "none";
        error.style.display = "block";

        modelName.textContent = "Недоступна";
    }
}

loadDashboardImage();