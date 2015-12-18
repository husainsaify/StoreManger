package com.hackerkernel.storemanager.pojo;

/**
 * A plain java object to hold sales tacker date
 */
public class STdatePojo {
    private String  date,
                    dateId,
                    message;
    private boolean returned;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
