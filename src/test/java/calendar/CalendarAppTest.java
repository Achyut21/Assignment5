package calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import calendar.controller.CalendarController;
import calendar.exceptions.InvalidCommandException;
import calendar.exceptions.InvalidTokenException;
import calendar.exceptions.MissingParameterException;
import calendar.model.Calendar;
import calendar.model.SingleEvent;
import calendar.util.CommandProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/** Test Class for CalendarAPP. */
public class CalendarAppTest {

  private CalendarController controller;
  private SingleEvent event;

  /** Sets up test fixtures. */
  @Before
  public void setup() {
    Calendar defaultCal = new Calendar("Default", ZoneId.of("America/New_York"));
    controller = new CalendarController(defaultCal);
    event =
        new SingleEvent(
            "Test Event",
            LocalDateTime.of(2025, 4, 1, 10, 0),
            LocalDateTime.of(2025, 4, 1, 11, 0),
            "Initial Description",
            "Initial Location",
            true);
  }

  /** Tests getters of SingleEvent. */
  @Test
  public void testGetters() {
    assertEquals("Test Event", event.getName());
    assertEquals(LocalDateTime.of(2025, 4, 1, 10, 0), event.getStart());
    assertEquals(LocalDateTime.of(2025, 4, 1, 11, 0), event.getEnd());
    assertEquals("Initial Description", event.getDescription());
    assertEquals("Initial Location", event.getLocation());
    assertTrue(event.isPublic());
  }

  /** Tests setName method. */
  @Test
  public void testSetName() {
    event.setName("New Event Name");
    assertEquals("New Event Name", event.getName());
  }

  /** Tests setDescription method. */
  @Test
  public void testSetDescription() {
    event.setDescription("New Description");
    assertEquals("New Description", event.getDescription());
  }

  /** Tests setLocation method. */
  @Test
  public void testSetLocation() {
    event.setLocation("New Location");
    assertEquals("New Location", event.getLocation());
  }

  /** Tests setIsPublic method. */
  @Test
  public void testSetIsPublic() {
    event.setIsPublic(false);
    assertFalse(event.isPublic());
    event.setIsPublic(true);
    assertTrue(event.isPublic());
  }

  /** Tests successful creation of a single event. */
  @Test
  public void testCreateSingleEventSuccess() throws Exception {
    controller.createSingleEvent(
        "Meeting", "2025-04-01T10:00", "2025-04-01T11:00", "Team meeting", "Room1", true, false);
    String output = controller.getFormattedEventsOn("2025-04-01");
    assertTrue(output.contains("Meeting"));
  }

  /** Tests single event conflict with auto-decline true. */
  @Test(expected = Exception.class)
  public void testCreateSingleEventConflictAutoDecline() throws Exception {
    controller.createSingleEvent(
        "Event1", "2025-04-01T10:00", "2025-04-01T11:00", "", "", true, false);
    controller.createSingleEvent(
        "Event2", "2025-04-01T10:30", "2025-04-01T11:30", "", "", true, true);
  }

  /** Tests single event conflict with auto-decline false. */
  @Test
  public void testCreateSingleEventConflictNoAutoDecline() throws Exception {
    controller.createSingleEvent(
        "Event1", "2025-04-01T10:00", "2025-04-01T11:00", "", "", true, false);
    controller.createSingleEvent(
        "Event2", "2025-04-01T10:30", "2025-04-01T11:30", "", "", true, false);
    String output = controller.getFormattedEventsOn("2025-04-01");
    assertTrue(output.contains("Event1") && output.contains("Event2"));
  }

  /** Tests recurring timed event creation with occurrences. */
  @Test
  public void testCreateRecurringEventOccurrencesSuccess() throws Exception {
    controller.createRecurringEventOccurrences(
        "Yoga", "2025-04-01T08:00", "2025-04-01T09:00", "Morning yoga", "Gym", true, "T", 2, false);
    String output = controller.getFormattedEventsOn("2025-04-01");
    assertTrue(output.contains("Yoga"));
  }

  /** Tests recurring timed event creation with until date. */
  @Test
  public void testCreateRecurringEventUntilSuccess() throws Exception {
    controller.createRecurringEventUntil(
        "Class",
        "2025-04-01T09:00",
        "2025-04-01T10:00",
        "Math",
        "Room101",
        true,
        "T",
        "2025-04-15T00:00",
        false);
    String output = controller.getFormattedEventsOn("2025-04-01");
    assertTrue(output.contains("Class"));
  }

  /** Tests recurring all-day event creation with occurrences. */
  @Test
  public void testCreateRecurringAllDayEventOccurrencesSuccess() throws Exception {
    controller.createRecurringAllDayEventOccurrences(
        "Holiday", "2025-04-01", "Holiday", "Office", true, "MTW", 3, false);
    String output = controller.getFormattedEventsOn("2025-04-01");
    assertTrue(output.contains("Holiday"));
  }

  /** Tests recurring all-day event creation with until date. */
  @Test
  public void testCreateRecurringAllDayEventUntilSuccess() throws Exception {
    controller.createRecurringAllDayEventUntil(
        "Festival", "2025-04-05", "Festival", "City", true, "F", "2025-04-26", false);
    String output = controller.getFormattedEventsOn("2025-04-11");
    assertTrue(output.contains("Festival"));
  }

  /** Tests single all-day event creation formatting. */
  @Test
  public void testCreateSingleAllDayEventSuccess() throws Exception {
    controller.createSingleAllDayEvent(
        "Holiday", "2025-04-10", "National holiday", "Country", true, false);
    String output = controller.getFormattedEventsOn("2025-04-10");
    assertTrue(output.contains("Holiday") && !output.contains("00:00"));
  }

  /** Tests successful editing of a single event. */
  @Test
  public void testEditSingleEventSuccess() throws Exception {
    controller.createSingleEvent(
        "Meeting", "2025-04-01T10:00", "2025-04-01T11:00", "Team meeting", "Room1", true, false);
    controller.editSingleEvent(
        "description", "Meeting", "2025-04-01T10:00", "2025-04-01T11:00", "Updated meeting");
    String output = controller.getFormattedEventsOn("2025-04-01");
    assertTrue(output.contains("Meeting"));
  }

  /** Tests editing a non-existent event. */
  @Test(expected = Exception.class)
  public void testEditSingleEventNotFound() throws Exception {
    controller.editSingleEvent(
        "description", "Nonexistent", "2025-04-01T10:00", "2025-04-01T11:00", "Update");
  }

  /** Tests editing events using editEventsFrom. */
  @Test
  public void testEditEventsFromSuccess() throws Exception {
    controller.createSingleEvent(
        "Workshop", "2025-04-02T14:00", "2025-04-02T15:00", "Initial", "Lab", true, false);
    controller.createSingleEvent(
        "Workshop", "2025-04-02T14:00", "2025-04-02T15:00", "Initial", "Lab", true, false);
    controller.editEventsFrom("description", "Workshop", "2025-04-02T14:00", "Updated");
    String output = controller.getFormattedEventsOn("2025-04-02");
    assertTrue(output.contains("Workshop"));
  }

  /** Tests editEventsFrom when no matching events exist. */
  @Test(expected = Exception.class)
  public void testEditEventsFromNotFound() throws Exception {
    controller.editEventsFrom("description", "Nonexistent", "2025-04-02T14:00", "Updated");
  }

  /** Tests bulk editing of events using editEvents. */
  @Test
  public void testEditEventsSuccess() throws Exception {
    controller.createSingleEvent(
        "Seminar", "2025-04-03T09:00", "2025-04-03T10:00", "Old", "Auditorium", true, false);
    controller.createSingleEvent(
        "Seminar", "2025-04-03T11:00", "2025-04-03T12:00", "Old", "Auditorium", true, false);
    controller.editEvents("description", "Seminar", "New Description");
    String output = controller.getFormattedEventsOn("2025-04-03");
    assertTrue(output.contains("Seminar"));
  }

  /** Tests bulk editing when no matching events exist. */
  @Test(expected = Exception.class)
  public void testEditEventsNotFound() throws Exception {
    controller.editEvents("description", "Nonexistent", "New Description");
  }

  /** Tests formatted events on a date with events. */
  @Test
  public void testGetFormattedEventsOnSuccess() throws Exception {
    controller.createSingleEvent(
        "Meeting", "2025-04-04T10:00", "2025-04-04T11:00", "Team meeting", "RoomA", true, false);
    String output = controller.getFormattedEventsOn("2025-04-04");
    assertTrue(output.contains("Meeting") && output.contains("10:00") && output.contains("11:00"));
  }

  /** Tests formatted events on a date with no events. */
  @Test
  public void testGetFormattedEventsOnNoEvents() throws Exception {
    String output = controller.getFormattedEventsOn("2025-04-05");
    assertEquals("No events on 2025-04-05", output);
  }

  /** Tests formatted events between two date-times when events exist. */
  @Test
  public void testGetFormattedEventsBetweenSuccess() throws Exception {
    controller.createSingleEvent(
        "Call", "2025-04-06T15:00", "2025-04-06T16:00", "Call", "Office", true, false);
    String output = controller.getFormattedEventsBetween("2025-04-06T14:00", "2025-04-06T17:00");
    assertTrue(output.contains("Call"));
  }

  /** Tests formatted events between two date-times when no events exist. */
  @Test
  public void testGetFormattedEventsBetweenNoEvents() throws Exception {
    String output = controller.getFormattedEventsBetween("2025-04-07T10:00", "2025-04-07T11:00");
    assertEquals("No events between 2025-04-07T10:00 and 2025-04-07T11:00", output);
  }

  /** Tests that isBusy returns true when an event is scheduled. */
  @Test
  public void testIsBusyTrue() throws Exception {
    controller.createSingleEvent(
        "Interview", "2025-04-08T13:00", "2025-04-08T14:00", "", "", true, false);
    String status = controller.getBusyStatus("2025-04-08T13:30");
    assertTrue(status.contains("Busy"));
  }

  /** Tests that isBusy returns false when no event is scheduled. */
  @Test
  public void testIsBusyFalse() throws Exception {
    controller.createSingleEvent(
        "Lunch", "2025-04-08T12:00", "2025-04-08T13:00", "", "", true, false);
    String status = controller.getBusyStatus("2025-04-08T11:00");
    assertTrue(status.contains("Available"));
  }

  /** Tests CSV export header correctness. */
  @Test
  public void testExportCalendarCSVHeader() throws Exception {
    controller.createSingleEvent(
        "Event", "2025-04-09T09:00", "2025-04-09T10:00", "Desc", "Loc", true, false);
    String filePath = controller.exportCalendar("test_export.csv");
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    String header = reader.readLine();
    reader.close();
    assertEquals(
        "Subject,Start Date,Start Time,End Date,End Time,"
            + "All Day Event,Description,Location,Private",
        header);
    new File(filePath).delete();
  }

  /** Tests CSV export content correctness. */
  @Test
  public void testExportCalendarCSVContent() throws Exception {
    controller.createSingleEvent(
        "Event", "2025-04-10T09:00", "2025-04-10T10:00", "Desc", "Loc", true, false);
    String filePath = controller.exportCalendar("test_export2.csv");
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    reader.readLine();
    String line = reader.readLine();
    reader.close();
    assertNotNull(line);
    assertTrue(line.contains("Event") && line.contains("04/10/2025"));
    new File(filePath).delete();
  }

  /** Tests recurring timed event occurrences with zero occurrences yield no events. */
  @Test
  public void testRecurringEventOccurrencesZero() throws Exception {
    controller.createRecurringEventOccurrences(
        "ZeroEvent", "2025-04-11T09:00", "2025-04-11T10:00", "", "", true, "MWF", 0, false);
    String output = controller.getFormattedEventsOn("2025-04-11");
    assertEquals("No events on 2025-04-11", output);
  }

  /** Tests recurring timed event occurrences with negative occurrences throw an exception. */
  @Test(expected = IllegalArgumentException.class)
  public void testRecurringEventOccurrencesNegative() throws Exception {
    controller.createRecurringEventOccurrences(
        "NegativeEvent", "2025-04-11T09:00", "2025-04-11T10:00", "", "", true, "MWF", -1, false);
  }

  /** Tests recurring all-day event occurrences with zero occurrences yield no events. */
  @Test
  public void testRecurringAllDayEventOccurrencesZero() throws Exception {
    controller.createRecurringAllDayEventOccurrences(
        "ZeroAllDay", "2025-04-12", "", "", true, "MTW", 0, false);
    String output = controller.getFormattedEventsOn("2025-04-12");
    assertEquals("No events on 2025-04-12", output);
  }

  /** Tests recurring event conflict with auto-decline true throws exception. */
  @Test(expected = Exception.class)
  public void testRecurringEventConflictAutoDecline() throws Exception {
    controller.createSingleEvent(
        "Conflict", "2025-04-13T10:00", "2025-04-13T11:00", "", "", true, false);
    controller.createRecurringEventOccurrences(
        "RecurringConflict", "2025-04-13T10:30", "2025-04-13T11:30", "", "", true, "UR", 2, true);
  }

  /** Tests recurring event conflict with auto-decline false allows conflict. */
  @Test
  public void testRecurringEventConflictNoAutoDecline() throws Exception {
    controller.createSingleEvent(
        "Conflict", "2025-04-14T10:00", "2025-04-14T11:00", "", "", true, false);
    controller.createRecurringEventOccurrences(
        "RecurringConflict", "2025-04-14T10:30", "2025-04-14T11:30", "", "", true, "MTW", 2, false);
    String output = controller.getFormattedEventsOn("2025-04-14");
    assertTrue(output.contains("Conflict") && output.contains("RecurringConflict"));
  }

  /** Tests CommandProcessor valid create single event command. */
  @Test
  public void testCommandProcessorCreateSingle() throws Exception {
    String cmd = "create event Meeting from 2025-04-15T10:00 to 2025-04-15T11:00 --autodecline";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Single timed event created"));
  }

  /** Tests CommandProcessor valid edit single event command. */
  @Test
  public void testCommandProcessorEditSingle() throws Exception {
    controller.createSingleEvent(
        "EditTest", "2025-04-16T10:00", "2025-04-16T11:00", "", "", true, false);
    String cmd =
        "edit event description EditTest from 2025-04-16T10:00 to 2025-04-16T11:00 with "
            + "UpdatedDesc";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Single event edited"));
  }

  /** Tests CommandProcessor valid print events on command. */
  @Test
  public void testCommandProcessorPrintOn() throws Exception {
    controller.createSingleEvent(
        "PrintTest", "2025-04-17T09:00", "2025-04-17T10:00", "", "", true, false);
    String cmd = "print events on 2025-04-17";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("PrintTest"));
  }

  /** Tests CommandProcessor valid export command. */
  @Test
  public void testCommandProcessorExport() throws Exception {
    controller.createSingleEvent(
        "ExportTest", "2025-04-18T09:00", "2025-04-18T10:00", "Desc", "Loc", true, false);
    String cmd = "export cal test_export_cmd.csv";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Calendar exported to CSV at:"));
    new File("test_export_cmd.csv").delete();
  }

  /** Tests CommandProcessor valid show status command. */
  @Test
  public void testCommandProcessorShow() throws Exception {
    controller.createSingleEvent(
        "ShowTest", "2025-04-19T14:00", "2025-04-19T15:00", "", "", true, false);
    String cmd = "show status on 2025-04-19T14:30";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Busy"));
  }

  /** Tests CommandProcessor invalid command. */
  @Test(expected = InvalidCommandException.class)
  public void testCommandProcessorInvalidCommand() throws Exception {
    String cmd = "invalid command";
    CommandProcessor.process(cmd, controller);
  }

  /** Tests CommandProcessor missing parameter. */
  @Test(expected = MissingParameterException.class)
  public void testCommandProcessorMissingParameter() throws Exception {
    String cmd = "create event";
    CommandProcessor.process(cmd, controller);
  }

  /** Tests CommandProcessor invalid token. */
  @Test(expected = InvalidTokenException.class)
  public void testCommandProcessorInvalidToken() throws Exception {
    String cmd = "create event Meeting from 2025-04-20T10:00 2025-04-20T11:00 --autodecline";
    CommandProcessor.process(cmd, controller);
  }

  /** Tests CommandProcessor invalid recurring specification. */
  @Test(expected = InvalidCommandException.class)
  public void testCommandProcessorInvalidRecurringSpecification() throws Exception {
    String cmd =
        "create event Meeting from 2025-04-21T10:00 to 2025-04-21T11:00 repeats MTW invalid";
    CommandProcessor.process(cmd, controller);
  }

  @Test
  public void testCreateSingleEventCommand() throws Exception {
    String cmd = "create event Meeting from 2025-05-01T10:00 to 2025-05-01T11:00 --autodecline";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Single timed event created: Meeting"));
  }

  /** Tests creating a recurring timed event by occurrences command. */
  @Test
  public void testCreateRecurringEventOccurrencesCommand() throws Exception {
    String cmd = "create event Yoga from 2025-05-01T08:00 to 2025-05-01T09:00 repeats MTW for 3 times --autodecline";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Recurring timed event created with 3 occurrences."));
  }

  /** Tests creating a recurring timed event until a specific date command. */
  @Test
  public void testCreateRecurringEventUntilCommand() throws Exception {
    String cmd = "create event Class from 2025-05-01T09:00 to 2025-05-01T10:00 repeats T until 2025-05-15T00:00 --autodecline";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Recurring timed event created until 2025-05-15T00:00"));
  }

  /** Tests creating a single all-day event command. */
  @Test
  public void testCreateSingleAllDayEventCommand() throws Exception {
    String cmd = "create event Holiday on 2025-05-10 --autodecline";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Single all day event created: Holiday"));
  }

  /** Tests creating a recurring all-day event by occurrences command. */
  @Test
  public void testCreateRecurringAllDayEventOccurrencesCommand() throws Exception {
    String cmd = "create event Holiday on 2025-05-10 repeats MTW for 2 times";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Recurring all day event created with 2 occurrences."));
  }

  /** Tests creating a recurring all-day event until a specified date command. */
  @Test
  public void testCreateRecurringAllDayEventUntilCommand() throws Exception {
    String cmd = "create event Festival on 2025-05-05 repeats F until 2025-05-20";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Recurring all day event created until 2025-05-20"));
  }

  /** Tests editing a single event command. */
  @Test
  public void testEditSingleEventCommand() throws Exception {
    CommandProcessor.process("create event Meeting from 2025-05-01T10:00 to 2025-05-01T11:00 --autodecline", controller);
    String editCmd = "edit event description Meeting from 2025-05-01T10:00 to 2025-05-01T11:00 with UpdatedDesc";
    String result = CommandProcessor.process(editCmd, controller);
    assertTrue(result.contains("Single event edited."));
  }

  /** Tests editing events from a specified date-time command. */
  @Test
  public void testEditEventsFromCommand() throws Exception {
    CommandProcessor.process("create event Workshop from 2025-05-02T14:00 to 2025-05-02T15:00 --autodecline", controller);
    CommandProcessor.process("create event Workshop from 2025-05-02T14:00 to 2025-05-02T15:00 --autodecline", controller);
    String editCmd = "edit events description Workshop from 2025-05-02T14:00 with UpdatedWorkshop";
    String result = CommandProcessor.process(editCmd, controller);
    assertTrue(result.contains("Events starting at 2025-05-02T14:00 edited."));
  }

  /** Tests bulk editing events command. */
  @Test
  public void testEditEventsCommand() throws Exception {
    CommandProcessor.process("create event Seminar from 2025-05-03T09:00 to 2025-05-03T10:00 --autodecline", controller);
    CommandProcessor.process("create event Seminar from 2025-05-03T11:00 to 2025-05-03T12:00 --autodecline", controller);
    String editCmd = "edit events description Seminar NewDesc";
    String result = CommandProcessor.process(editCmd, controller);
    assertTrue(result.contains("All events with name Seminar edited."));
  }

  /** Tests printing events on a specific date command. */
  @Test
  public void testPrintEventsOnCommand() throws Exception {
    CommandProcessor.process("create event Meeting from 2025-05-04T10:00 to 2025-05-04T11:00 --autodecline", controller);
    String printCmd = "print events on 2025-05-04";
    String result = CommandProcessor.process(printCmd, controller);
    assertTrue(result.contains("Meeting"));
  }

  /** Tests printing events between two date-times command. */
  @Test
  public void testPrintEventsBetweenCommand() throws Exception {
    CommandProcessor.process("create event Call from 2025-05-06T15:00 to 2025-05-06T16:00 --autodecline", controller);
    String printCmd = "print events from 2025-05-06T14:00 to 2025-05-06T17:00";
    String result = CommandProcessor.process(printCmd, controller);
    assertTrue(result.contains("Call"));
  }

  /** Tests exporting the calendar to a CSV file command. */
  @Test
  public void testExportCalendarCommand() throws Exception {
    CommandProcessor.process("create event ExportTest from 2025-05-07T09:00 to 2025-05-07T10:00 --autodecline", controller);
    String exportCmd = "export cal test_export.csv";
    String result = CommandProcessor.process(exportCmd, controller);
    assertTrue(result.contains("Calendar exported to CSV at:"));
    Files.deleteIfExists(Paths.get("test_export.csv"));
  }

  /** Tests showing busy status command. */
  @Test
  public void testShowStatusCommand() throws Exception {
    CommandProcessor.process("create event BusyTest from 2025-05-08T13:00 to 2025-05-08T14:00 --autodecline", controller);
    String showCmd = "show status on 2025-05-08T13:30";
    String result = CommandProcessor.process(showCmd, controller);
    assertTrue(result.contains("Busy"));
  }

  /** Tests creating a new calendar command. */
  @Test
  public void testCreateCalendarCommand() throws Exception {
    String cmd = "create calendar --name WorkCalendar --timezone America/New_York";
    String result = CommandProcessor.process(cmd, controller);
    assertTrue(result.contains("Calendar created: WorkCalendar"));
  }

  /** Tests editing a calendar command. */
  @Test
  public void testEditCalendarCommand() throws Exception {
    CommandProcessor.process("create calendar --name TestCal --timezone America/New_York", controller);
    String editCmd = "edit calendar --name TestCal --property timezone Europe/Paris";
    String result = CommandProcessor.process(editCmd, controller);
    assertTrue(result.contains("Calendar TestCal updated: timezone = Europe/Paris"));
  }

  /** Tests using a calendar command. */
  @Test
  public void testUseCalendarCommand() throws Exception {
    CommandProcessor.process("create calendar --name NewCal --timezone Asia/Kolkata", controller);
    String useCmd = "use calendar --name NewCal";
    String result = CommandProcessor.process(useCmd, controller);
    assertTrue(result.contains("Using calendar: NewCal"));
  }

  /** Tests copying a single event command. */
  @Test
  public void testCopyEventCommand() throws Exception {
    CommandProcessor.process("create event CopyTest from 2025-05-09T10:00 to 2025-05-09T11:00 --autodecline", controller);
    CommandProcessor.process("create calendar --name TargetCal --timezone Europe/London", controller);
    String copyCmd = "copy event CopyTest on 2025-05-09T10:00 --target TargetCal to 2025-05-10T10:00";
    String result = CommandProcessor.process(copyCmd, controller);
    assertTrue(result.contains("Event CopyTest copied to calendar TargetCal"));
  }

  /** Tests copying all events on a specific day command. */
  @Test
  public void testCopyEventsOnCommand() throws Exception {
    CommandProcessor.process("create event DayEvent on 2025-05-10 --autodecline", controller);
    CommandProcessor.process("create calendar --name TargetCal2 --timezone Europe/London", controller);
    String copyCmd = "copy events on 2025-05-10 --target TargetCal2 to 2025-05-11T00:00";
    String result = CommandProcessor.process(copyCmd, controller);
    assertTrue(result.contains("copied"));
  }

  /** Tests copying events between two dates command. */
  @Test
  public void testCopyEventsBetweenCommand() throws Exception {
    CommandProcessor.process("create event IntervalEvent1 from 2025-05-12T09:00 to 2025-05-12T10:00 --autodecline", controller);
    CommandProcessor.process("create event IntervalEvent2 from 2025-05-12T11:00 to 2025-05-12T12:00 --autodecline", controller);
    CommandProcessor.process("create calendar --name TargetCal3 --timezone Asia/Tokyo", controller);
    String copyCmd = "copy events between 2025-05-12 and 2025-05-12 --target TargetCal3 to 2025-05-13";
    String result = CommandProcessor.process(copyCmd, controller);
    assertTrue(result.contains("copied"));
  }
}
