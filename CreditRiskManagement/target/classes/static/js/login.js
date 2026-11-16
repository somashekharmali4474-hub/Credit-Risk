
document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("loginForm");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const message = document.getElementById("message");
        const button = document.getElementById("loginButton");

        message.textContent = "";

        button.disabled = true;
        button.textContent = "Logging in...";

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password })
            });

            const result = await response.json();

            if (response.ok) {
                message.style.color = "green";
                message.textContent = "Login successful!";

                window.location.href = "/dashboard.html";
            } else {
                message.style.color = "red";
                message.textContent =
                    result.message || "Login failed";
            }

        } catch (error) {
            message.style.color = "red";
            message.textContent =
                "Unable to connect to the server.";
            console.error(error);

        } finally {
            button.disabled = false;
            button.textContent = "Login";
        }
    });
});
