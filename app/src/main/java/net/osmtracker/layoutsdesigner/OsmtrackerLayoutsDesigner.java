package net.osmtracker.layoutsdesigner;

import android.Manifest;

public class OsmtrackerLayoutsDesigner {

    public static final class Preferences{
        //TAG
        public final static String TAG = OsmtrackerLayoutsDesigner.class.getPackage().getName();

        //Codes request
        public final static int READ_STORAGE_PERMISSION_REQUEST_CODE = 101;
        public final static int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 102;

        //permissions name
        public final static String READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
        public final static String WRITE_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        //Default
        public final static String VAL_STORAGE_DIR = "/osmtracker";
        public static final String LAYOUTS_SUBDIR = "layouts";
        public static final String LAYOUT_FILE_EXTENSION = ".xml";
        public static final String EXTRA_COLUMNS_NAME = "columns";
        public static final String EXTRA_ROWS_NAME = "rows";
        public static final String EXTRA_NEW_LAYOUT_NAME = "layoutName";
        public static final String EXTRA_CHECKBOX_CAMERA = "camera_button";
        public static final String EXTRA_CHECKBOX_VOICE_RECORDER = "voice_recorder_button";
        public static final String EXTRA_CHECKBOX_NOTES = "notes_button";

    }

}