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
    private String venue;
    private String venueUrl;
    private String image;


    public Event(String name, String ticketMasterId, String url, String localDate, String localTime, String priceRange, String venue, String venueUrl, String image) {
        this.name = name;
        this.ticketMasterId = ticketMasterId;
        this.url = url;
        this.localDate = localDate;
        this.localTime = localTime;
        this.priceRange = priceRange;
        this.venue = venue;
        this.venueUrl = venueUrl;
        this.image = image;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getVenueUrl() {
        return venueUrl;
    }

    public void setVenueUrl(String venueUrl) {
        this.venueUrl = venueUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!name.equals(event.name)) return false;
        if (!ticketMasterId.equals(event.ticketMasterId)) return false;
        if (!url.equals(event.url)) return false;
        if (!localDate.equals(event.localDate)) return false;
        return localTime.equals(event.localTime);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + ticketMasterId.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + localDate.hashCode();
        result = 31 * result + localTime.hashCode();
        return result;
    }
}
