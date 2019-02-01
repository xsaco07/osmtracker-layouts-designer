package net.osmtracker.layoutsdesigner.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;
import net.osmtracker.layoutsdesigner.R;
import net.osmtracker.layoutsdesigner.activity.MainActivity;
import net.osmtracker.layoutsdesigner.utils.CheckPermissions;
import net.osmtracker.layoutsdesigner.utils.CustomGridItemAdapter;
import net.osmtracker.layoutsdesigner.utils.LayoutButtonGridItem;

import java.util.ArrayList;
import java.util.Calendar;

public class Editor extends AppCompatActivity {

    private TextView txtLayoutName;
    private String contextTag = OsmtrackerLayoutsDesigner.Preferences.TAG + ".Editor";
    private GridView gvLayoutEditor;
    private CustomGridItemAdapter gridAdapter;
    private ArrayList<LayoutButtonGridItem> gridItemsArray;
    private int columnsNum = 0;
    private int rownsNum = 0;
    private String layoutName;
    private Button btnCancel;
    private Button btnAccept;

    // Code returned by the startActivityForResult() to the onActionResult() method
    // Indicates that the user choose correctly to select an image from the gallery
    private int SELECT_IMAGE = 10;

    private String IMAGE_PATH;
    private TextView sample_url;
    private ImageButton image_button;

    private View current_new_button_popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CheckPermissions.isPermissionDenied(Editor.this, OsmtrackerLayoutsDesigner.Preferences.WRITE_STORAGE_PERMISSION)){
            Log.i(contextTag, "Permission to write storage denied");

            //if the permission was denied, we push a dialog with an explanation message
            if(CheckPermissions.needsToExplainToUser(Editor.this, OsmtrackerLayoutsDesigner.Preferences.WRITE_STORAGE_PERMISSION)){

                AlertDialog.Builder builder = new AlertDialog.Builder(Editor.this);
                builder.setMessage(R.string.permission_write_storage_needed)
                        .setTitle(R.string.permission_request_dialog_tittle);

                builder.setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(contextTag, "User accept the message");
                        CheckPermissions.makePermissionRequest(Editor.this, OsmtrackerLayoutsDesigner.Preferences.WRITE_STORAGE_PERMISSION,
                                OsmtrackerLayoutsDesigner.Preferences.WRITE_STORAGE_PERMISSION_REQUEST_CODE);
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(contextTag, "User declined to accept the permission");
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.btn_accept), getResources().getString(R.string.permission_grant_settings), Snackbar.LENGTH_LONG)
                                .setAction(getResources().getString(R.string.snackbar_permission_request_denied_action), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i("Intent", "Opening the app settings to grant the permission of read storage");
                                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                                    }
                                });
                        snackbar.show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                //We don't need to explain
                CheckPermissions.makePermissionRequest(Editor.this, OsmtrackerLayoutsDesigner.Preferences.WRITE_STORAGE_PERMISSION,
                        OsmtrackerLayoutsDesigner.Preferences.WRITE_STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            makeLayoutView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case OsmtrackerLayoutsDesigner.Preferences.WRITE_STORAGE_PERMISSION_REQUEST_CODE: {
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.i(contextTag, "The permission to read was denied by the user");
                    Snackbar.make(findViewById(R.id.btn_accept), getResources().getString(R.string.permission_write_storage_denied), Snackbar.LENGTH_LONG).show();
                }
                else{
                    Log.i(contextTag, "The permission to read was granted");
                    makeLayoutView();
                }
            }
        }
    }

    private void makeLayoutView(){
        Bundle extras = this.getIntent().getExtras();

        //get the extras from the intent and check if exist and has values to initialize the variable to create the editor view
        if(extras != null){
            columnsNum = Integer.parseInt(extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_COLUMNS_NAME, "3"));
            rownsNum = Integer.parseInt(extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_ROWS_NAME, "4"));

            layoutName = extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_NEW_LAYOUT_NAME, getResources().getString(R.string.empty_layout_name).replace("{0}",  Calendar.getInstance().getTime().toString()));
            if(layoutName.equals("") || layoutName.isEmpty()){
                layoutName = getResources().getString(R.string.empty_layout_name).replace("{0}", Calendar.getInstance().getTime().toString());
            }
            txtLayoutName = (TextView) findViewById(R.id.txt_layout_name);
            txtLayoutName.setText(layoutName);

            boolean notesCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_NOTES, false);
            boolean cameraCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_CAMERA, false);
            boolean voiceRecorderCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_VOICE_RECORDER, false);
            gridItemsArray = new ArrayList<LayoutButtonGridItem>();
            int amountToSubstract = checkIfNeedsDefaultButtons(notesCheckbox, cameraCheckbox, voiceRecorderCheckbox);
            int totalItems = (columnsNum * rownsNum) - amountToSubstract;
            //TODO: VERIFY IF THE DEFAULT BUTTONS ARE CHECKED AND CREATE THEM IN THE ARRAY
            //set the total items created by default in the array
            for(int i = 0; i < totalItems; i++){
                gridItemsArray.add(new LayoutButtonGridItem(""));
            }

            gvLayoutEditor = (GridView) findViewById(R.id.grid_view_editor);
            //pass the num columns assigned by the user
            gvLayoutEditor.setNumColumns(columnsNum);
            gridAdapter = new CustomGridItemAdapter(this, gridItemsArray);
            gvLayoutEditor.setAdapter(gridAdapter);
            gvLayoutEditor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showPopUp();
                }
            });

            btnCancel = (Button) findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: SHOW A MESSAGE WITH AN ALERT
                }
            });
            btnAccept = (Button) findViewById(R.id.btn_accept);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: SAVE THE LAYOUT
                }
            });

        }
        else{
            Toast.makeText(getApplicationContext(), R.string.editor_intent_extras_error, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private int checkIfNeedsDefaultButtons(boolean notesCheckbox, boolean cameraCheckbox, boolean voiceRecorderCheckbox) {
        int i = 0;
        if(voiceRecorderCheckbox){
            gridItemsArray.add(new LayoutButtonGridItem(getResources().getString(R.string.default_button_voice), getResources().getDrawable(R.drawable.voice_32x32)));
            i++;
        }
        if(cameraCheckbox){
            gridItemsArray.add(new LayoutButtonGridItem(getResources().getString(R.string.default_button_camera), getResources().getDrawable(R.drawable.camera_32x32)));
            i++;
        }
        if(notesCheckbox){
            gridItemsArray.add(new LayoutButtonGridItem(getString(R.string.default_button_text_note), getResources().getDrawable(R.drawable.text_32x32)));
            i++;
        }
        return i;
    }

    @Override
    public void onBackPressed() {
        //TODO: push a dialog notifying that this action delete the current layout without save it
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showPopUp(){
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        current_new_button_popup = inflater.inflate(R.layout.new_button_popup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(Editor.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle(R.string.new_button_pop_up_title)
                .setView(current_new_button_popup)
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //

                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true)
                .create().show();
    }

    // This method throws the intent to open the gallery so the user can choose an icon for the new button in the layout
    // Is called when the user presses the Icon in the pop up
    public void selectImage(View view){

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_IMAGE); // Start the activity waiting for a code as a response (SELECT_IMAGE)
    }

    // This method is called when the intent to open the gallery is finished or closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == SELECT_IMAGE){ // This is to indentIfy who returned the original requestCode
            if (resultCode == Activity.RESULT_OK) { // If no error happened in the process
                if(current_new_button_popup != null) showPathAndIcon(data.getData());
            }
        }
    }

    // This method writes the image path on the pop up window and shows the icon selected by the user
    private void showPathAndIcon(Uri selectedImage){

        sample_url = (TextView)current_new_button_popup.findViewById(R.id.url_text_view);
        image_button = (ImageButton)current_new_button_popup.findViewById(R.id.imageButton);

        if (selectedImage != null && selectedImage.getPath() != null){

            IMAGE_PATH = getPath(selectedImage);
            sample_url.setText(IMAGE_PATH);
            Log.e("Path", IMAGE_PATH);
        }

        // Set the correct properties so the image is shown in the right way
        image_button.setMaxWidth(100);
        image_button.setMaxHeight(100);
        image_button.setPadding(9,9,9,9);
        image_button.setImageURI(selectedImage);
    }

    // This method returns the path of an Uri object as a string
    private String getPath(Uri uri) {
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
