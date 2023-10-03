package com.dichthuatjun88binh.jun88.model;

public class DataBaseModel {
    int id;
    String source_language_jun;
    String source_language_txt;
    String target_language;
    String target_language_txt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource_language_jun() {
        return source_language_jun;
    }

    public void setSource_language_jun(String source_language_jun) {
        this.source_language_jun = source_language_jun;
    }

    public String getSource_language_txt() {
        return source_language_txt;
    }

    public void setSource_language_txt(String source_language_txt) {
        this.source_language_txt = source_language_txt;
    }

    public String getTarget_language() {
        return target_language;
    }

    public void setTarget_language(String target_language) {
        this.target_language = target_language;
    }

    public String getTarget_language_txt() {
        return target_language_txt;
    }

    public void setTarget_language_txt(String target_language_txt) {
        this.target_language_txt = target_language_txt;
    }
}
