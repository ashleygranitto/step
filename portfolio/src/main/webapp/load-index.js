/** Set the nametag's HTML to be that fetched from the server */
async function getNametagText() {
  const response = await fetch('/nametag-text');
  const greetings = await response.json();

  // Properly format and place the fetched greetings within the nametag
  const nametag = document.getElementById('nametag-container'); 
  nametag.innerHTML = '';
  formatComments(nametag, greetings);
}

/** Format a list of comments such that each is a new paragraph */
function formatComments(container, comments) {
  comments.forEach((comment) => {
    const pElement = document.createElement('p');
    pElement.innerText = comment; 
    pElement.className = 'nametag';
    container.appendChild(pElement);
  });
}
