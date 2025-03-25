package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.MissingParameterException;

public class UseCommand implements Command {
  private String[] tokens;
  private CalendarController controller;

  public UseCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  @Override
  public String execute() throws Exception {
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

}
