const modelVersionModal = document.getElementById('modelVersionModal');
const openButton = document.getElementById('models'); // ссылка "Модели" в nav
const closeButton = document.getElementById('closeModelVersionModal');
const modelListEl = document.getElementById('modelList');

let selectedModelId = null;

async function loadModels() {
    modelListEl.innerHTML = '<p>Загрузка...</p>';
    try {
        const response = await fetch('api/v1/model-version/all');
        if (!response.ok) throw new Error('Ошибка загрузки моделей');
        const models = await response.json();
        renderModels(models);
    } catch (err) {
        modelListEl.innerHTML = '<p>Не удалось загрузить список моделей</p>';
        console.error(err);
    }
}

function renderModels(models) {
    modelListEl.innerHTML = '';
    models.forEach(model => {
        const item = document.createElement('div');
        item.className = 'model-item';
        item.dataset.modelId = model.id;
        item.innerHTML = `
                <div class="model-item-name">${model.name} ${model.version}</div>
                `;
        item.addEventListener('click', () => selectModel(model.id, item));
        modelListEl.appendChild(item);
    });
}

function selectModel(id, itemEl) {
    selectedModelId = id;
    document.querySelectorAll('.model-item').forEach(el => el.classList.remove('selected'));
    itemEl.classList.add('selected');
    sessionStorage.setItem('selectedModelVersionId', id);

    window.dispatchEvent(new CustomEvent('model-selected', { detail: { modelId: id } }));
}

openButton.addEventListener("click", (event) => {
    event.preventDefault();
    modelVersionModal.showModal();
    loadModels();
});

closeButton.addEventListener("click", () => {
    modelVersionModal.close();
});

modelVersionModal.addEventListener("click", (event) => {
    if (event.target === modelVersionModal) {
        modelVersionModal.close();
    }
});