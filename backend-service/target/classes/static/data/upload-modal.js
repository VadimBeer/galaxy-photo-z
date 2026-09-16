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

    sendButton.addEventListener("click", () => {
        const file =
            fileInput.files[0];

        if (!file) {
            return;
        }

        console.log(
            "Отправляем:",
            file.name
        );

    });