
const API = "/api/customers";

// Add customer
document.getElementById("customerForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const customer = {
            customerName:
            document.getElementById("customerName").value,

            email:
            document.getElementById("email").value,

            phone:
            document.getElementById("phone").value,

            address:
            document.getElementById("address").value,

            annualIncome:
                Number(document.getElementById("annualIncome").value),

            creditLimit:
                Number(document.getElementById("creditLimit").value)
        };

        try {
            const response = await fetch(API, {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(customer)
            });

            if (!response.ok) {
                throw new Error("Unable to add customer");
            }

            document.getElementById("message").innerText =
                "Customer added successfully!";

            this.reset();

            loadCustomers();

        } catch (error) {

            document.getElementById("message").innerText =
                error.message;
        }
    });


// Display all customers
async function loadCustomers() {

    const response = await fetch(API);

    if (!response.ok) {
        throw new Error("Unable to load customers");
    }

    const customers = await response.json();

    const table = document.getElementById("customerTable");

    table.innerHTML = "";

    customers.forEach(customer => {

        table.innerHTML += `
            <tr>
                <td>${customer.customerId}</td>
                <td>${customer.customerName}</td>
                <td>${customer.email}</td>
                <td>${customer.creditLimit}</td>
            </tr>
        `;

    });
}

loadCustomers();
customers.forEach(customer => {
    const row = document.createElement("tr");

    const values = [
        customer.customerId,
        customer.customerName,
        customer.email,
        customer.phone || "-",
        "₹" + Number(customer.creditLimit || 0)
            .toLocaleString("en-IN"),
        customer.status || "ACTIVE"
    ];

    values.forEach(value => {
        const cell = document.createElement("td");
        cell.textContent = value;
        row.appendChild(cell);
    });

    const actionCell = document.createElement("td");

    const editButton = document.createElement("button");
    editButton.textContent = "Edit";
    editButton.style.cssText =
        "background:#2563eb;color:white;padding:7px;border:0;border-radius:5px;cursor:pointer;";
    editButton.onclick = () => editCustomer(customer);

    const deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.style.cssText =
        "background:#dc2626;color:white;padding:7px;border:0;border-radius:5px;margin-left:5px;cursor:pointer;";
    deleteButton.onclick = () => deleteCustomer(customer.customerId);

    actionCell.append(editButton, deleteButton);
    row.appendChild(actionCell);

    table.appendChild(row);
});
