package models;

/**
 * Created by Guest on 1/29/18.
 */
public class Event {
    private int id;
    private String name;
    private String ticketMasterId;
    private String url;
    private String localDate;
    private String localTime;
    private String priceRange;


    public Event(String name, String ticketMasterId, String url, String localDate, String localTime) {
        this.name = name;
        this.ticketMasterId = ticketMasterId;
        this.url = url;
        this.localDate = localDate;
        this.localTime = localTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicketMasterId() {
        return ticketMasterId;
    }

    public void setTicketMasterId(String ticketMasterId) {
        this.ticketMasterId = ticketMasterId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!name.equals(event.name)) return false;
        if (!ticketMasterId.equals(event.ticketMasterId)) return false;
        if (!url.equals(event.url)) return false;
        if (localDate != null ? !localDate.equals(event.localDate) : event.localDate != null) return false;
        return localTime != null ? localTime.equals(event.localTime) : event.localTime == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + ticketMasterId.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + (localDate != null ? localDate.hashCode() : 0);
        result = 31 * result + (localTime != null ? localTime.hashCode() : 0);
        return result;
    }
}
