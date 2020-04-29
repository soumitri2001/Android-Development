package com.appdevSoumitri.smiledetector;

public class FaceDetectorModel
{
    private int id;
    private String text;

    public FaceDetectorModel(int id,String text)
    {
        this.id=id;
        this.text=text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}
