import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map;

public class CalendarApp {

    public static void main(String[] args) {

        // TODO: read arguments from command line. planning to implement an option
        // add a file or url

        // connect to google calendar
        Connect conn = new Connect();
        com.google.api.services.calendar.Calendar service = conn.getService();

        // setup map that stores calendars by id
        Map<String, CalendarModel> calendars =
                new HashMap<String, CalendarModel>();

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
                calendars.put(id, new CalendarModel(name, id, events));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //
    }
}
