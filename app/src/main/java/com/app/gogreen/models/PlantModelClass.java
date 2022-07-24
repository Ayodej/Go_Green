package com.app.gogreen.models;

public class PlantModelClass {
    private String id;
    private String picUrl;
    private String title;
    private String price;
    private String description;
    private String flag;

    public PlantModelClass() { }

    public PlantModelClass(String id, String picUrl, String title, String price, String description, String flag) {
        this.id = id;
        this.picUrl = picUrl;
        this.title = title;
        this.price = price;
        this.description = description;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getFlag() {
        return flag;
    }
}
