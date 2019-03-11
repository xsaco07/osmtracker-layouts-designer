package net.osmtracker.layoutsdesigner.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;
import net.osmtracker.layoutsdesigner.R;
import net.osmtracker.layoutsdesigner.utils.CheckPermissions;
import net.osmtracker.layoutsdesigner.utils.CustomGridItemAdapter;
import net.osmtracker.layoutsdesigner.utils.LayoutButtonGridItem;
import net.osmtracker.layoutsdesigner.utils.XMLGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Editor extends AppCompatActivity {

    private String contextTag = OsmtrackerLayoutsDesigner.Preferences.TAG + ".Editor";
    private GridView gvLayoutEditor;
    private CustomGridItemAdapter gridAdapter;
    private ArrayList<LayoutButtonGridItem> gridItemsArray;
    private int columnsNum = 0;
    private int rowsNum = 0;
    private String layoutName;

    // Code returned by the startActivityForResult() to the onActionResult() method
    // Indicates that the user choose correctly to select an image from the gallery
    private int OPEN_GALLERY_CODE = 10;

    private Uri currentUriImage;
    private View newButtonLayout;

    private boolean USER_CANCEL_EDITION = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = this.getIntent().getExtras();

        //get the extras from the intent and check if exist and has values to initialize the variable to create the editor view
        if(extras != null){

            columnsNum = Integer.parseInt(extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_COLUMNS_NAME, "3"));
            rowsNum = Integer.parseInt(extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_ROWS_NAME, "4"));

            layoutName = extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_NEW_LAYOUT_NAME, getResources().getString(R.string.empty_layout_name).replace("{0}",  Calendar.getInstance().getTime().toString()));
            if(layoutName.equals("") || layoutName.isEmpty()){
                layoutName = getResources().getString(R.string.empty_layout_name).replace("{0}", Calendar.getInstance().getTime().toString());
            }
            TextView txtLayoutName = (TextView) findViewById(R.id.txt_layout_name);
            txtLayoutName.setText(layoutName);

            boolean notesCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_NOTES, false);
            boolean cameraCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_CAMERA, false);
            boolean voiceRecorderCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_VOICE_RECORDER, false);
            gridItemsArray = new ArrayList<LayoutButtonGridItem>();
            int amountToSubstract = checkIfNeedsDefaultButtons(notesCheckbox, cameraCheckbox, voiceRecorderCheckbox);
            int totalItems = (columnsNum * rowsNum) - amountToSubstract < 0 ? 0 : (columnsNum * rowsNum) - amountToSubstract;
            if((columnsNum * rowsNum) - amountToSubstract < 0){
                rowsNum = 1;
                columnsNum = amountToSubstract;
            }
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

                    LayoutButtonGridItem currentGridItem =  gridItemsArray.get(position);
                    if (currentGridItem.getDefaultIcon() != null){
                        //The selected button is an default button chosen before the editor activity was open, the user can't edit this button
                        Toast.makeText(getApplicationContext(), R.string.can_not_edit_button_message, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        showNewButtonPopUp(currentGridItem);
                    }

                }
            });

            manageCancelEditionButton();
            manageAcceptEditionBUtton();

        }
        else{
            Toast.makeText(getApplicationContext(), R.string.editor_intent_extras_error, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void manageCancelEditionButton(){
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCancelLayoutEdition();
            }
        });
    }

    private void manageAcceptEditionBUtton(){
        Button btnAccept = (Button) findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GridIsFull()){
                    try {
                        XMLGenerator.generateXML(Editor.this, gridItemsArray, layoutName, rowsNum,columnsNum);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Editor.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                        builder.setTitle(R.string.succesfully_created_title)
                                .setMessage(R.string.succesfully_created_message)
                                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        goHome();
                                    }
                                })
                                .create().show();
                    } catch (IOException e) {
                        Toast.makeText(Editor.this,R.string.error_creating_message, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(Editor.this,R.string.some_empty_buttons, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean GridIsFull(){
        for (LayoutButtonGridItem currentItem : gridItemsArray) {
            if (currentItem.getItemName().equals("")) return false;
        }
        return true;
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
                }
            }
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

    private void userCancelLayoutEdition(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(Editor.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        builder.setTitle(R.string.cancelling_creation);
        builder.setMessage(R.string.cancel_verification);
        
        builder.setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goHome();
            }});

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }});

        builder.setCancelable(true);
        builder.create();
        builder.show();

    }

    @Override
    public void onBackPressed() {
        userCancelLayoutEdition(); // Warn the user that the layout will not be saved if press Back button.
    }

    private void goHome(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        goHome();
        return true;
    }

    private void showNewButtonPopUp(final LayoutButtonGridItem currentGridItem){
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        newButtonLayout = inflater.inflate(R.layout.new_button_popup, null);

        final EditText buttonName = newButtonLayout.findViewById(R.id.editTextButtonName);
        final TextView imagePathTextView = newButtonLayout.findViewById(R.id.url_text_view);
        final AlertDialog.Builder builder = new AlertDialog.Builder(Editor.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        builder.setTitle(R.string.new_button_pop_up_title);
        builder.setView(newButtonLayout);
        builder.setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Checking if the user changed the name or the image of the item

                if(!currentGridItem.getItemName().equals(buttonName.getText().toString())) {
                    currentGridItem.setItemName(buttonName.getText().toString());
                    updateAndSetAdapter();
                }

                if(currentUriImage != null){
                    currentGridItem.setImageURI(currentUriImage);
                    currentGridItem.setImagePath(imagePathTextView.getText().toString());
                    currentUriImage = null;
                    updateAndSetAdapter();
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        setButtonPathAndName(currentGridItem, buttonName, imagePathTextView);

        addTextListenerInAlert(buttonName, dialog);

        checkIfButtonNameEmpty(buttonName, dialog);

    }

    private void updateAndSetAdapter(){
        gridAdapter.notifyDataSetChanged();
        gvLayoutEditor.setAdapter(gridAdapter);
    }

    private void checkIfButtonNameEmpty(EditText buttonName, AlertDialog dialog) {
        if (buttonName.getText().toString().isEmpty()) dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        else dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }

    private void addTextListenerInAlert(final EditText buttonName, final AlertDialog dialog) {
        buttonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIfButtonNameEmpty(buttonName, dialog);
            }
        });
    }

    // Check if the user has already changed the name or the icon when open it again
    private void setButtonPathAndName(LayoutButtonGridItem currentGridItem, EditText name, TextView path) {
        if(!currentGridItem.getItemName().equals(getString(R.string.EMPTY)) || currentGridItem.getImageURI() != null){
            Uri selectedImage = currentGridItem.getImageURI();
            name.setText(currentGridItem.getItemName());
            path.setText(currentGridItem.getImagePath());
            setIconInPopUp(selectedImage);
        }
    }

    // This method throws the intent to open the gallery so the user can choose an icon for the new button in the layout
    public void selectImage(View view){
        Intent galleryIntent = new Intent();
        Uri requestedData = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        String intentAction = Intent.ACTION_PICK;
        String dataType = "image/*";
        galleryIntent.setDataAndType(requestedData,dataType);
        galleryIntent.setAction(intentAction);
        startActivityForResult(galleryIntent, OPEN_GALLERY_CODE); // Start the activity waiting for a code as a response (OPEN_GALLERY_CODE)
    }

    // This method is called when the intent to open the gallery is finished or closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == OPEN_GALLERY_CODE){ // This is to indentIfy who returned the original requestCode
            if (resultCode == Activity.RESULT_OK) { // If no error happened in the process
                setPathInPopUp(data.getData());
                setIconInPopUp(data.getData());
            }
        }
    }

    private void setPathInPopUp(Uri selectedImage){
        currentUriImage = selectedImage;
        TextView imagePathView = (TextView) newButtonLayout.findViewById(R.id.url_text_view);
        imagePathView.setText(getUriPath(selectedImage));
    }

    private void setIconInPopUp(Uri selectedImage){
        ImageButton PopUpImageBUtton = (ImageButton) newButtonLayout.findViewById(R.id.imageButton);
        PopUpImageBUtton.setMaxWidth(100);
        PopUpImageBUtton.setMaxHeight(100);
        PopUpImageBUtton.setPadding(9,9,9,9);
        PopUpImageBUtton.setImageURI(selectedImage);

    }

    private String getUriPath(Uri uri) {
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}