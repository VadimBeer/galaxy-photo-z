async function login(email, password) {
    const formData = {
        email: email,
        password: password
    };

    const res = await fetch("/api/v1/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(formData)
    });

    if (!res.ok) {
        throw new Error(`Ошибка входа: ${res.status}`);
    }

    return await res.json();
}

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const errorEl = document.getElementById("error");
    errorEl.style.display = "none";

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    try {
        const data = await login(email, password);
        console.log("Успешный вход:", data);


    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    }
});