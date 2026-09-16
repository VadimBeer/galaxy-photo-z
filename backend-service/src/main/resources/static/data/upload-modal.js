const modal =
    document.getElementById("uploadModal");
const openButton =
    document.getElementById("openUpload");
const closeButton =
    document.getElementById("closeModal");
const fileInput =
    document.getElementById("dataset");
const sendButton =
    document.getElementById("sendButton");
const selectedFile =
    document.getElementById("selectedFile");

/*
 * Открытие modal
 */

openButton.addEventListener("click", (event) => {
    event.preventDefault();
    modal.showModal();
});

/*
 * Закрытие modal
 */

closeButton.addEventListener("click", () => {
    modal.close();
});


/*
 * Выбор файла
 */

fileInput.addEventListener("change", () => {
    if (fileInput.files.length === 0) {
        selectedFile.textContent = "";
        sendButton.disabled = true;
        return;
    }

    const file = fileInput.files[0];
    selectedFile.textContent = `Выбран файл: ${file.name}`;
    sendButton.disabled = false;
});


/*
 * Отправка файла
 */

sendButton.addEventListener("click", async () => {
    const file = fileInput.files[0];
    if (!file) {
        return;
    }

    sendButton.disabled = true;
    sendButton.textContent = "Загрузка...";

    const formData = new FormData();
    formData.append("imgFile", file);

    try {
        const response = await fetch("/api/v1/galaxy-photos", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }
        const result = await response.json();
        console.log("Ответ сервера:", result);

        alert("Файл успешно загружен, анализ запущен.");
        modal.close();
    } catch (error) {
        console.error("Ошибка отправки:", error);
        alert("Не удалось загрузить файл. Попробуйте снова.");
    } finally {
        sendButton.disabled = false;
        sendButton.textContent = "Отправить";
    }

});

