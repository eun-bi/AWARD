package com.example.user.myapplication.MyAward;

import android.graphics.drawable.Drawable;

/**
 * Created by User on 2016-09-13.
 */
public class AwardDetail {

    private String award_id;
    private String img_award;
    private String txtAward_title;
    private String txtAward_date;

    public String getAward_id() {
        return award_id;
    }

    public void setAward_id(String award_id) {
        this.award_id = award_id;
    }

    public String getTxtAward_date() {
        return txtAward_date;
    }

    public void setTxtAward_date(String txtAward_date) {
        this.txtAward_date = txtAward_date;
    }

    public String getTxtAward_title() {
        return txtAward_title;
    }

    public void setTxtAward_title(String txtAward_title) {
        this.txtAward_title = txtAward_title;
    }

    public String getImg_award() {
        return img_award;
    }

    public void setImg_award(String img_award) {
        this.img_award = img_award;
    }
}
