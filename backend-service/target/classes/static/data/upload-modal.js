const modal = document.getElementById("uploadModal");
const openButton = document.getElementById("openUpload");
const closeButton = document.getElementById("closeModal");
const fileInput = document.getElementById("dataset");
const sendButton = document.getElementById("sendButton");
const selectedFile = document.getElementById("selectedFile");

const inputHint = document.getElementById("inputHint");

const inputRules = {
    "2": {
        accept: ".jpg,.jpeg,.png",
        hint: "CNN V2: выберите изображение JPEG или PNG."
    },
    "3": {
        accept: ".csv",
        hint: "CatBoost V1: выберите CSV UTF-8 с разделителем-запятой, "
            + "заголовками признаков и одной строкой объекта. "
            + "Файл Excel нужно экспортировать в CSV."
    }
};

let selectedModelId = sessionStorage.getItem("selectedModelVersionId");

function updateInputFormat() {
    const rule = inputRules[String(selectedModelId)];

    fileInput.disabled = !rule;
    fileInput.accept = rule?.accept ?? "";
    inputHint.textContent = rule?.hint
        ?? "Сначала выберите поддерживаемую модель.";
}

// Кнопка активна, только если выбраны и файл, и модель
function updateSendButton() {
    const rule = inputRules[String(selectedModelId)];

    sendButton.disabled = !(
        rule && fileInput.files.length > 0
    );
}

window.addEventListener("model-selected", (event) => {
    const nextModelId = String(event.detail.modelId);

    if (String(selectedModelId) !== nextModelId) {
        fileInput.value = "";
        selectedFile.textContent = "";
    }

    selectedModelId = nextModelId;
    updateInputFormat();
    updateSendButton();
});

openButton.addEventListener("click", (event) => {
    event.preventDefault();
    modal.showModal();
});

closeButton.addEventListener("click", () => {
    modal.close();
});

fileInput.addEventListener("change", () => {
    if (fileInput.files.length === 0) {
        selectedFile.textContent = "";
    } else {
        selectedFile.textContent = `Выбран файл: ${fileInput.files[0].name}`;
    }
    updateSendButton();
});

sendButton.addEventListener("click", async () => {
    const file = fileInput.files[0];
    if (!file || !selectedModelId) {
        return;
    }

    sendButton.disabled = true;
    sendButton.textContent = "Загрузка...";

    const formData = new FormData();
    formData.append("imgFile", file);
    formData.append("modelId", selectedModelId);

    try {
        const response = await fetch("/api/v1/galaxy-photos", {
            method: "POST",
            body: formData
        });

        const body = await response.json().catch(() => null);

        if (!response.ok) {
            const message =
                body?.message
                || body?.detail
                || `Ошибка загрузки: HTTP ${response.status}`;

            throw new Error(message);
        }

        const result = body;
        console.log("Задача создана:", result);

        alert("Файл успешно загружен, анализ запущен.");
        modal.close();
    } catch (error) {
        console.error("Ошибка отправки:", error);
        alert(error.message || "Не удалось загрузить файл.");
    } finally {
        sendButton.textContent = "Отправить";
        updateSendButton();
    }
});

updateInputFormat();
updateSendButton();