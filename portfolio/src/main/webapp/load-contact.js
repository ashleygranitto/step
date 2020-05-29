const businessAccounts = [
    { link: 'https://www.linkedin.com/in/ashley-granitto', icon: 'fab fa-linkedin-in', username: 'ashley-granitto'},
    { link: 'mailto:aeg249@cornell.edu', icon: 'far fa-envelope', username: 'aeg249@cornell.edu'},
    { link: 'https://github.com/ashleygranitto', icon: 'fab fa-github', username: '@ashleygranitto'},
];

const personalAccounts = [
    { link: 'https://www.instagram.com/ashleygranitto/?hl=en', icon: 'fab fa-instagram', username: '@ashleygranitto'},
    { link: 'mailto:granittoashley@gmail.com', icon: 'far fa-envelope', username: 'granittoashley@gmail.com'},
    { link: 'https://www.youtube.com/channel/UCUWdAT3O2K4gJGyqkkBGxOA', icon: 'fab fa-youtube', username: 'Ashley Granitto'},
];

let current = 'business';

// Switch from the business tab to the personal tab 
function businessToPersonal() { 
    if (current === 'business') { 
        document.getElementById('business-body').style.display = 'none';
        document.getElementById('personal-body').style.display = 'flex';
    }
    current = 'personal';
}

// Switch from the business tab to the personal tab 
function personalToBusiness() {  
    if (current === 'personal') { 
        document.getElementById('business-body').style.display = 'flex';
        document.getElementById('personal-body').style.display = 'none';
    }
    current = 'business';
}

// Append contact information to the business body
function createBusinessBody() {
    businessAccounts.forEach(function(account) {
        document.getElementById('business-body').innerHTML +=  
              '<article class="account-container"> \n' +
              '<a class="account-icon" href="' + account.link + '">' +
              '<i class="' + account.icon + '"></i></a> \n' +
              '<p class="account-username">' + account.username + '</p> \n' +
              '</article>'
    }); 
}

// Append contact information to the personal body
function createPersonalBody() {
    personalAccounts.forEach(function(account) {
        document.getElementById('personal-body').innerHTML +=  
              '<article class="account-container"> \n' +
              '<p class="account-username">' + account.username + '</p> \n' +
              '<a class="account-icon" href="' + account.link + '">' +
              '<i class="' + account.icon + '"></i></a> \n' +
              '</article>'
    }); 
}
