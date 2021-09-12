package com.example.covidmc;

import java.util.Date;

class Redcomponent<T> {
    final Date timestamp;
    final T redValue;

    Redcomponent(Date timestamp, T redValue) {
        this.timestamp = timestamp;
        this.redValue = redValue;
    }
}
