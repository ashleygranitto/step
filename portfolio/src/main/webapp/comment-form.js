/** Retrieve and post the default quantity of user comments */
async function getComments() {
  postFeed(3);
}

/** Update the feed to display the quantity of comments the user requested */
async function updateComments(button) {
  postFeed(button.value);
}

/** Properly fetch, format, and place the specified quantity of comments within the feed */
async function postFeed(quantity) {
  // Retrieve desired amount of comments
  const response = await fetch('/handle-comment?value=' + quantity);
  const comments = await response.json();
  
  // Post comments to the feed
  const feed = document.getElementById('comment-display'); 
  feed.innerHTML = '';
  formatComments(feed, comments);
}

/** Format list of user comments such that each is represented as a paragraph */
function formatComments(container, comments) {
  comments.forEach((comment) => {
    const pElement = document.createElement('p');
    pElement.innerText = comment; 
    pElement.className = 'user-comment';
    container.appendChild(pElement);
  });
}
