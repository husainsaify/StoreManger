package com.hackerkernel.storemanager.pojo;

/**
 * POJO class to hold Singup parse details
 */
public class SignupPojo {
    private String userId;
    private String message;
    private boolean returned;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
