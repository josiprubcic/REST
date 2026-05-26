document
  .getElementById('loginForm')
  .addEventListener('submit', function (event) {
    event.preventDefault();

    const emailInput = document.getElementById('email').value;
    const passwordInput = document.getElementById('password').value;
    const errorElement = document.getElementById('error-message');

    const loginData = {
      email: emailInput,
      lozinka: passwordInput,
    };

    fetch('http://localhost:8080/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(loginData),
    })
      .then((response) => {
        if (response.ok) {
          alert('Uspješna prijava!');
          errorElement.style.display = 'none';
          window.location.href = 'rezervacije.html';

          return response.json();
        } else if (response.status === 401) {
          throw new Error('Pogrešan email ili lozinka.');
        } else {
          throw new Error('Nešto je pošlo po zlu na serveru.');
        }
      })
      .then((korisnik) => {
        console.log('Podaci o ulogiranom korisniku:', korisnik);
      })
      .catch((error) => {
        errorElement.innerText = error.message;
        errorElement.style.display = 'block';
      });
  });
