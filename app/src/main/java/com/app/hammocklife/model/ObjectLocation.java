package com.app.hammocklife.model;

public class ObjectLocation {
    double _lat, _lng;

    public ObjectLocation(double _lat, double _lng) {
        this._lat = _lat;
        this._lng = _lng;
    }

    public double get_lat() {
        return _lat;
    }

    public void set_lat(double _lat) {
        this._lat = _lat;
    }

    public double get_lng() {
        return _lng;
    }

    public void set_lng(double _lng) {
        this._lng = _lng;
    }
}
