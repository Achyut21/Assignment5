package calendar.util;

import calendar.controller.CalendarController;
import calendar.exceptions.InvalidCommandException;
import calendar.exceptions.InvalidTokenException;
import calendar.exceptions.MissingParameterException;

/** Processes commands for the calendar application. */
public class CommandProcessor {

  /** Processes a command string using the given controller. */
  public static String process(String input, CalendarController controller) throws Exception {
    String[] tokens = input.trim().split("\\s+");
    if (tokens.length == 0) {
      throw new MissingParameterException("command");
    }
    String commandType = tokens[0].toLowerCase();
    switch (commandType) {
      case "create":
        if (tokens[1].equalsIgnoreCase("calendar")) {
          return processCreateCalendar(tokens, controller);
        }
        return processCreate(tokens, controller);
      case "edit":
        if (tokens[1].equalsIgnoreCase("calendar")) {
          return processEditCalendar(tokens, controller);
        }
        return processEdit(tokens, controller);
      case "use":
        return processUseCalendar(tokens, controller);
      case "copy":
        return processCopy(tokens, controller);
      case "print":
        return processPrint(tokens, controller);
      case "export":
        return processExport(tokens, controller);
      case "show":
        return processShow(tokens, controller);
      default: {
        throw new InvalidCommandException(commandType);
      }
    }
  }

  /** Processes a create calendar command. */
  private static String processCreateCalendar(String[] tokens, CalendarController controller) throws Exception {
    int index = 2;
    if (!tokens[index].equalsIgnoreCase("--name")) {
      throw new MissingParameterException("calendar name");
    }
    index++;
    String calName = tokens[index++];
    if (!tokens[index].equalsIgnoreCase("--timezone")) {
      throw new MissingParameterException("timezone");
    }
    index++;
    String timezone = tokens[index++];
    controller.createCalendar(calName, timezone);
    return "Calendar created: " + calName + " with timezone " + timezone;
  }

  /** Processes an edit calendar command. */
  private static String processEditCalendar(String[] tokens, CalendarController controller) throws Exception {
    int index = 2;
    if (!tokens[index].equalsIgnoreCase("--name")) {
      throw new MissingParameterException("calendar name");
    }
    index++;
    String calName = tokens[index++];
    if (!tokens[index].equalsIgnoreCase("--property")) {
      throw new MissingParameterException("property");
    }
    index++;
    String property = tokens[index++];
    String newValue = tokens[index++];
    controller.editCalendar(calName, property, newValue);
    return "Calendar " + calName + " updated: " + property + " = " + newValue;
  }

  /** Processes a use calendar command. */
  private static String processUseCalendar(String[] tokens, CalendarController controller) throws Exception {
    int index = 1;
    if (!tokens[index].equalsIgnoreCase("calendar")) {
      throw new MissingParameterException("calendar");
    }
    index++;
    if (!tokens[index].equalsIgnoreCase("--name")) {
      throw new MissingParameterException("calendar name");
    }
    index++;
    String calName = tokens[index++];
    controller.useCalendar(calName);
    return "Using calendar: " + calName;
  }

  /** Processes a copy command with the provided tokens. */
  private static String processCopy(String[] tokens, CalendarController controller) throws Exception {
    int index = 1;
    if (tokens[index].equalsIgnoreCase("event")) {
      index++;
      String eventName = tokens[index++];
      if (!tokens[index].equalsIgnoreCase("on")) {
        throw new MissingParameterException("on");
      }
      index++;
      String sourceDateTime = tokens[index++];
      if (!tokens[index].equalsIgnoreCase("--target")) {
        throw new MissingParameterException("target calendar");
      }
      index++;
      String targetCal = tokens[index++];
      if (!tokens[index].equalsIgnoreCase("to")) {
        throw new MissingParameterException("to");
      }
      index++;
      String targetDateTime = tokens[index++];
      controller.copyEvent(eventName, sourceDateTime, targetCal, targetDateTime);
      return "Event " + eventName + " copied to calendar " + targetCal + ".";
    } else if (tokens[index].equalsIgnoreCase("events")) {
      index++;
      if (tokens[index].equalsIgnoreCase("on")) {
        index++;
        String date = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("--target")) {
          throw new MissingParameterException("target calendar");
        }
        index++;
        String targetCal = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("to")) {
          throw new MissingParameterException("to");
        }
        index++;
        String targetDateTime = tokens[index++];
        controller.copyEventsOn(date, targetCal, targetDateTime);
        return "Events on " + date + " copied to calendar " + targetCal + ".";
      } else if (tokens[index].equalsIgnoreCase("between")) {
        index++;
        String startDate = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("and")) {
          throw new MissingParameterException("and");
        }
        index++;
        String endDate = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("--target")) {
          throw new MissingParameterException("target calendar");
        }
        index++;
        String targetCal = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("to")) {
          throw new MissingParameterException("to");
        }
        index++;
        String targetDate = tokens[index++];
        controller.copyEventsBetween(startDate, endDate, targetCal, targetDate);
        return "Events between " + startDate + " and " + endDate + " copied to calendar " + targetCal + ".";
      } else {
        throw new InvalidCommandException("copy");
      }
    }
    throw new InvalidCommandException("copy");
  }

  /** Processes a create event command with the provided tokens. */
  private static String processCreate(String[] tokens, CalendarController controller) throws Exception {
    if (tokens.length < 3 || !tokens[1].equalsIgnoreCase("event")) {
      throw new MissingParameterException("event");
    }
    int index = 2;
    boolean autoDecline = false;
    if (tokens[index].equalsIgnoreCase("--autodecline")) {
      autoDecline = true;
      index++;
    }
    if (index >= tokens.length) {
      throw new MissingParameterException("event name");
    }
    String eventName = tokens[index++];
    if (index >= tokens.length) {
      throw new MissingParameterException("Expected from or on");
    }
    String mode = tokens[index].toLowerCase();
    if (mode.equals("from")) {
      index++;
      if (index >= tokens.length) {
        throw new MissingParameterException("start datetime");
      }
      String startDateTime = tokens[index++];
      if (index >= tokens.length || !tokens[index].equalsIgnoreCase("to")) {
        throw new InvalidTokenException("to");
      }
      index++;
      if (index >= tokens.length) {
        throw new MissingParameterException("end datetime");
      }
      String endDateTime = tokens[index++];
      if (index < tokens.length && tokens[index].equalsIgnoreCase("repeats")) {
        index++;
        if (index >= tokens.length) {
          throw new MissingParameterException("weekdays for recurring event");
        }
        String weekdays = tokens[index++];
        if (index >= tokens.length) {
          throw new MissingParameterException("Expected 'for' or 'until'");
        }
        String recurringType = tokens[index++].toLowerCase();
        if (recurringType.equals("for")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("occurrence count");
          }
          int occurrences = Integer.parseInt(tokens[index++]);
          if (index >= tokens.length || !tokens[index].equalsIgnoreCase("times")) {
            throw new InvalidTokenException("times");
          }
          index++;
          controller.createRecurringEventOccurrences(
              eventName, startDateTime, endDateTime, "", "", true, weekdays, occurrences, autoDecline);
          return "Recurring timed event created with " + occurrences + " occurrences.";
        } else if (recurringType.equals("until")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("until datetime");
          }
          String untilDateTime = tokens[index++];
          controller.createRecurringEventUntil(
              eventName, startDateTime, endDateTime, "", "", true, weekdays, untilDateTime, autoDecline);
          return "Recurring timed event created until " + untilDateTime + ".";
        } else {
          throw new InvalidCommandException("Recurring specification: " + recurringType);
        }
      } else {
        controller.createSingleEvent(eventName, startDateTime, endDateTime, "", "", true, autoDecline);
        return "Single timed event created: " + eventName;
      }
    } else if (mode.equals("on")) {
      index++;
      if (index >= tokens.length) {
        throw new MissingParameterException("date for all day event");
      }
      String date = tokens[index++];
      if (index < tokens.length && tokens[index].equalsIgnoreCase("repeats")) {
        index++;
        if (index >= tokens.length) {
          throw new MissingParameterException("weekdays for recurring all day event");
        }
        String weekdays = tokens[index++];
        if (index >= tokens.length) {
          throw new MissingParameterException("Expected 'for' or 'until'");
        }
        String recurringType = tokens[index++].toLowerCase();
        if (recurringType.equals("for")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("occurrence count");
          }
          int occurrences = Integer.parseInt(tokens[index++]);
          if (index >= tokens.length || !tokens[index].equalsIgnoreCase("times")) {
            throw new InvalidTokenException("times");
          }
          index++;
          controller.createRecurringAllDayEventOccurrences(
              eventName, date, "", "", true, weekdays, occurrences, autoDecline);
          return "Recurring all day event created with " + occurrences + " occurrences.";
        } else if (recurringType.equals("until")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("until date");
          }
          String untilDate = tokens[index++];
          controller.createRecurringAllDayEventUntil(
              eventName, date, "", "", true, weekdays, untilDate, autoDecline);
          return "Recurring all day event created until " + untilDate + ".";
        } else {
          throw new InvalidCommandException("Recurring specification: " + recurringType);
        }
      } else {
        controller.createSingleAllDayEvent(eventName, date, "", "", true, autoDecline);
        return "Single all day event created: " + eventName;
      }
    } else {
      throw new InvalidCommandException("Expected from or on, found: " + mode);
    }
  }

  /** Processes an edit command with the provided tokens. */
  private static String processEdit(String[] tokens, CalendarController controller) throws Exception {
    if (tokens.length < 2) {
      throw new MissingParameterException("edit command");
    }
    String target = tokens[1].toLowerCase();
    if (target.equals("event")) {
      if (tokens.length < 9) {
        throw new MissingParameterException("edit event command parameters");
      }
      String property = tokens[2];
      String eventName = tokens[3];
      if (!tokens[4].equalsIgnoreCase("from")) {
        throw new InvalidTokenException("from");
      }
      String startDateTime = tokens[5];
      if (!tokens[6].equalsIgnoreCase("to")) {
        throw new InvalidTokenException("to");
      }
      String endDateTime = tokens[7];
      if (tokens.length < 10 || !tokens[8].equalsIgnoreCase("with")) {
        throw new InvalidTokenException("with");
      }
      String newValue = tokens[9];
      controller.editSingleEvent(property, eventName, startDateTime, endDateTime, newValue);
      return "Single event edited.";
    } else if (target.equals("events")) {
      String property = tokens[2];
      String eventName = tokens[3];
      if (tokens[4].equalsIgnoreCase("from")) {
        String startDateTime = tokens[5];
        if (tokens.length < 8 || !tokens[6].equalsIgnoreCase("with")) {
          throw new InvalidTokenException("with");
        }
        String newValue = tokens[7];
        controller.editEventsFrom(property, eventName, startDateTime, newValue);
        return "Events starting at " + startDateTime + " edited.";
      } else {
        String newValue = tokens[4];
        controller.editEvents(property, eventName, newValue);
        return "All events with name " + eventName + " edited.";
      }
    } else {
      throw new InvalidCommandException("Invalid edit command target: " + target);
    }
  }

  /** Processes a print command with the provided tokens. */
  private static String processPrint(String[] tokens, CalendarController controller) throws Exception {
    if (tokens.length < 3) {
      throw new MissingParameterException("print command");
    }
    if (!tokens[1].equalsIgnoreCase("events")) {
      throw new InvalidCommandException("print command must be 'print events ...'");
    }
    if (tokens[2].equalsIgnoreCase("on")) {
      if (tokens.length < 4) {
        throw new MissingParameterException("date for print events on");
      }
      String date = tokens[3];
      return controller.getFormattedEventsOn(date);
    } else if (tokens[2].equalsIgnoreCase("from")) {
      if (tokens.length < 6 || !tokens[4].equalsIgnoreCase("to")) {
        throw new InvalidTokenException("to");
      }
      String startDateTime = tokens[3];
      String endDateTime = tokens[5];
      return controller.getFormattedEventsBetween(startDateTime, endDateTime);
    } else {
      throw new InvalidCommandException("Invalid print events command.");
    }
  }

  /** Processes an export command with the provided tokens. */
  private static String processExport(String[] tokens, CalendarController controller) throws Exception {
    if (tokens.length < 3 || !tokens[1].equalsIgnoreCase("cal")) {
      throw new InvalidCommandException("export command must be 'export cal <filename>'");
    }
    String fileName = tokens[2];
    String path = controller.exportCalendar(fileName);
    return "Calendar exported to CSV at: " + path;
  }

  /** Processes a show command with the provided tokens. */
  private static String processShow(String[] tokens, CalendarController controller) throws Exception {
    if (tokens.length < 4 || !tokens[1].equalsIgnoreCase("status") || !tokens[2].equalsIgnoreCase("on")) {
      throw new InvalidCommandException("show status command must be 'show status on <datetime>'");
    }
    String dateTime = tokens[3];
    return controller.getBusyStatus(dateTime);
  }
}
