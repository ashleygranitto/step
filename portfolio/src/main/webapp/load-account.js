/** Populate the account page withadsfadsf */
async function checkLogin() { 
  const response = await fetch('/login');
  const body = await response.text();

  document.getElementById('login-text').innerHTML = body;
}
