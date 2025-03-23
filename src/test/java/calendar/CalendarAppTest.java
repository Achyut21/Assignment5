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
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/** Test Class for CalendarAPP. */
public class CalendarAppTest {

  private CalendarController controller;
  private SingleEvent event;

  /** Sets up test fixtures. */
  @Before
  public void setup() {
    Calendar calendar = new Calendar();
    controller = new CalendarController(calendar);
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
}
