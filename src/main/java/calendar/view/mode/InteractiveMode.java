package calendar.view.mode;

import calendar.controller.CalendarController;
import calendar.controller.command.Command;
import calendar.controller.command.CommandFactory;
import calendar.view.ConsoleView;

public class InteractiveMode implements Mode {
  private final ConsoleView view;

  //TODO: Decouple This
  private final CalendarController controller;

  public InteractiveMode(ConsoleView view, CalendarController controller) {
    this.view = view;
    this.controller = controller;
  }
  @Override
  public void execute() {
    while (true) {
      String input = view.getInput();
      if (input.equalsIgnoreCase("exit")) {
        view.display("Exiting Calendar App.");
        break;
      }
      try {
        Command command = CommandFactory.process(input, controller);
        String output = command.execute();
        view.display(output);
      } catch (Exception e) {
        view.display("Error: " + e.getMessage());
      }
    }
  }
}
