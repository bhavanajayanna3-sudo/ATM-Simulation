if (sessionStorage.getItem("accountNumber") === null) {
    window.location.href = "index.html";
}
function checkBalance() {

    let account = sessionStorage.getItem("accountNumber");

    fetch("https://atm-simulation-47hn.onrender.com/balance?account=" + account))

    .then(response => response.text())

    .then(message => {
        document.getElementById("message").innerText = message;
    })

    .catch(error => {
        document.getElementById("message").innerText =
            "Unable to connect to Java!";
    });
}
function depositMoney() {

    let account = sessionStorage.getItem("accountNumber");

    let amount = prompt("Enter amount to deposit:");

    if (amount === null || amount === "" || isNaN(amount) || Number(amount) <= 0) {
        alert("Please enter a valid amount!");
        return;
    }

    let details = new URLSearchParams();

    details.append("account", account);
    details.append("amount", amount);

    fetch("https://atm-simulation-47hn.onrender.com/deposit", {
        method: "POST",
        body: details
    })

    .then(response => response.text())

    .then(message => {
        document.getElementById("message").innerText = message;
    })

    .catch(error => {
        document.getElementById("message").innerText =
            "Deposit failed!";
    });
}
function withdrawMoney() {

    let account = sessionStorage.getItem("accountNumber");

    let amount = prompt("Enter amount to withdraw:");

    if (amount === null || amount === "" || isNaN(amount) || Number(amount) <= 0) {
        alert("Enter a valid amount!");
        return;
    }

    let details = new URLSearchParams();

    details.append("account", account);
    details.append("amount", amount);

    fetch("https://atm-simulation-47hn.onrender.com/withdraw", {
        method: "POST",
        body: details
    })

    .then(response => response.text())

    .then(message => {
        document.getElementById("message").innerText = message;
    })

    .catch(error => {
        document.getElementById("message").innerText =
            "Withdrawal failed!";
    });
}
function miniStatement() {

    let account = sessionStorage.getItem("accountNumber");

    fetch("https://atm-simulation-47hn.onrender.com/statement?account=" + account)

    .then(response => response.text())

    .then(message => {
        document.getElementById("message").innerText = message;
    })

    .catch(error => {
        document.getElementById("message").innerText =
            "Unable to connect to Java!";
    });
}
function logout() {
    sessionStorage.removeItem("accountNumber");
    window.location.href = "index.html";
}
