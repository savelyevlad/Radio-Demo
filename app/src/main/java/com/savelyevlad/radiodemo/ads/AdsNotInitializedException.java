package com.savelyevlad.radiodemo.ads;

public class AdsNotInitializedException extends RuntimeException {

    @Override
    public String getMessage() {
        return "The AdsRunner class is not initialized. Please initialize it with initialize() method";
    }
}
