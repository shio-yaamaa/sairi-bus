package com.hatenadiary.yaamaa.sairibus;

public class Classwork {

    long id;
    String name;
    int[] times;

    public Classwork(long id, String name, int[] times) {
        this.id = id;
        this.name = name;
        this.times = times;

        ClassworkManager.classworkList.add(this);

        Constants.entryTimeRange(times[0], times[1]);
    }
}
