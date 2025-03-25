package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;

public class ExportCommand implements Command {
  private String[] tokens;
  private CalendarController controller;

  public ExportCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  @Override
  public String execute() throws Exception {
    if (tokens.length < 3 || !tokens[1].equalsIgnoreCase("cal")) {
      throw new InvalidCommandException("export command must be 'export cal <filename>'");
    }
    String fileName = tokens[2];
    String path = controller.exportCalendar(fileName);
    return "Calendar exported to CSV at: " + path;
  }
}
