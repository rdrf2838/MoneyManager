package com.example.moneymanager.utility;

public class Payment {
    int _id;
    double _cost;
    String _description;
    int _unixtime;

    @Override
    public String toString() {
        return "Payment{" +
                "_id=" + _id +
                ", _cost=" + _cost +
                ", _description='" + _description + '\'' +
                ", _unixtime=" + _unixtime +
                '}';
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double get_cost() {
        return _cost;
    }

    public void set_cost(double _cost) {
        this._cost = _cost;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public int get_unixtime() {
        return _unixtime;
    }

    public String get_dmytime() {
        return Helper.unixToDMY(_unixtime);
    }

    public void set_unixtime(int _unixtime) {
        this._unixtime = _unixtime;
    }

    public Payment(int _id, double _cost, String _description, int _unixtime) {
        this._id = _id;
        this._cost = _cost;
        this._description = _description;
        this._unixtime = _unixtime;
    }
    public Payment(int _id, double _cost, String _description) {
        this._id = _id;
        this._cost = _cost;
        this._description = _description;
        this._unixtime = Helper.getUnixTime();
    }

    public Payment(double _cost, String _description) {
        this._cost = _cost;
        this._description = _description;
        this._unixtime = Helper.getUnixTime();
    }

    public Payment() {}
}
