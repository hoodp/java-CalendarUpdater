import com.google.api.services.calendar.model.Events;

public class CalendarModel {

    /** stores calendar name */
    private String name;

    /** stores calendar id */
    private String id;

    /** stores calendar events */
    private Events events;

    public CalendarModel(String name, String id, Events events) {
        this.name = name;
        this.id = id;
        this.events = events;
    }

    private void setName(String name) {
	this.name = name;
    }

    private void setId(String id) {
	this.id = id;
    }

    private void setEvents(Events events) {
	this.events = events;
    }

    public String getName () {
	return this.name;
    }

    public String getId() {
	return this.id;
    }

    public Events getEvents() {
	return this.events;
    }
}
