package calendar.view.mode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import calendar.controller.CalendarController;
import calendar.controller.command.Command;
import calendar.controller.command.CommandFactory;

public class HeadlessMode implements Mode {
  private final String filePath;

  private final CalendarController controller;

  public HeadlessMode(String filePath, CalendarController controller) {
    this.filePath = filePath;
    this.controller = controller;
  }

  @Override
  public void execute() {
    Scanner scanner = new Scanner(System.in);
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      int lineNo = 1;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          lineNo++;
          continue;
        }
        System.out.println("Processing command (" + lineNo + "): " + line);
        if (line.equalsIgnoreCase("exit")) {
          System.out.println("Exiting Calendar App.");
          break;
        }
        try {
          Command command = CommandFactory.process(line, controller);
          String output = command.execute();
          System.out.println(output);
        }
        catch (Exception e) {
          System.out.println("Error at line " + lineNo + ": " + e.getMessage());
          break;
        }
        lineNo++;
      }
    } catch (IOException e) {
      System.out.println("Headless mode terminated due to error: " + e.getMessage());
    }
  }

}
