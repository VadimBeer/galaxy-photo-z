const downloadButton = document.getElementById('download-experiment');

async function downloadChart() {
    const currentChartId = document
        .getElementById('experiment-chart-id')
        .textContent.trim();

    if (!currentChartId || currentChartId === '—') {
        console.error('ID графика не задан');
        return;
    }

    downloadButton.disabled = true;

    try {
        const response = await fetch(
            `/api/v1/experiments/download/chart/${currentChartId}`
        );

        if (!response.ok) {
            throw new Error(`Ошибка скачивания: ${response.status}`);
        }

        const blob = await response.blob();
        const url = URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.download = `chart_${currentChartId}.png`;

        document.body.appendChild(link);
        link.click();
        link.remove();

        setTimeout(() => URL.revokeObjectURL(url), 1000);

    } catch (error) {
        console.error(error);

        const message = document.getElementById('experiment-message');
        message.textContent = 'Не удалось скачать график';
        message.hidden = false;
    } finally {
        downloadButton.disabled = false;
    }
}

downloadButton.addEventListener('click', downloadChart);