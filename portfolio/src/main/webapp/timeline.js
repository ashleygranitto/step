const events = [
    { date: '', event: ''},
    { date: 'August 2018', event: 'Began freshman year at Cornell University'},
    { date: 'October 2018', event: 'Joined Engineers Without Borders Cornell'},
    { date: 'May 2019 - August 2019', event: 'Worked as a Technology Camp Instructor for TECH CORPS'},
    { date: 'August 2019', event: 'Began working as a course consultant for CS 1110'},
    { date: 'May 2020 - August 2020', event: 'Worked as a STEP Intern at Google'},
    { date: '', event: ''}
];

let currentEvent = 3; 

/** Update the timeline on the "about" page by moving back one event */ 
function displayPastEvent() {  
    // Prevent the user from updating the timeline once they are on the least recent event  
    if (currentEvent > 1) {
        currentEvent--;

        const priorContainer = document.getElementById('prior-event');
        priorContainer.innerText = events[currentEvent-1].event;

        const currentEventContainer = document.getElementById('current-event');
        currentEventContainer.innerText = events[currentEvent].event;

        const currentDateContainer = document.getElementById('current-date');
        currentDateContainer.innerText = events[currentEvent].date;

        const futureContainer = document.getElementById('future-event');
        futureContainer.innerText = events[currentEvent+1].event;
    }
}

/** Update the timeline on the "about" page by moving forward one event */ 
function displayFutureEvent() {
  // Prevent the user from updating the timeline once they are on the most recent event
  if(currentEvent < (events.length - 2)) {
        currentEvent++;

        const priorContainer = document.getElementById('prior-event');
        priorContainer.innerText = events[currentEvent-1].event;

        const currentContainer = document.getElementById('current-event');
        currentContainer.innerText = events[currentEvent].event;

        const currentDateContainer = document.getElementById('current-date');
        currentDateContainer.innerText = events[currentEvent].date;

        const futureContainer = document.getElementById('future-event');
        futureContainer.innerText = events[currentEvent+1].event;
    }
}
