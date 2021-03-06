package com.bbld.warehouse.bean;

import java.util.List;

/**
 * Created by likey on 2017/11/17.
 */

public class FHDGetOtherProviceFHDInfo {
    /** "status": 0,
     "mes": "成功",
     "OrderId": 161,
     "OrderCode": "XSDD-107-20171117-1",
     "DealerName": "郑州地办",
     "DealerRemark": "地板办提报",
     "HeaderRemark": "sg",
     "DealerWarehouseId": 21,
     "DealerWarehouseList": []
     "FhdCode": "XSFH-21-20171117-1",
     "FhdRemark": "1",
     "FhdProList": []
     "ReceiveDealerId": 107,
     "ReceiveDeliveryId": 25
     "ReceiveDealerList": []
     "ReceiveDeliveryList": []
     */
    private int status;
    private String mes;
    private int OrderId;
    private String OrderCode;
    private String DealerName;
    private String DealerRemark;
    private String HeaderRemark;
    private int DealerWarehouseId;
    private List<FHDDealerWarehouseList> DealerWarehouseList;
    private String FhdCode;
    private String FhdRemark;
    private List<FHDFhdProList> FhdProList;
    private int ReceiveDealerId;
    private int ReceiveDeliveryId;
    private List<FHDReceiveDealerList> ReceiveDealerList;
    private List<FHDReceiveDeliveryList> ReceiveDeliveryList;

    public List<FHDReceiveDealerList> getReceiveDealerList() {
        return ReceiveDealerList;
    }

    public void setReceiveDealerList(List<FHDReceiveDealerList> receiveDealerList) {
        ReceiveDealerList = receiveDealerList;
    }

    public List<FHDReceiveDeliveryList> getReceiveDeliveryList() {
        return ReceiveDeliveryList;
    }

    public void setReceiveDeliveryList(List<FHDReceiveDeliveryList> receiveDeliveryList) {
        ReceiveDeliveryList = receiveDeliveryList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getOrderCode() {
        return OrderCode;
    }

    public void setOrderCode(String orderCode) {
        OrderCode = orderCode;
    }

    public String getDealerName() {
        return DealerName;
    }

    public void setDealerName(String dealerName) {
        DealerName = dealerName;
    }

    public String getDealerRemark() {
        return DealerRemark;
    }

    public void setDealerRemark(String dealerRemark) {
        DealerRemark = dealerRemark;
    }

    public String getHeaderRemark() {
        return HeaderRemark;
    }

    public void setHeaderRemark(String headerRemark) {
        HeaderRemark = headerRemark;
    }

    public int getDealerWarehouseId() {
        return DealerWarehouseId;
    }

    public void setDealerWarehouseId(int dealerWarehouseId) {
        DealerWarehouseId = dealerWarehouseId;
    }

    public List<FHDDealerWarehouseList> getDealerWarehouseList() {
        return DealerWarehouseList;
    }

    public void setDealerWarehouseList(List<FHDDealerWarehouseList> dealerWarehouseList) {
        DealerWarehouseList = dealerWarehouseList;
    }

    public String getFhdCode() {
        return FhdCode;
    }

    public void setFhdCode(String fhdCode) {
        FhdCode = fhdCode;
    }

    public String getFhdRemark() {
        return FhdRemark;
    }

    public void setFhdRemark(String fhdRemark) {
        FhdRemark = fhdRemark;
    }

    public List<FHDFhdProList> getFhdProList() {
        return FhdProList;
    }

    public void setFhdProList(List<FHDFhdProList> fhdProList) {
        FhdProList = fhdProList;
    }

    public int getReceiveDealerId() {
        return ReceiveDealerId;
    }

    public void setReceiveDealerId(int receiveDealerId) {
        ReceiveDealerId = receiveDealerId;
    }

    public int getReceiveDeliveryId() {
        return ReceiveDeliveryId;
    }

    public void setReceiveDeliveryId(int receiveDeliveryId) {
        ReceiveDeliveryId = receiveDeliveryId;
    }

    public static class FHDDealerWarehouseList{
        /**"Id": 21,
         "Name": "河南库房"*/
        private int Id;
        private String Name;

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }

    public static class FHDFhdProList{
        /**"Id": 155,
         "ProductId": 12,
         "Name": "优智DHA藻油凝胶糖果（90粒550mg）",
         "PackSpecifications": 8,
         "Code": "0101004",
         "Unit": 1,
         "ProductAmount": 8,
         "GiveAmount": 0,
         "ProductPrice": 0.0,
         "DeliveryCount": 8,
         "DeliveryGiveCount": 0,
         "DeliveryAmount": 8,
         "DeliverGiveAmount": 0
         "Logo": "http://manager.xiuzheng.cc//UploadFile/2eb2efd9458c481a951bbf4c4302c384.jpg",
         "ProSpecifications": "0.55g/粒*10粒/板*3板/袋*3袋/盒*8盒/件"*/
        private int Id;
        private int ProductId;
        private String Name;
        private int PackSpecifications;
        private String Code;
        private int Unit;
        private int ProductAmount;
        private int GiveAmount;
        private float ProductPrice;
        private int DeliveryCount;
        private int DeliveryGiveCount;
        private int DeliveryAmount;
        private int DeliverGiveAmount;
        private String Logo;
        private String ProSpecifications;

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public int getProductId() {
            return ProductId;
        }

        public void setProductId(int productId) {
            ProductId = productId;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public int getPackSpecifications() {
            return PackSpecifications;
        }

        public void setPackSpecifications(int packSpecifications) {
            PackSpecifications = packSpecifications;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public int getUnit() {
            return Unit;
        }

        public void setUnit(int unit) {
            Unit = unit;
        }

        public int getProductAmount() {
            return ProductAmount;
        }

        public void setProductAmount(int productAmount) {
            ProductAmount = productAmount;
        }

        public int getGiveAmount() {
            return GiveAmount;
        }

        public void setGiveAmount(int giveAmount) {
            GiveAmount = giveAmount;
        }

        public float getProductPrice() {
            return ProductPrice;
        }

        public void setProductPrice(float productPrice) {
            ProductPrice = productPrice;
        }

        public int getDeliveryCount() {
            return DeliveryCount;
        }

        public void setDeliveryCount(int deliveryCount) {
            DeliveryCount = deliveryCount;
        }

        public int getDeliveryGiveCount() {
            return DeliveryGiveCount;
        }

        public void setDeliveryGiveCount(int deliveryGiveCount) {
            DeliveryGiveCount = deliveryGiveCount;
        }

        public int getDeliveryAmount() {
            return DeliveryAmount;
        }

        public void setDeliveryAmount(int deliveryAmount) {
            DeliveryAmount = deliveryAmount;
        }

        public int getDeliverGiveAmount() {
            return DeliverGiveAmount;
        }

        public void setDeliverGiveAmount(int deliverGiveAmount) {
            DeliverGiveAmount = deliverGiveAmount;
        }

        public String getLogo() {
            return Logo;
        }

        public void setLogo(String logo) {
            Logo = logo;
        }

        public String getProSpecifications() {
            return ProSpecifications;
        }

        public void setProSpecifications(String proSpecifications) {
            ProSpecifications = proSpecifications;
        }
    }
    public static class FHDReceiveDealerList{
        /**"Id": 107,
         "Name": "郑州地办"*/
        private int Id;
        private String Name;

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }
    public static class FHDReceiveDeliveryList{
        /**"Id": 25,
         "Name": "郑州地办",
         "Address": "郑州市区",
         "Phone": "13200000000",
         "Areas": "河南省.郑州市.管城回族区"*/
        private int Id;
        private String Name;
        private String Address;
        private String Phone;
        private String Areas;

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String phone) {
            Phone = phone;
        }

        public String getAreas() {
            return Areas;
        }

        public void setAreas(String areas) {
            Areas = areas;
        }
    }
}
