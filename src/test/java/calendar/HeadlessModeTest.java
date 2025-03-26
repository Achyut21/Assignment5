package calendar;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import calendar.controller.CalendarController;
import calendar.model.Calendar;
import calendar.view.mode.HeadlessMode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HeadlessModeTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private File tempFile;
  private CalendarController controller;

  @Before
  public void setUp() throws Exception {
    System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
    Calendar cal = new Calendar("TestCal", ZoneId.of("America/New_York"));
    controller = new CalendarController(cal);
    tempFile = File.createTempFile("headless_test", ".txt");
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
    if (tempFile.exists()) {
      tempFile.delete();
    }
  }

  @Test
  public void testExitImmediately() throws Exception {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
      writer.write("exit");
    }
    new HeadlessMode(tempFile.getAbsolutePath(), controller).execute();
    String output = outContent.toString();
    assertTrue(output.contains("Processing command (1): exit"));
    assertTrue(output.contains("Exiting Calendar App."));
  }

  @Test
  public void testValidCommandThenExit() throws Exception {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
      writer.write("print events on 2025-01-01\nexit");
    }
    new HeadlessMode(tempFile.getAbsolutePath(), controller).execute();
    String output = outContent.toString();
    assertTrue(output.contains("Processing command (1): print events on 2025-01-01"));
    assertTrue(output.contains("No events on 2025-01-01"));
    assertTrue(output.contains("Processing command (2): exit"));
    assertTrue(output.contains("Exiting Calendar App."));
  }

  @Test
  public void testInvalidCommandBreaksExecution() throws Exception {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
      writer.write("invalid command\nexit");
    }
    new HeadlessMode(tempFile.getAbsolutePath(), controller).execute();
    String output = outContent.toString();
    assertTrue(output.contains("Processing command (1): invalid command"));
    assertTrue(output.contains("Error at line 1:"));
    assertFalse(output.contains("Processing command (2): exit"));
  }

  @Test
  public void testIOExceptionHandling() throws Exception {
    new HeadlessMode("non_existent_file.txt", controller).execute();
    String output = outContent.toString();
    assertTrue(output.contains("Headless mode terminated due to error:"));
  }
}
