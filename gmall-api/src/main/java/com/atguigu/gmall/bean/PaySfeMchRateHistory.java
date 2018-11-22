package com.atguigu.gmall.bean;

public class PaySfeMchRateHistory {


    private Long id;
    private String merchantId;
    private String productCode;
    private String serviceCode;
    private String subServiceCode;
    private String rateType;
    private int ratePercent;
    private int rateMaxValue;
    private int rateMinValue;
    private int rateFixedValue;
    private int physicsFlag;
    private long payCenterId;
    private String beginEffectiveTime;
    private String endEffectiveTime;
    private String remark;
    private String createTime;
    private String updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getSubServiceCode() {
        return subServiceCode;
    }

    public void setSubServiceCode(String subServiceCode) {
        this.subServiceCode = subServiceCode;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public int getRatePercent() {
        return ratePercent;
    }

    public void setRatePercent(int ratePercent) {
        this.ratePercent = ratePercent;
    }

    public int getRateMaxValue() {
        return rateMaxValue;
    }

    public void setRateMaxValue(int rateMaxValue) {
        this.rateMaxValue = rateMaxValue;
    }

    public int getRateMinValue() {
        return rateMinValue;
    }

    public void setRateMinValue(int rateMinValue) {
        this.rateMinValue = rateMinValue;
    }

    public int getRateFixedValue() {
        return rateFixedValue;
    }

    public void setRateFixedValue(int rateFixedValue) {
        this.rateFixedValue = rateFixedValue;
    }

    public int getPhysicsFlag() {
        return physicsFlag;
    }

    public void setPhysicsFlag(int physicsFlag) {
        this.physicsFlag = physicsFlag;
    }

    public long getPayCenterId() {
        return payCenterId;
    }

    public void setPayCenterId(long payCenterId) {
        this.payCenterId = payCenterId;
    }

    public String getBeginEffectiveTime() {
        return beginEffectiveTime;
    }

    public void setBeginEffectiveTime(String beginEffectiveTime) {
        this.beginEffectiveTime = beginEffectiveTime;
    }

    public String getEndEffectiveTime() {
        return endEffectiveTime;
    }

    public void setEndEffectiveTime(String endEffectiveTime) {
        this.endEffectiveTime = endEffectiveTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
