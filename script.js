document.getElementById("loginForm").addEventListener("submit", function(event) {

    event.preventDefault();

    let account = document.getElementById("accountNumber").value;
    let pin = document.getElementById("pin").value;

    let loginDetails = new URLSearchParams();

    loginDetails.append("account", account);
    loginDetails.append("pin", pin);

    fetch(https://atm-simulation-47hn.onrender.com, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: loginDetails.toString()
    })

    .then(response => response.text())

    .then(message => {

    document.getElementById("message").innerText = message;

    if (message.startsWith("Login Successful!")) {
        sessionStorage.setItem("accountNumber", account);
        window.location.href = "dashboard.html";
    }

})
    

    .catch(error => {
        document.getElementById("message").innerText =
            "Unable to connect to Java server!";
    });

});
