package calendar.view.mode;

import java.util.Scanner;

import calendar.controller.CalendarController;

public class ModeFactory {

  private final CalendarController controller;

  public ModeFactory(CalendarController controller) {
    this.controller = controller;
  }

  public Mode getMode() {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("Choose mode: 1 for Interactive, 2 for Headless and 3 to Exit");
      String mode = scanner.nextLine();
      switch (mode) {
        case "1": {
          System.out.println("Interactive mode.");
          return new InteractiveMode(controller);
        }
        case "2": {
          System.out.println("Enter commands file path:");
          String filePath = scanner.nextLine();

          return new HeadlessMode(filePath, controller);
        }
        case "3": {
          System.out.println("Exiting Calendar App.");
          return null;
        }
        default: {
          System.out.println("Invalid command.");
          break;
        }
      }
    }
  }

}
