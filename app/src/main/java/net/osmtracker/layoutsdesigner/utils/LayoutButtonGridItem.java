package net.osmtracker.layoutsdesigner.utils;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;

public class LayoutButtonGridItem {
    private String itemName;
    private Uri imageURI;
    private String imagePath;
    private Drawable defaultIcon;

    public LayoutButtonGridItem(String name, Uri path){
        this.itemName = name;
        this.imageURI = path;
        this.defaultIcon = null;
    }

    public LayoutButtonGridItem(String name, Drawable icon){
        this.itemName = name;
        this.defaultIcon = icon;
        this.imageURI = null;
    }

    public LayoutButtonGridItem(String name) {
        this.itemName = name;
        this.defaultIcon = null;
        this.imageURI = null;
    }

    public void setItemName(String name){
        itemName = name;
    }

    public void setImageURI(Uri uri){
        imageURI = uri;
    }

    public void setImagePath(String path){
        imagePath = path;
    }

    public Drawable getDefaultIcon(){
        return defaultIcon;
    }

    public String getItemName() {
        return itemName;
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getId(){
        return itemName.hashCode();
    }
}
