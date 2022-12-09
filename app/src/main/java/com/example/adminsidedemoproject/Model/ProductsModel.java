package com.example.adminsidedemoproject.Model;

public class ProductsModel {

    String  AvailableStock, Category,
            LowQuantity,MrpPrice, ProdId,
            ProdImage, ProductDescription,ProductName,
            ProductPurchasePrice,SellPrice,Timestamp,
            UnitType,UpdateTime,UploadTime,StockIn,StockOut,OpeningStock;

    public ProductsModel(String availableStock,
                         String category, String lowQuantity,
                         String mrpPrice, String prodId,
                         String prodImage, String productDescription,
                         String productName, String productPurchasePrice,
                         String sellPrice, String timestamp, String unitType,
                         String updateTime, String uploadTime, String stockIn,
                         String stockOut, String openingStock) {
        AvailableStock = availableStock;
        Category = category;
        LowQuantity = lowQuantity;
        MrpPrice = mrpPrice;
        ProdId = prodId;
        ProdImage = prodImage;
        ProductDescription = productDescription;
        ProductName = productName;
        ProductPurchasePrice = productPurchasePrice;
        SellPrice = sellPrice;
        Timestamp = timestamp;
        UnitType = unitType;
        UpdateTime = updateTime;
        UploadTime = uploadTime;
        StockIn = stockIn;
        StockOut = stockOut;
        OpeningStock = openingStock;
    }

    public String getOpeningStock() {
        return OpeningStock;
    }

    public void setOpeningStock(String openingStock) {
        OpeningStock = openingStock;
    }

    public String getAvailableStock() {
        return AvailableStock;
    }

    public String getStockIn() {
        return StockIn;
    }

    public void setStockIn(String stockIn) {
        StockIn = stockIn;
    }

    public String getStockOut() {
        return StockOut;
    }

    public void setStockOut(String stockOut) {
        StockOut = stockOut;
    }

    public void setAvailableStock(String availableStock) {
        AvailableStock = availableStock;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getLowQuantity() {
        return LowQuantity;
    }

    public void setLowQuantity(String lowQuantity) {
        LowQuantity = lowQuantity;
    }

    public String getMrpPrice() {
        return MrpPrice;
    }

    public void setMrpPrice(String mrpPrice) {
        MrpPrice = mrpPrice;
    }

    public String getProdId() {
        return ProdId;
    }

    public void setProdId(String prodId) {
        ProdId = prodId;
    }

    public String getProdImage() {
        return ProdImage;
    }

    public void setProdImage(String prodImage) {
        ProdImage = prodImage;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPurchasePrice() {
        return ProductPurchasePrice;
    }

    public void setProductPurchasePrice(String productPurchasePrice) {
        ProductPurchasePrice = productPurchasePrice;
    }

    public String getSellPrice() {
        return SellPrice;
    }

    public void setSellPrice(String sellPrice) {
        SellPrice = sellPrice;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getUnitType() {
        return UnitType;
    }

    public void setUnitType(String unitType) {
        UnitType = unitType;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public String getUploadTime() {
        return UploadTime;
    }

    public void setUploadTime(String uploadTime) {
        UploadTime = uploadTime;
    }
}
