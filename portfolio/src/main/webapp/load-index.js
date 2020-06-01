// Set the nametag's HTML to be that fetched from the server
async function getNametagText() {
  const response = await fetch('/nametag-text');
  const greetings = await response.json();

  // Properly format and place the fetched greetings within the nametag
  const nametag = document.getElementById('nametag'); 
  nametag.innerHTML = formatComments(greetings);
}

// Format a list of comments such that their is a newline between each
function formatComments (comments) {
  master = '';
  comments.forEach((comment) => {
    master += comment + '</br>'
  });
  return master; 
}
