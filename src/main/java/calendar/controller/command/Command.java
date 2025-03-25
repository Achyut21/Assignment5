package calendar.controller.command;

import calendar.controller.CalendarController;

public interface Command {
  String execute() throws Exception;
}
