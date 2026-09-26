const modelVersionModal = document.getElementById('modelVersionModal');
const openButton = document.getElementById('models'); // ссылка "Модели" в nav
const closeButton = document.getElementById('closeModelVersionModal');

openButton.addEventListener("click", (event) => {
    event.preventDefault();
    modelVersionModal.showModal();
});

closeButton.addEventListener("click", () => {
    modelVersionModal.close();
});

modelVersionModal.addEventListener("click", (event) => {
    if (event.target === modelVersionModal) {
        modelVersionModal.close();
    }
});