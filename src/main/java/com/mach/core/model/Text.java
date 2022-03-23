package com.mach.core.model;

import java.util.List;

public class Text {
    private String key;
    private List<String> values;
    private List<String> tags;

    public Text() {
        super();
    }

    public Text(final String key, final List<String> values, final List<String> tags) {
        this.key = key;
        this.values = values;
        this.tags = tags;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
