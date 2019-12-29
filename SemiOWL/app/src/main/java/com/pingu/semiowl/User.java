package com.pingu.semiowl;

import android.graphics.drawable.Drawable;

public class User {

    private Drawable image;
    private String name;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "image=" + image +
                ", name='" + name + '\'' +
                '}';
    }
}
