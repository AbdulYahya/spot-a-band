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


    public Event(String name, String ticketMasterId, String url, String localDate, String localTime, String priceRange) {
        this.name = name;
        this.ticketMasterId = ticketMasterId;
        this.url = url;
        this.localDate = localDate;
        this.localTime = localTime;
        this.priceRange = priceRange;
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

        if (id != event.id) return false;
        if (!name.equals(event.name)) return false;
        if (!ticketMasterId.equals(event.ticketMasterId)) return false;
        if (!url.equals(event.url)) return false;
        if (!localDate.equals(event.localDate)) return false;
        if (!localTime.equals(event.localTime)) return false;
        return priceRange != null ? priceRange.equals(event.priceRange) : event.priceRange == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + ticketMasterId.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + localDate.hashCode();
        result = 31 * result + localTime.hashCode();
        result = 31 * result + (priceRange != null ? priceRange.hashCode() : 0);
        return result;
    }
}
