package calendar.view.mode;

import calendar.controller.CalendarController;
import calendar.controller.command.Command;
import calendar.view.ConsoleView;

public class ModeFactory {

  private final ConsoleView view;

  //TODO: Decouple This
  private final CalendarController controller;

  public ModeFactory(ConsoleView view, CalendarController controller) {
    this.view = view;
    this.controller = controller;
  }

  public Mode getMode() {
    while (true) {
      view.display("Choose mode: 1 for Interactive, 2 for Headless and 3 to Exit");
      String mode = view.getInput();
      switch (mode) {
        case "1": {
          view.display("Interactive mode.");
          return new InteractiveMode(view, controller);
        }
        case "2": {
          view.display("Enter commands file path:");
          String filePath = view.getInput();
          return new HeadlessMode(filePath, view, controller);
        }
        case "3": {
          view.display("Exiting Calendar App.");
          return null;
        }
        default: {
          view.display("Invalid command.");
          break;
        }
      }
    }
  }

}
