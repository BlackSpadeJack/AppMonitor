package com.app.monitor.core;

public class InfoModel {
    private String mDateTime; // 当前页面发生时间
    private String TopActivity; // 顶层的activiy;
    private String heapData; // 内存大小
    private String pMemory; // 应用占用大小
    private String percent; // 占比
    private String fMemory; // 剩余内存大小
    private String processCpuRatio;
    private StringBuffer totalCpuBuffer;
    private String trafValue;
    private String totalBatt;// 流量
    private String currentBatt;// 电量
    private String temperature;// 温度
    private String voltage;// 电压
    private String fps;

    public String getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    public String getTopActivity() {
        return TopActivity;
    }

    public void setTopActivity(String topActivity) {
        TopActivity = topActivity;
    }

    public String getHeapData() {
        return heapData;
    }

    public void setHeapData(String heapData) {
        this.heapData = heapData;
    }

    public String getpMemory() {
        return pMemory;
    }

    public void setpMemory(String pMemory) {
        this.pMemory = pMemory;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getfMemory() {
        return fMemory;
    }

    public void setfMemory(String fMemory) {
        this.fMemory = fMemory;
    }

    public String getProcessCpuRatio() {
        return processCpuRatio;
    }

    public void setProcessCpuRatio(String processCpuRatio) {
        this.processCpuRatio = processCpuRatio;
    }

    public StringBuffer getTotalCpuBuffer() {
        return totalCpuBuffer;
    }

    public void setTotalCpuBuffer(StringBuffer totalCpuBuffer) {
        this.totalCpuBuffer = totalCpuBuffer;
    }

    public String getTrafValue() {
        return trafValue;
    }

    public void setTrafValue(String trafValue) {
        this.trafValue = trafValue;
    }

    public String getTotalBatt() {
        return totalBatt;
    }

    public void setTotalBatt(String totalBatt) {
        this.totalBatt = totalBatt;
    }

    public String getCurrentBatt() {
        return currentBatt;
    }

    public void setCurrentBatt(String currentBatt) {
        this.currentBatt = currentBatt;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }


}
