package com.hatenadiary.yaamaa.sairibus;

public class Restaurant {
    long id;
    String name;
    int[] times;

    public Restaurant(long id, RestaurantManager.Campus campus, String name, int[] times) {
        this.id = id;
        this.name = name;
        this.times = times;

        campus.restaurantList.add(this);

        if (times[0] != -1) {
            Constants.entryTimeRange(times[0], times[1]);
        }
    }

}
