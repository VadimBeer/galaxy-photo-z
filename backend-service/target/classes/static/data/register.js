document.getElementById('registerForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const email = document.getElementById('email').value.trim();
    const username = document.querySelector('input[name="username"]').value.trim();
    const password = document.querySelector('input[name="password"]').value;
    const confirm = document.querySelector('input[name="confirm"]').value;

    // Валидация
    if (!email || !username || !password || !confirm) {
        alert('Пожалуйста, заполните все поля.');
        return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
        alert('Введите корректный email.');
        return;
    }

    if (password.length < 6) {
        alert('Пароль должен содержать не менее 6 символов.');
        return;
    }

    if (password !== confirm) {
        alert('Пароли не совпадают.');
        return;
    }

    // Формируем объект для отправки
    const formData = {
        email: email,
        username: username,
        password: password
    };

    // Отправка данных на сервер
    fetch('/api/v1/registration', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка регистрации');
            }
            return response.json();
        })
        .then(data => {
            console.log('Успешная регистрация:', data);
            alert('Регистрация прошла успешно!');
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Не удалось зарегистрироваться. Попробуйте снова.');
        });
});