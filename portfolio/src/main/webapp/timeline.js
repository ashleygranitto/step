const dates = ['', 'August 2018', 'October 2018', 'May 2019 - August 2019', 'August 2019', 'May 2020 - August 2020', ''];
const events = ['', 'Began freshman year at Cornell University', 'Joined Engineers Without Borders Cornell', 
'Worked as a Technology Camp Instructor for TECH CORPS', 'Began working as a course consultant for CS 1110', 'Worked as a STEP Intern at Google', ''];
var currentEvent = 3; 

// Update the timeline on the "about" page by moving back one event
function displayPastEvent() {  
    // Prevent the user from updating the timeline once they are on the least recent event  
    if(currentEvent > 1) {
        currentEvent--;

        const priorContainer = document.getElementById('prior-event');
        priorContainer.innerText = events[currentEvent-1];

        const currentEventContainer = document.getElementById('current-event');
        currentEventContainer.innerText = events[currentEvent];

        const currentDateContainer = document.getElementById('current-date');
        currentDateContainer.innerText = dates[currentEvent];

        const futureContainer = document.getElementById('future-event');
        futureContainer.innerText = events[currentEvent+1];
    }
}

// Update the timeline on the "about" page by moving forward one event
function displayFutureEvent() {
    // Prevent the user from updating the timeline once they are on the most recent event
  if(currentEvent < (events.length - 2)) {
        currentEvent++;

        const priorContainer = document.getElementById('prior-event');
        priorContainer.innerText = events[currentEvent-1];

        const currentContainer = document.getElementById('current-event');
        currentContainer.innerText = events[currentEvent];

        const currentDateContainer = document.getElementById('current-date');
        currentDateContainer.innerText = dates[currentEvent];

        const futureContainer = document.getElementById('future-event');
        futureContainer.innerText = events[currentEvent+1];
    }
}
