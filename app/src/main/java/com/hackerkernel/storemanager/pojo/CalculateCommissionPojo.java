package com.hackerkernel.storemanager.pojo;

public class CalculateCommissionPojo {
    String message,
            costprice,
            sellingprice,
            noOfItemSold,
            noOfSales;
    boolean returned;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCostprice() {
        return costprice;
    }

    public void setCostprice(String costprice) {
        this.costprice = costprice;
    }

    public String getSellingprice() {
        return sellingprice;
    }

    public void setSellingprice(String sellingprice) {
        this.sellingprice = sellingprice;
    }

    public String getNoOfItemSold() {
        return noOfItemSold;
    }

    public void setNoOfItemSold(String noOfItemSold) {
        this.noOfItemSold = noOfItemSold;
    }

    public String getNoOfSales() {
        return noOfSales;
    }

    public void setNoOfSales(String noOfSales) {
        this.noOfSales = noOfSales;
    }

    public boolean getReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
