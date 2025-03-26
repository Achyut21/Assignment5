package calendar.view.mode;

import java.util.Scanner;

import calendar.controller.CalendarController;
import calendar.controller.command.Command;
import calendar.controller.command.CommandFactory;

public class InteractiveMode implements Mode {

  private final CalendarController controller;

  public InteractiveMode(CalendarController controller) {
    this.controller = controller;
  }
  @Override
  public void execute() {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.print("Enter command: ");
      String input = scanner.nextLine();

      if (input.equalsIgnoreCase("exit")) {
        System.out.println("Exiting Calendar App.");
        break;
      }
      try {
        Command command = CommandFactory.process(input, controller);
        String output = command.execute();
        System.out.println(output);
      }
      catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }
}
