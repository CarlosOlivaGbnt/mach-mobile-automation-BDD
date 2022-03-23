package com.mach.core.model;

public class Instructions {
    private String title;
    private String description;
    private String type;

    public Instructions() {
        super();
    }

    public Instructions(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Instructions(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
