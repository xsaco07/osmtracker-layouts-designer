package net.osmtracker.layoutsdesigner.utils;

public class ItemListMain {
    private String layoutCreatedName;
    private String layoutCreatedDescription;

    /**
     * This method initialize a list item with two attributes (name, description)
     * @param name
     * @param description
     */
    public ItemListMain(String name, String description){
        this.layoutCreatedName = name;
        this.layoutCreatedDescription = description;
    }

    public String getLayoutCreatedName() {
        return layoutCreatedName;
    }

    public String getLayoutCreatedDescription() {
        return layoutCreatedDescription;
    }
}
