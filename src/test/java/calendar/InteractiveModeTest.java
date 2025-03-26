package calendar;

import static org.junit.Assert.assertTrue;

import calendar.controller.CalendarController;
import calendar.model.Calendar;
import calendar.view.mode.InteractiveMode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InteractiveModeTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @Before
  public void setUp() throws Exception {
    System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testExitImmediately() throws Exception {
    System.setIn(new ByteArrayInputStream("exit\n".getBytes(StandardCharsets.UTF_8)));
    Calendar cal = new Calendar("TestCal", ZoneId.of("America/New_York"));
    CalendarController controller = new CalendarController(cal);
    new InteractiveMode(controller).execute();
    String output = outContent.toString();
    assertTrue(output.contains("Enter command: "));
    assertTrue(output.contains("Exiting Calendar App."));
  }

  @Test
  public void testValidCommandThenExit() throws Exception {
    String input = "print events on 2025-01-01\nexit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    Calendar cal = new Calendar("TestCal", ZoneId.of("America/New_York"));
    CalendarController controller = new CalendarController(cal);
    new InteractiveMode(controller).execute();
    String output = outContent.toString();
    assertTrue(output.contains("Enter command: "));
    assertTrue(output.contains("No events on 2025-01-01"));
    assertTrue(output.contains("Exiting Calendar App."));
  }
}
