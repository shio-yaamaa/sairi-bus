package com.hatenadiary.yaamaa.sairibus;

public class Bus {

    long id;
    String name;
    int section;
    int direction;
    int[] times;
    boolean twoBuses;
    boolean microDisease;
    int lane;

    public Bus(long id, String name, int section, int direction, int[] times, boolean twoBuses, boolean microDisease, int lane) {
        this.id = id;
        this.name = name;
        this.section = section;
        this.direction = direction;
        this.times = times;
        this.twoBuses = twoBuses;
        this.microDisease = microDisease;
        this.lane = lane;

        BusManager.busList.add(this);

        Constants.entryTimeRange(times[0], times[times.length - 1]);
    }
}