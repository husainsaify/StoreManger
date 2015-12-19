package com.hackerkernel.storemanager.pojo;

public class SalesTrackerPojo {
    private String message,
            sellId,
            quantity,
            price_per,
            productId,
            productName,
            productCode,
            productCp,
            productSp,
            currentSales,
            currentCp,
            totalSales,
            totalCp;
    private boolean returned;

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public String getSellId() {
                return sellId;
        }

        public void setSellId(String sellId) {
                this.sellId = sellId;
        }

        public String getQuantity() {
                return quantity;
        }

        public void setQuantity(String quantity) {
                this.quantity = quantity;
        }

        public String getPrice_per() {
                return price_per;
        }

        public void setPrice_per(String price_per) {
                this.price_per = price_per;
        }

        public String getProductId() {
                return productId;
        }

        public void setProductId(String productId) {
                this.productId = productId;
        }

        public String getProductName() {
                return productName;
        }

        public void setProductName(String productName) {
                this.productName = productName;
        }

        public String getProductCode() {
                return productCode;
        }

        public void setProductCode(String productCode) {
                this.productCode = productCode;
        }

        public String getProductCp() {
                return productCp;
        }

        public void setProductCp(String productCp) {
                this.productCp = productCp;
        }

        public String getProductSp() {
                return productSp;
        }

        public void setProductSp(String productSp) {
                this.productSp = productSp;
        }

        public String getCurrentSales() {
                return currentSales;
        }

        public void setCurrentSales(String currentSales) {
                this.currentSales = currentSales;
        }

        public String getCurrentCp() {
                return currentCp;
        }

        public void setCurrentCp(String currentCp) {
                this.currentCp = currentCp;
        }

        public String getTotalSales() {
                return totalSales;
        }

        public void setTotalSales(String totalSales) {
                this.totalSales = totalSales;
        }

        public String getTotalCp() {
                return totalCp;
        }

        public void setTotalCp(String totalCp) {
                this.totalCp = totalCp;
        }

        public boolean isReturned() {
                return returned;
        }

        public void setReturned(boolean returned) {
                this.returned = returned;
        }
}
