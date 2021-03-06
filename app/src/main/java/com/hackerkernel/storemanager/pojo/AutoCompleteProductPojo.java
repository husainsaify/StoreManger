package com.hackerkernel.storemanager.pojo;

public class AutoCompleteProductPojo {
    private String  message,
                    id,
                    name,
                    code,
                    cp;
    private boolean returned = true;

    private String[] sizeArray;

    private int count = -1;

    public String[] getSizeArray() {
        return sizeArray;
    }

    public void setSizeArray(String[] sizeArray) {
        this.sizeArray = sizeArray;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
