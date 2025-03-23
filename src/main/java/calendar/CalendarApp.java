package calendar;

import calendar.controller.CalendarController;
import calendar.model.Calendar;

/** Main application class. */
public class CalendarApp {
  /** Main method that starts the CalendarController. */
  public static void main(String[] args) {
    Calendar calendar = new Calendar();
    CalendarController controller = new CalendarController(calendar);
    controller.run();
  }
}
