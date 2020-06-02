// Retrieve and post user comments
async function getComments() {
  const response = await fetch('/handle-comment');
  const comments = await response.json();

  // Properly format and place the fetched comments within the feed
  const feed = document.getElementById('comment-display'); 
  feed.innerHTML = '';
  formatComments(feed, comments);
}

// Format list of user comments such that each is represented as a paragraph
function formatComments(container, comments) {
  comments.forEach((comment) => {
    const pElement = document.createElement('p');
    pElement.innerText = comment; 
    pElement.className = 'user-comment';
    container.appendChild(pElement);
  });
}
