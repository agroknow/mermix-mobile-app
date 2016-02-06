package com.realestate.model;

import com.realestate.model.common.Pojo;

import java.io.Serializable;

/**
 * Created by vasilis on 2/4/16.
 */
public class BookEquipment extends Pojo implements Serializable {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
