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
    secElement.appendChild(addLoveFeature());  
    container.appendChild(secElement);
  });
}

/** Return a paragraph element that contains a user comment */
function addComment(comment) {
  const pElement = document.createElement('p');
  pElement.innerText = comment; 
  pElement.className = 'user-comment';
  return pElement; 
}

/** Return a div element that contains a love icon and love count */
function addLoveFeature() {
  const divElement = document.createElement('div');
  divElement.className = 'container love-button-container';
  divElement.appendChild(getLoveIcon());
  divElement.appendChild(getLoveCount());
  return divElement; 
}

/** Return an icon element that contains a love icon */
function getLoveIcon() {
  const loveIcon = document.createElement('i'); 
  loveIcon.className = 'love-button far fa-heart';
  return loveIcon;
}

/** Return a paragraph element that contains the amount of likes a comment has recieved */
function getLoveCount() {
  // TO DO--determine using Datastore how many likes to diplay--0 is placeholder for now
  const counter = document.createElement('p');
  counter.innerText = '0';
  return counter; 
}

/** Delete every comment from the feed */
async function deleteComments() {
  // response is not meaningful and is just used to catch the fetch return
  const response = await fetch("/delete-data", { method: 'POST' });
  feed.innerHTML = '';
}
