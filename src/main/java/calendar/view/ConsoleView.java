package calendar.view;

import java.util.Scanner;

/** Provides console-based I/O methods. */
public class ConsoleView {
  private Scanner scanner;

  /** Constructs a ConsoleView. */
  public ConsoleView() {
    scanner = new Scanner(System.in);
  }

  /** Reads and returns a line of user input. */
  public String getInput() {
    System.out.print("Enter command: ");
    return scanner.nextLine();
  }

  /** Displays the given message to the console. */
  public void display(String message) {
    System.out.println(message);
  }
}
