
document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("registerForm");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        const message = document.getElementById("message");
        const button = document.getElementById("registerButton");

        button.disabled = true;
        button.textContent = "Registering...";

        try {
            const response = await fetch("/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name,
                    email,
                    password
                })
            });

            const result = await response.json();

            if (response.ok) {
                message.style.color = "green";
                message.textContent =
                    "Registration successful! Please login.";

                form.reset();

            } else {
                message.style.color = "red";
                message.textContent =
                    result.message || "Registration failed.";
            }

        } catch (error) {
            message.style.color = "red";
            message.textContent = "Server connection failed.";
            console.error(error);

        } finally {
            button.disabled = false;
            button.textContent = "Register";
        }
    });
});
