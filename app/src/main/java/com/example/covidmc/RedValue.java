package com.example.covidmc;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

class RedValue {
    private final CopyOnWriteArrayList<Redcomponent<Integer>> mRedcomponents = new CopyOnWriteArrayList<>();
    private int minimum = 2147483647;
    private int maximum = -2147483648;

    void add(int measurement) {
        Redcomponent<Integer> redcomponentWithDate = new Redcomponent<>(new Date(), measurement);

        mRedcomponents.add(redcomponentWithDate);
        if (measurement < minimum) minimum = measurement;
        if (measurement > maximum) maximum = measurement;
    }

    CopyOnWriteArrayList<Redcomponent<Integer>> getWindow(int windowSz) {
        if (windowSz < mRedcomponents.size()) {
            return  new CopyOnWriteArrayList<>(mRedcomponents.subList(mRedcomponents.size() - 1 - windowSz, mRedcomponents.size() - 1));
        } else {
            return mRedcomponents;
        }
    }

    Date getLastTimestamp() {
        return mRedcomponents.get(mRedcomponents.size() - 1).timestamp;
    }
}
