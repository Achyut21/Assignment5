# Calendar Application - Documentation

## Overview
This application is a command line based calendar system capable of creating and editing events, querying events by date, and exporting them to CSV. It supports both **interactive** and **headless** modes, handling user commands to manage and organize calendar events.

## Code Flow

1. **CalendarApp (Main Entry Point)**
    - Instantiates the core `CalendarController`.
    - Invokes `controller.run()` to start command processing.

2. **CalendarController**
    - Maintains a reference to the core `Calendar` model.
    - Provides methods for:
        - Creating single or recurring events (timed or all-day).
        - Editing existing events (single or multiple).
        - Querying events (on a date, between dates, or checking busy status).
        - Exporting events to CSV.
    - Interprets the user's chosen mode (interactive or headless) and delegates to the appropriate processing method (`processInteractive` or `processHeadless`).

3. **ConsoleView**
    - Handles console input and output.
    - Displays prompts/messages and retrieves user commands in interactive mode.

4. **CommandProcessor**
    - Parses user commands (create, edit, print, export, show) and delegates to `CalendarController` methods.
    - Validates required tokens, throws exceptions if tokens are missing or invalid.
    - Ensures correct parameters are passed to the controller.

5. **Model Classes**
    - **Calendar**:
        - Stores and manages `Event` objects (e.g. conflict checks, add/edit methods, CSV export).
    - **Event** (interface):
        - Basic event behaviors (getters for name, time, location, etc.).
    - **AbstractCalendarEvent**:
        - Shared fields (name, start/end times, description, location, and public flag).
    - **SingleEvent**:
        - A single, non-recurring event.
    - **RecurringEvent**:
        - Generates multiple instances (`SingleEvent`) based on specified weekdays and either an occurrence count or an until date/time.

6. **Exceptions**
    - **InvalidCommandException**: Thrown when an unrecognized or invalid command is encountered.
    - **InvalidTokenException**: Thrown when a required token (like `to`) is missing.
    - **MissingParameterException**: Thrown when a required parameter is absent (e.g., event name, date/time).

## Input Command Workflow

### Interactive Mode
1. **Startup Prompt**  
   The application asks which mode to use:
    - **1** = Interactive
    - **2** = Headless
    - **3** = Exit

2. **If Interactive**
    - Prompts the user to enter commands (e.g., `create event Meeting from 2025-04-01T10:00 to 2025-04-01T11:00 --autodecline`).
    - Reads each command line via `ConsoleView`, then calls `CommandProcessor.process` to parse and delegate to the `CalendarController`.
    - Print or display results (e.g., event creation success messages or listing events).
    - Type `exit` to end the interactive session and return to the main mode prompt.

### Headless Mode
1. **Startup Prompt**
    - **2** = Headless
    - The application asks for a file path containing commands.

2. **File-based Commands**
    - Each line in the file is processed sequentially by `CommandProcessor`.
    - The final `exit` command stops execution.

### Common Commands Examples
- **Create Single Timed Event**  
  `create event Meeting from 2025-04-01T10:00 to 2025-04-01T11:00 --autodecline`
- **Create Single All-Day Event**  
  `create event Vacation on 2025-04-10`
- **Edit Single Event**  
  `edit event description Meeting from 2025-04-01T10:00 to 2025-04-01T11:00 with UpdatedDesc`
- **Print Events on a Date**  
  `print events on 2025-04-01`
- **Show Busy Status**  
  `show status on 2025-04-01T10:30`
- **Export to CSV**  
  `export cal my_calendar.csv`