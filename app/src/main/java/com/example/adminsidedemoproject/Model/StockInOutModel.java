package com.example.adminsidedemoproject.Model;

public class StockInOutModel {
    String ProdId,StockDate,StockTime,StockPurchaseSalesNumber,StockPartyName,
            StockQuantity,StockPurchaseSalesPrice,StockTotalAmount,StockAmountReceivedPaid,
            StockDueAmount,StockRemark,StockId,StockType,StockItemName,StockUnitType;

    public StockInOutModel(String prodId, String stockDate,
                           String stockTime, String stockPurchaseSalesNumber,
                           String stockPartyName, String stockQuantity,
                           String stockPurchaseSalesPrice, String stockTotalAmount,
                           String stockAmountReceivedPaid, String stockDueAmount,
                           String stockRemark, String stockId, String stockType,
                           String stockItemName, String stockUnitType) {
        ProdId = prodId;
        StockDate = stockDate;
        StockTime = stockTime;
        StockPurchaseSalesNumber = stockPurchaseSalesNumber;
        StockPartyName = stockPartyName;
        StockQuantity = stockQuantity;
        StockPurchaseSalesPrice = stockPurchaseSalesPrice;
        StockTotalAmount = stockTotalAmount;
        StockAmountReceivedPaid = stockAmountReceivedPaid;
        StockDueAmount = stockDueAmount;
        StockRemark = stockRemark;
        StockId = stockId;
        StockType = stockType;
        StockItemName = stockItemName;
        StockUnitType = stockUnitType;
    }

    public String getProdId() {
        return ProdId;
    }

    public void setProdId(String prodId) {
        ProdId = prodId;
    }

    public String getStockDate() {
        return StockDate;
    }

    public void setStockDate(String stockDate) {
        StockDate = stockDate;
    }

    public String getStockTime() {
        return StockTime;
    }

    public void setStockTime(String stockTime) {
        StockTime = stockTime;
    }

    public String getStockPurchaseSalesNumber() {
        return StockPurchaseSalesNumber;
    }

    public void setStockPurchaseSalesNumber(String stockPurchaseSalesNumber) {
        StockPurchaseSalesNumber = stockPurchaseSalesNumber;
    }

    public String getStockPartyName() {
        return StockPartyName;
    }

    public void setStockPartyName(String stockPartyName) {
        StockPartyName = stockPartyName;
    }

    public String getStockQuantity() {
        return StockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        StockQuantity = stockQuantity;
    }

    public String getStockPurchaseSalesPrice() {
        return StockPurchaseSalesPrice;
    }

    public void setStockPurchaseSalesPrice(String stockPurchaseSalesPrice) {
        StockPurchaseSalesPrice = stockPurchaseSalesPrice;
    }

    public String getStockTotalAmount() {
        return StockTotalAmount;
    }

    public void setStockTotalAmount(String stockTotalAmount) {
        StockTotalAmount = stockTotalAmount;
    }

    public String getStockAmountReceivedPaid() {
        return StockAmountReceivedPaid;
    }

    public void setStockAmountReceivedPaid(String stockAmountReceivedPaid) {
        StockAmountReceivedPaid = stockAmountReceivedPaid;
    }

    public String getStockDueAmount() {
        return StockDueAmount;
    }

    public void setStockDueAmount(String stockDueAmount) {
        StockDueAmount = stockDueAmount;
    }

    public String getStockRemark() {
        return StockRemark;
    }

    public void setStockRemark(String stockRemark) {
        StockRemark = stockRemark;
    }

    public String getStockId() {
        return StockId;
    }

    public void setStockId(String stockId) {
        StockId = stockId;
    }

    public String getStockType() {
        return StockType;
    }

    public void setStockType(String stockType) {
        StockType = stockType;
    }

    public String getStockItemName() {
        return StockItemName;
    }

    public void setStockItemName(String stockItemName) {
        StockItemName = stockItemName;
    }

    public String getStockUnitType() {
        return StockUnitType;
    }

    public void setStockUnitType(String stockUnitType) {
        StockUnitType = stockUnitType;
    }
}
