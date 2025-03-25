package calendar.view.mode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import calendar.controller.CalendarController;
import calendar.controller.command.Command;
import calendar.controller.command.CommandFactory;
import calendar.view.ConsoleView;

public class HeadlessMode implements Mode {
  private final ConsoleView view;
  private final String filePath;

  //TODO: Decouple This
  private final CalendarController controller;

  public HeadlessMode(String filePath, ConsoleView view, CalendarController controller) {
    this.view = view;
    this.filePath = filePath;
    this.controller = controller;
  }

  @Override
  public void execute() {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      int lineNo = 1;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          lineNo++;
          continue;
        }
        view.display("Processing command (" + lineNo + "): " + line);
        if (line.equalsIgnoreCase("exit")) {
          view.display("Exiting Calendar App.");
          break;
        }
        try {
          Command command = CommandFactory.process(line, controller);
          String output = command.execute();
          view.display(output);
        }
        catch (Exception e) {
          view.display("Error at line " + lineNo + ": " + e.getMessage());
          break;
        }
        lineNo++;
      }
    } catch (IOException e) {
      view.display("Headless mode terminated due to error: " + e.getMessage());
    }
  }

}
