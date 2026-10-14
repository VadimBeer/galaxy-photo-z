const experimentsListEl = document.getElementById('last-experiments');

function renderExperiments(experiments) {
    experimentsListEl.innerHTML = '';

    if (experiments.length === 0) {
        experimentsListEl.textContent = 'Экспериментов пока нет';
        return;
    }

    experiments.forEach(experiment => {
        const item = document.createElement('div');
        item.className = 'experiment';
        item.dataset.experimentId = experiment.id;

        item.innerHTML = `
            <div>
                <div class="experiment-name"></div>
                <div class="experiment-date"></div>
            </div>

            <div class="experiment-score">
                <strong></strong>
            </div>
        `;

        item.querySelector('.experiment-name').textContent =
            experiment.filename;

        item.querySelector('.experiment-date').textContent =
            `Эксперимент №${experiment.id}`;

        item.querySelector('.experiment-score strong').textContent =
            experiment.status;

        experimentsListEl.appendChild(item);
    });
}

async function getExperiments() {
    try {
        const response = await fetch('/api/v1/experiments/all');

        if (!response.ok) {
            throw new Error(`Ошибка загрузки: HTTP ${response.status}`);
        }

        const experiments = await response.json();
        renderExperiments(experiments);
    } catch (error) {
        console.error('Не удалось загрузить эксперименты:', error);
        experimentsListEl.textContent = 'Ошибка загрузки данных';
    }
}

getExperiments();