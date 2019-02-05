package net.osmtracker.layoutsdesigner.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;
import net.osmtracker.layoutsdesigner.R;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.io.File;

public class XMLGenerator {

    private Context context;

    public static void generateXML(Context context, ArrayList<LayoutButtonGridItem> gridItemsArray, String layoutName, int rows, int columns) throws IOException {


        layoutName = layoutName + OsmtrackerLayoutsDesigner.Preferences.XML_EXTENSION;

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "layouts");
        serializer.startTag("","layout");
        serializer.attribute("", "name", "root");

        int index = 0;

        for (int i = 0; i < rows ; i++) {
            serializer.startTag("", "row");
            for (int j = 0; j < columns; j++) {
                LayoutButtonGridItem currentItem = gridItemsArray.get(index);

                serializer.startTag("", "button");

                if (currentItem.getItemName() != null) {

                    //VOICE RECORD
                    if (currentItem.getItemName().equals(context.getResources().getString(R.string.default_button_voice))) {
                        serializer.attribute("", "label", currentItem.getItemName());
                        serializer.attribute("", "type", "voicerec");
                        serializer.attribute("", "icon", OsmtrackerLayoutsDesigner.Preferences.VOICE_RECORD_ICON_PATH);
                    }
                    //TEXT NOTE
                    else if (currentItem.getItemName().equals(context.getResources().getString(R.string.default_button_text_note))) {
                        serializer.attribute("", "label", currentItem.getItemName());
                        serializer.attribute("", "type", "textnote");
                        serializer.attribute("", "icon", OsmtrackerLayoutsDesigner.Preferences.TEXT_NOTE_ICON_PATH);
                    }
                    //CAMERA
                    else if (currentItem.getItemName().equals(context.getResources().getString(R.string.default_button_camera))) {
                        serializer.attribute("", "label", currentItem.getItemName());
                        serializer.attribute("", "type", "picture");
                        serializer.attribute("", "icon", OsmtrackerLayoutsDesigner.Preferences.CAMERA_ICON_PATH);
                    } else {
                        //Checking if the button have icon
                        if (currentItem.getImagePath() != null) {
                            serializer.attribute("", "type", "tag");
                            serializer.attribute("", "label", currentItem.getItemName());
                            serializer.attribute("", "icon", currentItem.getImagePath().replace(Environment.getExternalStorageDirectory().toString(),
                                    OsmtrackerLayoutsDesigner.Preferences.EXIT_LAYOUT_DIR));
                        }
                        //The button doesn't have icon
                        else {
                            serializer.attribute("", "type", "tag");
                            serializer.attribute("", "label", currentItem.getItemName());
                        }
                    }
                }

                //The button doesn't have have name
                else {
                    //Maybe the user only put an icon in the button
                    if (currentItem.getImagePath() != null) {
                        serializer.attribute("", "type", "tag");
                        serializer.attribute("", "label", "-");
                        serializer.attribute("", "icon", currentItem.getImagePath().replace(Environment.getExternalStorageDirectory().toString(),
                                OsmtrackerLayoutsDesigner.Preferences.EXIT_LAYOUT_DIR));
                    }
                    //The button information is empty
                    else {
                        serializer.attribute("", "type", "tag");
                        serializer.attribute("", "label", "-");
                    }

                }
                serializer.endTag("", "button");
                index++;
            }
            serializer.endTag("", "row");
        }

        serializer.endTag("", "layout");
        serializer.endTag("","layouts");
        serializer.endDocument();
        String result = writer.toString();

        String path = Environment.getExternalStorageDirectory() + OsmtrackerLayoutsDesigner.Preferences.VAL_STORAGE_DIR+
                File.separator+OsmtrackerLayoutsDesigner.Preferences.LAYOUTS_SUBDIR + File.separator;

        createDir(path);

        writeToFile(context,path + layoutName, result);

    }

    private static void writeToFile(Context context, String fileName, String str) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(str.getBytes(), 0, str.length());
        fos.close();
    }

    private static void createDir(String dirPath) {

        //Get File if SD card is present
        File apkStorage = null;
        if (isSDCardPresent()) {
            apkStorage = new File(dirPath);
        }
        //If File is not present create directory
        if (!apkStorage.exists()) {
            apkStorage.mkdirs();
            Log.e("#", "Directory Created.");
        }
    }
    private static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
