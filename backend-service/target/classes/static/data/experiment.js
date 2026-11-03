const experimentEl = document.getElementById('experiment');
const contentEl = document.getElementById('experiment-content');
const messageEl = document.getElementById('experiment-message');
const refreshButton = document.getElementById('refresh-experiment');

function setText(id, value) {
    document.getElementById(id).textContent = value ?? '—';
}

function formatNumber(value, digits = 4) {
    return Number.isFinite(value) ? value.toFixed(digits) : '—';
}

function showImage(imageId, messageId, url) {
    const image = document.getElementById(imageId);
    const message = document.getElementById(messageId);

    image.onload = null;
    image.onerror = null;
    image.hidden = true;
    image.removeAttribute('src');
    message.hidden = false;

    if (!url) {
        message.textContent = 'Изображение пока недоступно.';
        return;
    }

    message.textContent = 'Загрузка изображения…';

    image.onload = () => {
        image.hidden = false;
        message.hidden = true;
    };

    image.onerror = () => {
        image.hidden = true;
        message.hidden = false;
        message.textContent =
            'Не удалось загрузить изображение. Нажмите «Обновить».';
    };

    image.src = url;
}

function renderInputFile(experiment) {
    const image = document.getElementById('experiment-photo');
    const message = document.getElementById('photo-message');
    const link = document.getElementById('input-file-link');

    // Сбрасываем предыдущее состояние при обновлении страницы.
    image.onload = null;
    image.onerror = null;
    image.hidden = true;
    image.removeAttribute('src');

    message.hidden = false;
    message.textContent = '';

    link.hidden = true;
    link.removeAttribute('href');

    setText('input-filename', experiment.galaxyImgFilename);

    const contentType = (
        experiment.inputContentType ?? ''
    ).split(';')[0].trim().toLowerCase();

    const sourceUrl = experiment.galaxyImgUrl;

    if (sourceUrl) {
        link.href = sourceUrl;
        link.hidden = false;
    }

    const imageTypes = new Set([
        'image/jpeg',
        'image/png'
    ]);

    const csvTypes = new Set([
        'text/csv',
        'application/csv',
        'text/plain',
        'application/vnd.ms-excel'
    ]);

    if (imageTypes.has(contentType)) {
        showImage(
            'experiment-photo',
            'photo-message',
            sourceUrl
        );

    } else if (csvTypes.has(contentType)) {
        message.textContent =
            'Исходный CSV с табличными признаками объекта. '
            + 'Файл можно открыть по ссылке ниже.';

    } else {
        message.textContent =
            'Предварительный просмотр этого формата недоступен. '
            + 'Исходный файл можно открыть по ссылке ниже.';
    }
}

function renderTaskState(experiment) {
    const labels = {
        PENDING: 'Ожидает обработки',
        PROCESSING: 'Обрабатывается',
        COMPLETED: 'Завершён',
        FAILED: 'Ошибка обработки'
    };

    setText(
        'experiment-state',
        labels[experiment.state] ?? 'Статус неизвестен'
    );

    const errorEl = document.getElementById('experiment-error');
    const failed = experiment.state === 'FAILED';

    errorEl.textContent = failed
        ? (
            experiment.errorMessage
            || 'Обработка завершилась ошибкой без описания причины.'
        )
        : '';

    errorEl.hidden = !failed;
}

function renderResultChart(experiment) {
    const completed = experiment.state === 'COMPLETED';

    const chartAvailable = completed
        && experiment.chartId != null
        && Boolean(experiment.chartUrl);

    document.getElementById('download-experiment').disabled =
        !chartAvailable;

    showImage(
        'experiment-chart',
        'chart-message',
        chartAvailable ? experiment.chartUrl : null
    );

    if (chartAvailable) {
        return;
    }

    const message = document.getElementById('chart-message');

    switch (experiment.state) {
        case 'PENDING':
        case 'PROCESSING':
            message.textContent =
                'Результат ещё не готов. Нажмите «Обновить» позже.';
            break;

        case 'FAILED':
            message.textContent =
                'График не создан из-за ошибки обработки.';
            break;

        case 'COMPLETED':
            message.textContent =
                'Обработка завершена, но график недоступен.';
            break;

        default:
            message.textContent = 'График пока недоступен.';
    }
}

function renderExperiment(experiment) {
    renderTaskState(experiment);
    const title = `Эксперимент №${experiment.taskId}`;

    setText('experiment-title', title);
    document.title = `${title} — AstroML`;

    setText('experiment-filename', experiment.galaxyImgFilename);
    setText('experiment-photo-id', experiment.galaxyImgId);
    setText('experiment-chart-id', experiment.chartId);
    setText('experiment-chart-type', experiment.chartContentType);
    setText('experiment-median', formatNumber(experiment.median));

    const model = [
        experiment.modelName,
        experiment.modelVersion
    ].filter(value => value != null && value !== '').join(' ');

    setText('experiment-model', model || '—');

    // createdAt приходит из Java как LocalDateTime.
    setText(
        'experiment-created',
        typeof experiment.createdAt === 'string'
            ? experiment.createdAt.replace('T', ' ').slice(0, 19)
            : '—'
    );

    renderInputFile(experiment);

    renderResultChart(experiment);

    const quantiles = Array.isArray(experiment.quantiles)
        ? experiment.quantiles
        : [];

    const tbody = document.getElementById('quantile-rows');
    tbody.replaceChildren();

    document.getElementById('quantiles-empty').hidden =
        quantiles.length > 0;

    document.getElementById('quantiles-table-wrapper').hidden =
        quantiles.length === 0;

    [...quantiles]
        .sort((a, b) => a.tau - b.tau)
        .forEach(point => {
            const row = document.createElement('tr');

            const tauCell = document.createElement('td');
            tauCell.textContent = formatNumber(point.tau, 2);

            const valueCell = document.createElement('td');
            valueCell.textContent = formatNumber(point.value);

            row.append(tauCell, valueCell);
            tbody.appendChild(row);
        });
}

async function getExperiment() {
    document.getElementById('download-experiment').disabled = true;
    contentEl.hidden = true;
    messageEl.hidden = false;
    messageEl.textContent = 'Загрузка эксперимента…';
    refreshButton.disabled = true;
    experimentEl.setAttribute('aria-busy', 'true');

    try {
        const id = experimentEl.dataset.taskId;

        if (!id || !/^[1-9]\d*$/.test(id)) {
            throw new Error('Не удалось определить ID эксперимента.');
        }

        const response = await fetch(
            `/api/v1/experiments/${encodeURIComponent(id)}`
        );

        if (response.status === 401) {
            throw new Error('Для просмотра эксперимента войдите в аккаунт.');
        }

        if (response.status === 403) {
            throw new Error('У вас нет доступа к этому эксперименту.');
        }

        if (response.status === 404) {
            throw new Error('Эксперимент не найден.');
        }

        if (!response.ok) {
            throw new Error(`Ошибка загрузки: HTTP ${response.status}`);
        }

        const experiment = await response.json();

        if (!experiment || experiment.taskId == null) {
            throw new Error('Сервер вернул некорректные данные эксперимента.');
        }

        renderExperiment(experiment);

        contentEl.hidden = false;
        messageEl.hidden = true;
    } catch (error) {
        console.error('Не удалось загрузить эксперимент:', error);
        messageEl.textContent = error.message || 'Ошибка загрузки данных';
    } finally {
        refreshButton.disabled = false;
        experimentEl.setAttribute('aria-busy', 'false');
    }
}

refreshButton.addEventListener('click', getExperiment);

getExperiment();