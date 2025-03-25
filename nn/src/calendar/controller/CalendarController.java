package calendar.controller;

import calendar.model.Calendar;
import calendar.model.Event;
import calendar.model.SingleEvent;
import calendar.model.RecurringEvent;
import calendar.util.CommandProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Controller for calendar operations and command processing. */
public class CalendarController {
  private Calendar calendar;
  private DateTimeFormatter dtFormatter;
  private DateTimeFormatter dateFormatter;
  private DateTimeFormatter timeFormatter;

  /** Constructs a CalendarController with the specified calendar. */
  public CalendarController(Calendar calendar) {
    this.calendar = calendar;
    dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
  }

  /** Creates a single timed event. */
  public void createSingleEvent(
      String name,
      String startStr,
      String endStr,
      String description,
      String location,
      boolean isPublic,
      boolean autoDecline)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    SingleEvent event = new SingleEvent(name, start, end, description, location, isPublic);
    calendar.addEvent(event, autoDecline);
  }

  /** Creates a recurring timed event with a fixed number of occurrences. */
  public void createRecurringEventOccurrences(
      String name,
      String startStr,
      String endStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      int occurrences,
      boolean autoDecline)
      throws Exception {
    if (occurrences < 0) {
      throw new IllegalArgumentException("Occurrence count cannot be negative.");
    }
    if (occurrences == 0) {
      return;
    }
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(
            name, start, end, description, location, isPublic, weekdays, occurrences);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      calendar.addEvent(instance, autoDecline);
    }
  }

  /** Creates a recurring timed event until a specified date-time. */
  public void createRecurringEventUntil(
      String name,
      String startStr,
      String endStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      String untilStr,
      boolean autoDecline)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    LocalDateTime until = LocalDateTime.parse(untilStr, dtFormatter);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(name, start, end, description, location, isPublic, weekdays, until);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      calendar.addEvent(instance, autoDecline);
    }
  }

  /** Creates a single all-day event. */
  public void createSingleAllDayEvent(
      String name,
      String dateStr,
      String description,
      String location,
      boolean isPublic,
      boolean autoDecline)
      throws Exception {
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    LocalDateTime start = date.atTime(0, 0);
    LocalDateTime end = date.atTime(23, 59);
    SingleEvent event = new SingleEvent(name, start, end, description, location, isPublic);
    calendar.addEvent(event, autoDecline);
  }

  /** Creates a recurring all-day event with a fixed number of occurrences. */
  public void createRecurringAllDayEventOccurrences(
      String name,
      String dateStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      int occurrences,
      boolean autoDecline)
      throws Exception {
    if (occurrences < 0) {
      throw new IllegalArgumentException("Occurrence count cannot be negative.");
    }
    if (occurrences == 0) {
      return;
    }
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    LocalDateTime start = date.atTime(0, 0);
    LocalDateTime end = date.atTime(23, 59);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(
            name, start, end, description, location, isPublic, weekdays, occurrences);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      calendar.addEvent(instance, autoDecline);
    }
  }

  /** Creates a recurring all-day event until a specified date. */
  public void createRecurringAllDayEventUntil(
      String name,
      String dateStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      String untilDateStr,
      boolean autoDecline)
      throws Exception {
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    LocalDateTime start = date.atTime(0, 0);
    LocalDateTime end = date.atTime(23, 59);
    LocalDate untilDate = LocalDate.parse(untilDateStr, dateFormatter);
    LocalDateTime until = untilDate.atTime(23, 59);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(name, start, end, description, location, isPublic, weekdays, until);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      calendar.addEvent(instance, autoDecline);
    }
  }

  /** Edits a single event identified by name and start/end times. */
  public void editSingleEvent(
      String property, String eventName, String startStr, String endStr, String newValue)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    boolean found = calendar.editSingleEvent(property, eventName, start, end, newValue);
    if (!found) {
      throw new Exception("No matching event found for editing.");
    }
  }

  /** Edits events matching the given name and start time. */
  public void editEventsFrom(String property, String eventName, String startStr, String newValue)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    int count = calendar.editEventsFrom(property, eventName, start, newValue);
    if (count == 0) {
      throw new Exception("No matching events found");
    }
  }

  /** Edits all events with the given name. */
  public void editEvents(String property, String eventName, String newValue) throws Exception {
    int count = calendar.editEvents(property, eventName, newValue);
    if (count == 0) {
      throw new Exception("No matching events found");
    }
  }

  /** Returns formatted events on the specified date. */
  public String getFormattedEventsOn(String dateStr) {
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    List<Event> events = calendar.getEventsOn(date);
    if (events.isEmpty()) {
      return "No events on " + dateStr;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("Events on ").append(dateStr).append(":\n");
    for (Event e : events) {
      boolean isAllDay =
          (e.getStart().getHour() == 0
              && e.getStart().getMinute() == 0
              && e.getEnd().getHour() == 23
              && e.getEnd().getMinute() == 59);
      sb.append(" - ").append(e.getName());
      if (isAllDay) {
        sb.append(" All Day Event ");
      } else {
        sb.append(" (")
            .append(timeFormatter.format(e.getStart()))
            .append(" to ")
            .append(timeFormatter.format(e.getEnd()))
            .append(")");
      }
      sb.append(" at ").append(e.getLocation()).append("\n");
    }
    return sb.toString();
  }

  /** Returns formatted events between two date-times. */
  public String getFormattedEventsBetween(String startStr, String endStr) {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    List<Event> eventsBetween = calendar.getEventsBetween(start, end);
    if (eventsBetween.isEmpty()) {
      return "No events between " + startStr + " and " + endStr;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("Events from ").append(startStr).append(" to ").append(endStr).append(":\n");
    for (Event e : eventsBetween) {
      sb.append(" - ")
          .append(e.getName())
          .append(" (")
          .append(timeFormatter.format(e.getStart()))
          .append(" to ")
          .append(timeFormatter.format(e.getEnd()))
          .append(") at ")
          .append(e.getLocation())
          .append("\n");
    }
    return sb.toString();
  }

  /** Returns the busy status for the specified date-time. */
  public String getBusyStatus(String dateTimeStr) {
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dtFormatter);
    boolean busy = calendar.isBusy(dateTime);
    return "Status at " + dateTimeStr + ": " + (busy ? "Busy" : "Available");
  }

  /** Exports the calendar to CSV and returns its absolute path. */
  public String exportCalendar(String fileName) throws Exception {
    return calendar.exportToCSV(fileName);
  }

  /** Parses a string of weekdays into a Set of DayOfWeek. */
  private Set<DayOfWeek> parseWeekdays(String weekdaysStr) {
    Set<DayOfWeek> weekdays = new HashSet<>();
    for (char c : weekdaysStr.toCharArray()) {
      switch (c) {
        case 'M':
          weekdays.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          weekdays.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          weekdays.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          weekdays.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          weekdays.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          weekdays.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          weekdays.add(DayOfWeek.SUNDAY);
          break;
        default:
          break;
      }
    }
    return weekdays;
  }

  /** Runs the command processing loop. */
  public void run() {
    ConsoleView view = new ConsoleView();
    while (true) {
      view.display("Choose mode: 1 for Interactive, 2 for Headless and 3 to Exit");
      String mode = view.getInput();
      switch (mode) {
        case "1":
          view.display("Interactive mode.");
          processInteractive(view);
          break;
        case "2":
          view.display("Enter commands file path:");
          String filePath = view.getInput();
          processHeadless(filePath, view);
          break;
        case "3":
          view.display("Exiting Calendar App.");
          return;
        default:
          view.display("Invalid command.");
          break;
      }
    }
  }

  /** Processes commands in interactive mode. */
  private void processInteractive(ConsoleView view) {
    while (true) {
      String input = view.getInput();
      if (input.equalsIgnoreCase("exit")) {
        view.display("Exiting Calendar App.");
        break;
      }
      try {
        String output = CommandProcessor.process(input, this);
        view.display(output);
      } catch (Exception e) {
        view.display("Error: " + e.getMessage());
      }
    }
  }

  /** Processes commands in headless mode from a file. */
  private void processHeadless(String filePath, ConsoleView view) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      int lineNo = 1;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          lineNo++;
          continue;
        }
        view.display("Processing command (" + lineNo + "): " + line);
        if (line.equalsIgnoreCase("exit")) {
          view.display("Exiting Calendar App.");
          break;
        }
        try {
          String output = CommandProcessor.process(line, this);
          view.display(output);
        } catch (Exception e) {
          view.display("Error at line " + lineNo + ": " + e.getMessage());
          break;
        }
        lineNo++;
      }
    } catch (IOException e) {
      view.display("Headless mode terminated due to error: " + e.getMessage());
    }
  }
}
