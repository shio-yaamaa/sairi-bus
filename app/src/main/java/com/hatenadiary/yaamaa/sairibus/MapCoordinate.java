package com.hatenadiary.yaamaa.sairibus;

public class MapCoordinate {

    long id;
    String name;
    String latitude;
    String longitude;

    public MapCoordinate(long id, String name, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;

        MapCoordinateManager.mapCoordinateList.add(this);
    }
}
