package com.kar.kfd.gov.kfdsurvey.model;

public class KnownSpeciesModel {

    String speciesName;
    int speciesCount;
    int inventory_id;
    int eachSpeciesSurvived;
    int calculatedFailures;

    public KnownSpeciesModel(String speciesName, int speciesCount, int inventory_id, int eachSpeciesSurvived, int calculatedFailures) {
        this.speciesName = speciesName;
        this.speciesCount = speciesCount;
        this.inventory_id = inventory_id;
        this.eachSpeciesSurvived = eachSpeciesSurvived;
        this.calculatedFailures = calculatedFailures;
    }


    public KnownSpeciesModel(String speciesName, int speciesCount, int inventory_id, int eachSpeciesSurvived) {
        this.speciesName = speciesName;
        this.speciesCount = speciesCount;
        this.inventory_id = inventory_id;
        this.eachSpeciesSurvived = eachSpeciesSurvived;
    }

    public int getCalculatedFailures() {
        return calculatedFailures;
    }

    public void setCalculatedFailures(int calculatedFailures) {
        this.calculatedFailures = calculatedFailures;
    }


    public int getEachSpeciesSurvived() {
        return eachSpeciesSurvived;
    }

    public void setEachSpeciesSurvived(int eachSpeciesSurvived) {
        this.eachSpeciesSurvived = eachSpeciesSurvived;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public int getSpeciesCount() {
        return speciesCount;
    }

    public void setSpeciesCount(int speciesCount) {
        this.speciesCount = speciesCount;
    }

    public int getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(int inventory_id) {
        this.inventory_id = inventory_id;
    }
}
