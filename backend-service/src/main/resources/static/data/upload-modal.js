const modal = document.getElementById("uploadModal");
const openButton = document.getElementById("openUpload");
const closeButton = document.getElementById("closeModal");
const fileInput = document.getElementById("dataset");
const sendButton = document.getElementById("sendButton");
const selectedFile = document.getElementById("selectedFile");

let selectedModelId = sessionStorage.getItem("selectedModelVersionId");

// Кнопка активна, только если выбраны и файл, и модель
function updateSendButton() {
    sendButton.disabled = !(fileInput.files.length > 0 && selectedModelId);
}

window.addEventListener("model-selected", (event) => {
    selectedModelId = event.detail.modelId;
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

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }
        const result = await response.json();
        console.log("Задача создана:", result);

        alert("Файл успешно загружен, анализ запущен.");
        modal.close();
    } catch (error) {
        console.error("Ошибка отправки:", error);
        alert("Не удалось загрузить файл. Попробуйте снова.");
    } finally {
        sendButton.textContent = "Отправить";
        updateSendButton();
    }
});

updateSendButton();