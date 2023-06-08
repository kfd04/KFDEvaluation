package com.kar.kfd.gov.kfdsurvey.model;

public class SMCModel {

    String smcWorks;
    double smcCost;
    int smcId;
    int exist;

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public int getSmcId() {
        return smcId;
    }

    public void setSmcId(int smcId) {
        this.smcId = smcId;
    }

    public String getSmcWorks() {
        return smcWorks;
    }

    public void setSmcWorks(String smcWorks) {
        this.smcWorks = smcWorks;
    }

    public double getSmcCost() {
        return smcCost;
    }

    public void setSmcCost(double smcCost) {
        this.smcCost = smcCost;
    }





    public SMCModel(int smcId,String smcWorks, double smcCost, int exist ) {
        this.smcId =smcId;
        this.smcWorks = smcWorks;
        this.smcCost = smcCost;
        this.exist = exist;

    }

    @Override
    public String toString() {

        return "SMCModel{" + "smcWorks='" + smcWorks + '\'' + ", smcCost=" + smcCost + ", smcId=" + smcId + ", exist=" + exist + '}';

    }

}
