package com.mermix.model;

import com.mermix.model.common.Pojo;

import java.io.Serializable;

/**
 * Created by vasilis on 2/4/16.
 */
public class BookEquipment extends Pojo implements Serializable {
    private String status;
    private String created_user;
    private String message;

    public String getCreated_user() {
        return created_user;
    }

    public void setCreated_user(String created_user) {
        this.created_user = created_user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
