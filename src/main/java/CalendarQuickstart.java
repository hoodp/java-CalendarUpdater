import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CalendarQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Calendar API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
									System.getProperty("user.home"), ".credentials/calendar-api-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
						Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    /** service used to get calendar info */
    private com.google.api.services.calendar.Calendar service;

    /** list stores user calendars */
    private List<CalendarListEntry> calendars;

    /** scanner for reading system input */
    private Scanner in;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            CalendarQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
	    new GoogleAuthorizationCodeFlow.Builder(
						    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
	    .setDataStoreFactory(DATA_STORE_FACTORY)
	    .setAccessType("offline")
	    .build();
        Credential credential = new AuthorizationCodeInstalledApp(
								  flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
			   "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
								     HTTP_TRANSPORT, JSON_FACTORY, credential)
	    .setApplicationName(APPLICATION_NAME)
	    .build();
    }

    public CalendarQuickstart() {
        try {

            // setup the scanner
            in = new Scanner(System.in);

            // setup google calendar service
            service = getCalendarService();

            // set google calendars
            calendars = setCalendars();

            // start running calendar
            listCalEvents();

            // run until done viewing calendar info
            while(goBack()) {
                listCalEvents();
            }

            // close the scanner
            in.close();
        } catch (Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }
    }

    private boolean goBack() {
        System.out.print("Go back [Y/n]: ");

        // read input & convert result to uppercase
        String result = in.next().toUpperCase();

        // check if first char is Y
        return result.charAt(0) == 'Y';
    }

    private void listCalEvents() throws IOException {

        // get calendar number
        int calNum = getCalendarNum();

        // list the calendar events
        listEvents(calNum);
    }

    private List<CalendarListEntry> setCalendars() throws IOException {
        return service.calendarList().list().setPageToken(null).execute()
                .getItems();
    }

    private int getCalendarNum() throws IOException {

        // display prompt
        System.out.println("Calendars:");

        // display each calendar name
        for (int i = 0; i < calendars.size(); i++) {
            System.out.printf("%s: %s\n", i + 1, calendars.get(i).getSummary());
        }

        // open scanner & read calendar number
        System.out.print("Enter calendar #: ");
        return in.nextInt() - 1;
    }

    private void listEvents(int calNum) throws IOException {

        // get the selected calendar info
        Events events = service.events().list(calendars.get(calNum)
                .getId()).execute();
        for (Event e : events.getItems()) {
            System.out.println(e.getSummary());
        }
    }

    public static void main(String[] args) {
        new CalendarQuickstart();
    }
}
