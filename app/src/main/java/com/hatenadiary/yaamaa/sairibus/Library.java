package com.hatenadiary.yaamaa.sairibus;

public class Library {

    long id;
    String name;
    int[] times;

    public Library(long id, String name, int[] times) {
        this.id = id;
        this.name = name;
        this.times = times;

        LibraryManager.libraryList.add(this);

        if (times[0] != -1) {
            Constants.entryTimeRange(times[0], times[1]);
        }
    }
}
