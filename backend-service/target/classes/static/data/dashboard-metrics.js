function renderMetrics(model) {
    document.getElementById('metric-mae').textContent  = model.mae  != null ? model.mae.toFixed(4)  : '—';
    document.getElementById('metric-mse').textContent  = model.mse  != null ? model.mse.toFixed(4)  : '—';
    document.getElementById('metric-rmse').textContent = model.rmse != null ? model.rmse.toFixed(4) : '—';
    document.getElementById('metric-r2').textContent   = model.r2   != null ? model.r2.toFixed(4)   : '—';
}

async function loadMetricsForModel(modelId) {
    try {
        const response = await fetch(`/api/v1/model-version/${modelId}`);
        if (!response.ok) throw new Error('Ошибка загрузки метрик модели');
        const model = await response.json();
        renderMetrics(model);
    } catch (err) {
        console.error(err);
    }
}

// при загрузке страницы — если модель уже была выбрана раньше
const savedModelId = sessionStorage.getItem('selectedModelVersionId');
if (savedModelId) {
    loadMetricsForModel(savedModelId);
}

// слушаем кастомное событие, которое будет кидать модалка выбора модели
window.addEventListener('model-selected', (event) => {
    loadMetricsForModel(event.detail.modelId);
});