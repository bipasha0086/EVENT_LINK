package model;

public class Theatre {
    private int theatreId;
    private String name;
    private String area;
    private String mapQuery;

    public Theatre() {
    }

    public Theatre(int theatreId, String name, String area, String mapQuery) {
        this.theatreId = theatreId;
        this.name = name;
        this.area = area;
        this.mapQuery = mapQuery;
    }

    public int getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(int theatreId) {
        this.theatreId = theatreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMapQuery() {
        return mapQuery;
    }

    public void setMapQuery(String mapQuery) {
        this.mapQuery = mapQuery;
    }
}
