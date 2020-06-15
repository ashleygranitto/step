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

import com.google.common.collect.ImmutableList; 
import com.google.common.collect.ImmutableSet; 
import com.google.common.collect.Sets; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set; 
import java.util.stream.Collectors; 

/** A class to determine appropriate meeting times */
public final class FindMeetingQuery {

  private static final int LENGTH_OF_DAY = 24 * 60; 

  /** 
  * Returns a list of time ranges such that a meeting with the specified constraints can be held
  * within each time range while still respecting the event calendar 
  */
  public static Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    long meetingDuration = request.getDuration(); 
    Collection<String> mandatoryMeetingAttendees = request.getAttendees();
    Collection<String> optionalMeetingAttendees = request.getOptionalAttendees();

    // Return no availability if the requested meeting's duration is longer than one day
    if (meetingDuration > (LENGTH_OF_DAY)) {
        return Arrays.asList();
    }

    ImmutableList<TimeRange> mandatoryRanges = determineAvailability(events, mandatoryMeetingAttendees, meetingDuration);
    ImmutableList<TimeRange> optionalRanges = determineAvailability(events, optionalMeetingAttendees, meetingDuration);
    
    // Determine if availability is dependent upon solely mandatory verus optional attendees 
    if (mandatoryMeetingAttendees.isEmpty() && optionalMeetingAttendees.isEmpty()) {
        // Return availability as the entire day if there are no mandatory nor optional attendees 
        return Arrays.asList(TimeRange.WHOLE_DAY);
    } if (!mandatoryMeetingAttendees.isEmpty() && optionalMeetingAttendees.isEmpty()) {
        return mandatoryRanges; 
    } if (mandatoryMeetingAttendees.isEmpty() && !optionalMeetingAttendees.isEmpty()) {
        return optionalRanges; 
    } 
        
    // Merge mandatory and optional attendees 
    List<String> mergedAttendees = new ArrayList<>(mandatoryMeetingAttendees);
    mergedAttendees.addAll(optionalMeetingAttendees);

    // Return availability of all attendees if possible, otherwise return availability of mandatory attendees
    ImmutableList<TimeRange> mergedRanges = determineAvailability(events, mergedAttendees, meetingDuration);
    return mergedRanges.isEmpty() ? mandatoryRanges : mergedRanges; 
  }

  // Determine the time ranges for which the meeting attendees can all mutually meet
  private static ImmutableList<TimeRange> determineAvailability(Collection<Event> events, 
    Collection<String> meetingAttendees, long meetingDuration) {

    // Obtain a list of busy time ranges such that a meeting cannot be held during these ranges
    List<TimeRange> busyTimes = events.stream() 
    // Remove events that do not have mutual attendees with the meeting
    .filter(event -> hasMutualAttendees(event.getAttendees(), meetingAttendees))
    // Obtain the time range of each relevant event
    .map(Event::getWhen)
    // Sort the time ranges in order of increasing start time
    .sorted(TimeRange.ORDER_BY_START)
    .collect(Collectors.toList());

    // Obtain the inverse of the 'busy times' (i.e. aquire the 'free times')
    ImmutableList<TimeRange> freeTimes = inversify(busyTimes, meetingDuration);

    // Sort and return the 'free times' in order of ascending start time 
    ImmutableList<TimeRange> freeTimesSorted = ImmutableList.sortedCopyOf(TimeRange.ORDER_BY_START, freeTimes);
    return freeTimesSorted; 
  }

  // Return true if an event and a meeting share an attendee, false otherwise
  private static boolean hasMutualAttendees(Collection<String> eventAttendees, Collection<String> meetingAttendees) {
    ImmutableSet<String> eventSet = ImmutableSet.copyOf(eventAttendees);
    ImmutableSet<String> meetingSet = ImmutableSet.copyOf(meetingAttendees);
    
    return !Sets.intersection(eventSet, meetingSet).isEmpty(); 
  } 

  // Return the inverse of a schedule such that each returned range is an acceptable time to host a meeting
  private static ImmutableList<TimeRange> inversify(Collection<TimeRange> busyTimes, long duration) {
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

    return ImmutableList.copyOf(freeTimes);
  }
}
