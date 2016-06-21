package com.andrewaarondev.countdownclock;

import java.util.ArrayList;

public class ListLoadedEvent {
    private ArrayList<Countdown> countdowns;
    ListLoadedEvent(ArrayList<Countdown> countdowns) {
        this.countdowns = countdowns;
    }
    public ArrayList<Countdown> getCountdowns() {
        return countdowns;
    }
}
