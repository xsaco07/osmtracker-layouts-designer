package net.osmtracker.layoutsdesigner.utils;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class LayoutButtonGridItem {
    private String itemName;
    private Uri imagePath;
    private Drawable defaultIcon;

    public LayoutButtonGridItem(String name, Uri path){
        this.itemName = name;
        this.imagePath = path;
        this.defaultIcon = null;
    }

    public LayoutButtonGridItem(String name, Drawable icon){
        this.itemName = name;
        this.defaultIcon = icon;
        this.imagePath = null;
    }

    public LayoutButtonGridItem(String name) {
        this.itemName = name;
        this.defaultIcon = null;
        this.imagePath = null;
    }

    public Drawable getDefaultIcon(){
        return defaultIcon;
    }

    public String getItemName() {
        return itemName;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public int getId(){
        return itemName.hashCode();
    }
}
