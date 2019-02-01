package net.osmtracker.layoutsdesigner.activity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;
import net.osmtracker.layoutsdesigner.R;
import net.osmtracker.layoutsdesigner.activity.MainActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    //TODO: MAKES THE FUNCTIONALITY TO OPEN THE POP UP AND SET NAME AND ICON TO THE ITEM
                    Toast.makeText(getApplicationContext(), "You press " + position, Toast.LENGTH_SHORT).show();
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
}
