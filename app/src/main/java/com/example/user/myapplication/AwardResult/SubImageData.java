package com.example.user.myapplication.AwardResult;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by User on 2016-10-14.
 */
public class SubImageData {

    String award_id;
    String user_id;
    String sub_img;

    public String getSub_img() {
        return sub_img;
    }

    public void setSub_img(String sub_img) {
        this.sub_img = sub_img;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAward_id() {
        return award_id;
    }

    public void setAward_id(String award_id) {
        this.award_id = award_id;
    }
}
