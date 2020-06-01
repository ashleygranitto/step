// Set the nametag's HTML to be that fetched from the server
async function getNametagText() {
  const response = await fetch('/nametag-text');
  const greeting = await response.text();
  document.getElementById('nametag').innerHTML = greeting;
}
