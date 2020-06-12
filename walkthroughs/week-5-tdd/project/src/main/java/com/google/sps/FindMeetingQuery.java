// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/** A class to determine appropriate meeting times */
public final class FindMeetingQuery {

  /** Returns a list of time ranges such that a meeting with the specified constraints can be held
  within each time range while still respecting the event calendar */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    long meetingDuration = request.getDuration(); 
    Collection<String> mandatoryMeetingAttendees = request.getAttendees();
    Collection<String> optionalMeetingAttendees = request.getOptionalAttendees();

    // Return no availability if the requested meeting's duration is longer than one day
    if (meetingDuration > (24 * 60)) {
        return Arrays.asList();
    }

    List<TimeRange> mandatoryRanges = determineAvailability(events, mandatoryMeetingAttendees, meetingDuration);
    List<TimeRange> optionalRanges = determineAvailability(events, optionalMeetingAttendees, meetingDuration);
    
    // Return availability based upon the quantity of mandatory verus optional attendees 
    if (mandatoryMeetingAttendees.isEmpty() && optionalMeetingAttendees.isEmpty()) {
        // Return availability as the entire day if there are no mandatory nor optional attendees 
        return Arrays.asList(TimeRange.WHOLE_DAY);
    } else if (optionalMeetingAttendees.isEmpty()) {
        // Return availability of the mandatory attendees if there are no optional attendees 
        return mandatoryRanges; 
    } else if (mandatoryMeetingAttendees.isEmpty()) {
        // Return availability of the optional attendees if there are no mandatory attendees 
        return optionalRanges; 
    } else {
        // Merge mandatory and optional attendees 
        List<String> mergedAttendees = new ArrayList<>(mandatoryMeetingAttendees);
        mergedAttendees.addAll(optionalMeetingAttendees);

        // Return availability of all attendees if possible, otherwise return availability of mandatory attendees
        List<TimeRange> mergedRanges = determineAvailability(events, mergedAttendees, meetingDuration);
        return mergedRanges.isEmpty() ? mandatoryRanges : mergedRanges; 
    }
  }

  // Determine the time ranges for which the meeting attendees can all mutually meet
  private List<TimeRange> determineAvailability(Collection<Event> events, 
    Collection<String> meetingAttendees, long meetingDuration) {

    List<TimeRange> busyTimes = new ArrayList<>();

    // Acquire the time range of every event that shares an attendee with the meeting 
    for (Event event: events) {
      Collection<String> eventAttendees = event.getAttendees();
      if (hasMutualAttendees(eventAttendees, meetingAttendees)) {
        busyTimes.add(event.getWhen()); 
      }
    }

    // Sort these 'busy times' in order of ascending start time 
    Collections.sort(busyTimes, TimeRange.ORDER_BY_START);

    // Obtain the inverse of the 'busy times' (i.e. aquire the 'free times')
    List<TimeRange> freeTimes = inversify(busyTimes, meetingDuration);

    // Sort and return the 'free times' in order of ascending start time 
    Collections.sort(freeTimes, TimeRange.ORDER_BY_START);
    return freeTimes;
  }

  // Return true if an event and a meeting share an attendee, false otherwise
  private boolean hasMutualAttendees(Collection<String> eventAttendees, Collection<String> meetingAttendees) {
    for (String eventAttendee: eventAttendees) {
       if (meetingAttendees.contains(eventAttendee)) {
           return true; 
       }
    }
    return false;
  } 

  // Return the inverse of a schedule such that each returned range is an acceptable time to host a meeting
  private List<TimeRange> inversify(Collection<TimeRange> busyTimes, long duration) {
    List<TimeRange> freeTimes = new ArrayList();
    int startRange = TimeRange.START_OF_DAY; 
    int prevEnd = TimeRange.START_OF_DAY; 

    // Add a range between event blocks given that timing and scheduling permits
    for (TimeRange busyTime: busyTimes) {
       int eventStart = busyTime.start();
       int eventEnd = busyTime.end(); 
       // Ensure proper handling of nested events
       if (eventEnd >= startRange) {
           // Ensure a range is at least as long as the duration of the meeting
           if (duration <= eventStart - startRange) { 
               freeTimes.add(TimeRange.fromStartEnd(startRange, eventStart-1, true));
           }
           startRange = busyTime.end();
       }
    }

    // Add an additional range between the final event's end and the end of the day if time permits 
    if (startRange < TimeRange.END_OF_DAY && duration < TimeRange.END_OF_DAY - startRange) {
      freeTimes.add(TimeRange.fromStartEnd(startRange, TimeRange.END_OF_DAY, true));
    }

    return freeTimes; 
  }
}
