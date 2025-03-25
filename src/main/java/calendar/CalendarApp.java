package calendar;

import calendar.controller.CalendarController;
import calendar.model.Calendar;
import java.time.ZoneId;

/** Main application class. */
public class CalendarApp {
  /** Main method that starts the CalendarController. */
  public static void main(String[] args) {
    Calendar calendar = new Calendar("Default Calendar", ZoneId.of("America/New_York"));
    CalendarController controller = new CalendarController(calendar);
    controller.run();
  }
}
