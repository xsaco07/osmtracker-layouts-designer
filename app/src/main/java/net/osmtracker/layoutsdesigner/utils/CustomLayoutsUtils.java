package net.osmtracker.layoutsdesigner.utils;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;

public class CustomLayoutsUtils {
    //File Extension for the layouts in different languages
    public final static String LAYOUT_EXTENSION_ISO = "_xx.xml";
    public static final int ISO_CHARACTER_LENGTH = 2;

    /**
     * @param fileName is the name of a .xml that you want to convert to presentation name
     * @return a String presentation name, if the type isn't valid returns the same fileName parameter
     */
    public static String convertFileName(String fileName) {
        //Remove de file extension
        String subname = fileName.replace(OsmtrackerLayoutsDesigner.Preferences.LAYOUT_FILE_EXTENSION,"");

        //Check if it has iso:
        if(subname.matches("\\w+_..")){
            //Remove "_es"
            subname = subname.substring(0,subname.length() - (ISO_CHARACTER_LENGTH+1));
        }

        //Replace "_" to " "
        return subname.replace("_"," ");
    }
}
