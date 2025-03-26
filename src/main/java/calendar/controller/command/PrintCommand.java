package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;
import calendar.view.exceptions.InvalidTokenException;
import calendar.view.exceptions.MissingParameterException;

/** Concrete implementation of the print command. */
public class PrintCommand implements Command {
  private String[] tokens;
  private CalendarController controller;

  /** Constructor for the print command. */
  public PrintCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  /** Executes the print command. */
  @Override
  public String execute() throws Exception {
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


}
