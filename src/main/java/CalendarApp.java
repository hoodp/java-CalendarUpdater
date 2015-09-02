import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.lang.Package;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class CalendarApp {

    public static void main(String[] args) {

        // TODO: read arguments from command line. planning to implement an
        // option add a file or url

        // connect to google calendar
        Connect conn = new Connect();
        com.google.api.services.calendar.Calendar service = conn.getService();

        // setup map that stores calendars by id
        List<CalendarModel> calendars = new ArrayList<CalendarModel>();

        // attempt to get information from google calendar service
        try {

            // get list of calendars
            List<CalendarListEntry> calList = service.calendarList().list()
                    .setPageToken(null).execute().getItems();

            // loop through each calendar entry
            for (CalendarListEntry e : calList) {

                // get name & id from the entry
                String name = e.getSummary();
                String id = e.getId();

                // get events from the entry
                Events events = service.events().list(id).execute();

                // create new calendar model & add it to the map
                calendars.add(new CalendarModel(name, id, events));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(calendars.size());

        // display all calendars
        displayCalendars(calendars);

        // scanner for reading user input
        Scanner in = new Scanner(System.in);

        // list of options
        String[] options = {"View", "Edit", "Add", "Upload", "Exit"};

        // ask user what their intention is
        System.out.println("What would you like to do?");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%s: %s\n", i + 1, options[i]);
        }
        System.out.printf("Enter (1-%s): ", options.length);
        int optionIndex = in.nextInt();

        // upload, edit, add, or exit
        switch (optionIndex) {
            case 1:
            case 2:
            case 3:
                displayCalendars(calendars);

                // get calendar id from user
                System.out.printf("Enter id(1-%s): ", calendars.size());
                int calIndex = in.nextInt() - 1;

                // display calendar information until id does not match
                while (calIndex >= 0 && calIndex < calendars.size()) {

                    // display each of the calendars events
                    Events events = calendars.get(calIndex).getEvents();
                    for (Event e: events.getItems()) {
                        System.out.printf("%s\t%s\n", e.getCreated(),
                                e.getSummary());
                    }

                    // update the calendar index
                    System.out.printf("Enter id(1-%s): ", calendars.size());
                    calIndex = in.nextInt() - 1;
                }
                break;
            case 4:
                System.out.print("Enter file path: ");

                // get file path from user
                String path = in.next();

                // choose calendar to upload
                System.out.println("Select Calendar:");

                // get wanted calendar
                displayCalendars(calendars);
                System.out.printf("Enter id(1-%s): ", calendars.size());
                calIndex = in.nextInt() - 1;
                System.out.printf("Uploading %s to %s...\n", path,
                        calendars.get(calIndex).getName());
            default:
                System.exit(0);
        }

        // close the scanner
        in.close();
    }

    private static void displayCalendars(List<CalendarModel> cals) {

        // display each calendar name and its index + 1
        for (int i = 0; i < cals.size(); i++) {
            System.out.printf("%s: %s\n", i + 1, cals.get(i).getName());
        }
    }
}
