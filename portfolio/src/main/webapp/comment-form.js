const DEFAULT_COMMENT_COUNT = 3;

/** Update the page upon page load */
function init() {
  checkLogin();
  getComments(); 
  setFormAction();
}

/** Ensure the comment form is only displayed if the user is logged in */
async function checkLogin() { 
  const response = await fetch('/login-status');
  const status = await response.json();

  if (status.isLoggedIn) { 
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('comment-form').style.display = 'flex';
  } else {
    document.getElementById('comment-form').style.display = 'none';
    document.getElementById('login-form').style.display = 'flex';
  }  
}

/** Retrieve and post the default quantity of user comments */
function getComments() {
  postFeed(DEFAULT_COMMENT_COUNT);
}

/** Set the action of the comment form to be the Blobstore URL */
async function setFormAction() {
  const response = await fetch('/blobstore-link');
  const url = await response.text();

  const commentForm = document.getElementById('visible-form');
  commentForm.action = url; 
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
  const secElement = document.createElement('section');
  secElement.appendChild(addCommentText(comment));
  secElement.appendChild(addCommentImage(comment));  
  return secElement; 
}

/** Return a paragraph element that contains a user comment */
function addCommentText(comment) {
  const pElement = document.createElement('p');
  pElement.innerText = comment.email + ': ' + comment.text; 
  pElement.className = 'user-comment';
  return pElement; 
}

/** Return a paragraph element that contains a user comment */
function addCommentImage(comment) {
  const imgElement = document.createElement('img');
  imgElement.src = comment.url;
  imgElement.className = 'comment-image';
  return imgElement; 
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
  const counter = document.createElement('p');
  counter.innerText = comment.count;
  return counter; 
}

/** Delete every comment from the feed */
async function deleteComments() {
  await fetch("/delete-data", { method: 'POST' });
  feed.innerHTML = '';
}

/** Increment a comment's like value by one */
async function updateLike(comment) {
  await fetch(`/update-count?id=${comment.id}&count=${comment.count}` +
    `&text=${comment.text}&email=${comment.email}`, { method: 'POST' });
}
