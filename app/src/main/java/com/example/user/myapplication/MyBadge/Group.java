package com.example.user.myapplication.MyBadge;

import java.util.ArrayList;

/**
 * Created by User on 2017-08-15.
 */

public class Group {

    private String field;
    private ArrayList<Child> Items;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ArrayList<Child> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Child> items) {
        Items = items;
    }
}
