package calendar.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Represents a calendar containing events. */
public class Calendar {
  private List<Event> events = new ArrayList<>();

  /** Adds an event to the calendar, checking conflicts if autoDecline is true. */
  public void addEvent(Event event, boolean autoDecline) throws Exception {
    if (autoDecline) {
      for (Event e : events) {
        if (conflict(e, event)) {
          throw new Exception("Event conflict detected.");
        }
      }
    }
    events.add(event);
  }

  /** Returns the list of events on a given date. */
  public List<Event> getEventsOn(LocalDate date) {
    List<Event> result = new ArrayList<>();
    for (Event e : events) {
      if (e.getStart().toLocalDate().equals(date)) {
        result.add(e);
      }
    }
    return result;
  }

  /** Returns the list of events between the given start and end date-times. */
  public List<Event> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    List<Event> result = new ArrayList<>();
    for (Event e : events) {
      if (!e.getStart().isAfter(end) && !e.getEnd().isBefore(start)) {
        result.add(e);
      }
    }
    return result;
  }

  /** Returns true if an event covers the given date-time. */
  public boolean isBusy(LocalDateTime dateTime) {
    for (Event e : events) {
      if (!dateTime.isBefore(e.getStart()) && !dateTime.isAfter(e.getEnd())) {
        return true;
      }
    }
    return false;
  }

  /** Checks if two events conflict. */
  private boolean conflict(Event e1, Event e2) {
    return !(e2.getEnd().isBefore(e1.getStart()) || e2.getStart().isAfter(e1.getEnd()));
  }

  /** Edits a single event matching name and start/end times. */
  public boolean editSingleEvent(
      String property, String name, LocalDateTime start, LocalDateTime end, String newValue) {
    for (Event event : events) {
      if (event.getName().equals(name)
          && event.getStart().equals(start)
          && event.getEnd().equals(end)) {
        updateProperty((AbstractCalendarEvent) event, property, newValue);
        return true;
      }
    }
    return false;
  }

  /** Edits events with the given name and start time. */
  public int editEventsFrom(String property, String name, LocalDateTime start, String newValue) {
    int count = 0;
    for (Event event : events) {
      if (event.getName().equals(name) && event.getStart().equals(start)) {
        updateProperty((AbstractCalendarEvent) event, property, newValue);
        count++;
      }
    }
    return count;
  }

  /** Edits all events with the given name. */
  public int editEvents(String property, String name, String newValue) {
    int count = 0;
    for (Event event : events) {
      if (event.getName().equals(name)) {
        updateProperty((AbstractCalendarEvent) event, property, newValue);
        count++;
      }
    }
    return count;
  }

  /** Updates an event property based on a string identifier. */
  private void updateProperty(AbstractCalendarEvent event, String property, String newValue) {
    switch (property.toLowerCase()) {
      case "name":
        event.setName(newValue);
        break;
      case "description":
        event.setDescription(newValue);
        break;
      case "location":
        event.setLocation(newValue);
        break;
      case "ispublic":
        event.setIsPublic(Boolean.parseBoolean(newValue));
        break;
      default:
        break;
    }
  }

  /** Exports the calendar to a CSV file formatted for Google Calendar. */
  public String exportToCSV(String fileName) throws IOException {
    String absPath = Paths.get(fileName).toAbsolutePath().toString();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      writer.write(
          "Subject,Start Date,Start Time,End Date,End Time,"
              + "All Day Event,Description,Location,Private");
      writer.newLine();
      for (Event event : events) {
        String subject = event.getName();
        String startDate = dateFormatter.format(event.getStart());
        String endDate = dateFormatter.format(event.getEnd());
        String startTime = timeFormatter.format(event.getStart());
        String endTime = timeFormatter.format(event.getEnd());
        boolean isAllDay =
            (event.getStart().getHour() == 0
                && event.getStart().getMinute() == 0
                && event.getEnd().getHour() == 23
                && event.getEnd().getMinute() == 59);
        String allDay = isAllDay ? "True" : "False";
        if (isAllDay) {
          startTime = "";
          endTime = "";
        }
        String isPrivate = (!event.isPublic()) ? "True" : "False";
        String description = event.getDescription();
        String location = event.getLocation();
        writer.write(
            String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s",
                subject,
                startDate,
                startTime,
                endDate,
                endTime,
                allDay,
                description,
                location,
                isPrivate));
        writer.newLine();
      }
    }
    return absPath;
  }
}
