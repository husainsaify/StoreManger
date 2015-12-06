package com.hackerkernel.storemanager.pojo;

public class LoginPojo {
    private Boolean returned;
    private int id;
    private String  message,
                    name,
                    email,
                    phone,
                    password,
                    registerAt,
                    lastBillPaid,
                    nextDueDate,
                    active;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getReturned() {
        return returned;
    }

    public void setReturned(Boolean returned) {
        this.returned = returned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterAt() {
        return registerAt;
    }

    public void setRegisterAt(String registerAt) {
        this.registerAt = registerAt;
    }

    public String getLastBillPaid() {
        return lastBillPaid;
    }

    public void setLastBillPaid(String lastBillPaid) {
        this.lastBillPaid = lastBillPaid;
    }

    public String getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(String nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
