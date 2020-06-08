const DEFAULT_COMMENT_COUNT = 3;

/** Retrieve and post the default quantity of user comments */
function getComments() {
  postFeed(DEFAULT_COMMENT_COUNT);
}

/** Update the feed to display the quantity of comments the user requested */
function updateComments() {
  postFeed(event.target.value);
}

/** Properly fetch, format, and place the specified quantity of comments within the feed */
async function postFeed(quantity) {
  // Retrieve desired amount of comments
  const response = await fetch('/handle-comment?quantity=' + quantity);
  const comments = await response.json();
  
  // Post comments to the feed
  const feed = document.getElementById('comment-display'); 
  feed.innerHTML = '';
  formatComments(feed, comments);
}

/** Format list of user comments such that each is represented as a section */
function formatComments(container, comments) {
  comments.forEach((comment) => {
    const secElement = document.createElement('section');
    secElement.appendChild(addComment(comment));
    secElement.appendChild(addLoveFeature(comment));  
    container.appendChild(secElement);
  });
}

/** Return a paragraph element that contains a user comment */
function addComment(comment) {
  const pElement = document.createElement('p');
  pElement.innerText = comment.text; 
  pElement.className = 'user-comment';
  return pElement; 
}

/** Return a div element that contains a love icon and love count */
function addLoveFeature(comment) {
  const divElement = document.createElement('div');
  divElement.className = 'container love-button-container';
  divElement.appendChild(getLoveIcon(comment));
  divElement.appendChild(getLoveCount(comment));
  return divElement; 
}

/** Return an icon element that contains a love icon */
function getLoveIcon(comment) {
  const loveIcon = document.createElement('i'); 
  loveIcon.className = 'love-button far fa-heart';
  loveIcon.addEventListener('click', () => {
    updateLike(comment);
  });
  return loveIcon;
}

/** Return a paragraph element that contains the amount of likes a comment has recieved */
function getLoveCount(comment) {
  // TO DO--determine using Datastore how many likes to diplay--0 is placeholder for now
  const counter = document.createElement('p');
  counter.innerText = comment.count;
  return counter; 
}

/** Delete every comment from the feed */
async function deleteComments() {
  // response is not meaningful and is just used to catch the fetch return
  const response = await fetch("/delete-data", { method: 'POST' });
  feed.innerHTML = '';
}

/** Increment a comment's like value by one */
async function updateLike(comment) {
  // response is not meaningful and is just used to catch the fetch return
  const response = await fetch('/update-count?id=' + comment.id + '&count=' + comment.count 
    + '&text=' + comment.text, { method: 'POST' });
}
