package net.osmtracker.layoutsdesigner.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;
import net.osmtracker.layoutsdesigner.R;
import net.osmtracker.layoutsdesigner.utils.CheckPermissions;
import net.osmtracker.layoutsdesigner.utils.CustomAdapterListMain;
import net.osmtracker.layoutsdesigner.utils.CustomLayoutsUtils;
import net.osmtracker.layoutsdesigner.utils.ItemListMain;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<ItemListMain> itemsMainArray;
    private FloatingActionButton fab;
    private String contextTAG = OsmtrackerLayoutsDesigner.Preferences.TAG + ".MainActivity";
    private String storageDir;
    private String numberRows;
    private String numberColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpElemets();
    }

    //This mehtod is used to initialize the first elements in the screen
    private void setUpElemets(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_create_new_layout);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ServiceCast")
            @Override
            public void onClick(View view){
                showPopup();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        storageDir = File.separator + OsmtrackerLayoutsDesigner.Preferences.VAL_STORAGE_DIR;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //verify if the permission to READ storage are granted
        if(CheckPermissions.isPermissionDenied(MainActivity.this, OsmtrackerLayoutsDesigner.Preferences.READ_STORAGE_PERMISSION)){

            Log.i(contextTAG, "Permission to read storage denied");

            //if the permission was denied by the user we push a dialog with a explanation message
            if(CheckPermissions.needsToExplainToUser(MainActivity.this, OsmtrackerLayoutsDesigner.Preferences.READ_STORAGE_PERMISSION)){

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getResources().getString(R.string.permission_read_storage_needed))
                        .setTitle(getResources().getString(R.string.permission_request_dialog_tittle));

                builder.setPositiveButton(getResources().getString(R.string.dialog_accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(contextTAG, "User accept the message");
                        CheckPermissions.makePermissionRequest(MainActivity.this, OsmtrackerLayoutsDesigner.Preferences.READ_STORAGE_PERMISSION,
                                OsmtrackerLayoutsDesigner.Preferences.READ_STORAGE_PERMISSION_REQUEST_CODE);
                    }
                }).setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(contextTAG, "User declined to accept the permission");
                        Snackbar snackbar = Snackbar.make(fab, getResources().getString(R.string.permission_grant_settings), Snackbar.LENGTH_LONG)
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
                CheckPermissions.makePermissionRequest(MainActivity.this, OsmtrackerLayoutsDesigner.Preferences.READ_STORAGE_PERMISSION,
                        OsmtrackerLayoutsDesigner.Preferences.READ_STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            //the permission is already granted
            refreshActivity();
        }
    }

    public void showPopup(){

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupLayout = inflater.inflate(R.layout.preparation_popup, null);
        final EditText txtLayoutName = (EditText) popupLayout.findViewById(R.id.layout_name);
        final CheckBox cbxCamera = (CheckBox) popupLayout.findViewById(R.id.cbx_camera);
        final CheckBox cbxVoiceRecorder = (CheckBox) popupLayout.findViewById(R.id.cbx_voice_record);
        final CheckBox cbxNotes = (CheckBox) popupLayout.findViewById(R.id.cbx_text_note);
        setSpinners(popupLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle(R.string.preparing_pop_up_title)
                .setView(popupLayout)
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("#", "Opening the Editor with the rows, columns and name assigned by the user");
                        Intent intent = new Intent(MainActivity.this, Editor.class);
                        intent.putExtra(OsmtrackerLayoutsDesigner.Preferences.EXTRA_COLUMNS_NAME, numberColumns);
                        intent.putExtra(OsmtrackerLayoutsDesigner.Preferences.EXTRA_ROWS_NAME, numberRows);
                        intent.putExtra(OsmtrackerLayoutsDesigner.Preferences.EXTRA_NEW_LAYOUT_NAME, txtLayoutName.getText().toString());
                        intent.putExtra(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_CAMERA, cbxCamera.isChecked());
                        intent.putExtra(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_NOTES, cbxNotes.isChecked());
                        intent.putExtra(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_VOICE_RECORDER, cbxVoiceRecorder.isChecked());
                        startActivity(intent);
                        finish();
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

    public void setSpinners(View v){

        AppCompatSpinner columnsSpinner = (AppCompatSpinner) v.findViewById(R.id.spinner_columns);
        final String[] columnsCounter = {"1","2","3"};

        ArrayAdapter<String> adapterColumns = new ArrayAdapter<>(this,
                R.layout.spinner_item, columnsCounter);

        adapterColumns.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnsSpinner.setAdapter(adapterColumns);
        //Set the last number in columns list to default
        int lastColumnsPos = adapterColumns.getPosition(columnsCounter[columnsCounter.length - 1]);
        columnsSpinner.setSelection(lastColumnsPos);

        columnsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                numberColumns = columnsCounter[i];
                Log.i("#", "Selección columnas: "+ numberColumns);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AppCompatSpinner rowsSpinner = (AppCompatSpinner) v.findViewById(R.id.spinner_rows);
        final String[] rowsCounter = {"1","2","3","4"};
        ArrayAdapter<String> adapterRows = new ArrayAdapter<>(this,
                R.layout.spinner_item, rowsCounter);
        adapterRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowsSpinner.setAdapter(adapterRows);
        //Set the last number in rows list to default
        int lastRowsPos = adapterRows.getPosition(rowsCounter[rowsCounter.length - 1]);
        rowsSpinner.setSelection(lastRowsPos);

        rowsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                numberRows = rowsCounter[i];
                Log.i("#", "Selección filas: "+ numberRows);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case OsmtrackerLayoutsDesigner.Preferences.READ_STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(contextTAG, "The permission to read was denied by the user");
                    Snackbar.make(fab, getResources().getString(R.string.permission_read_storage_denied), Snackbar.LENGTH_LONG).show();
                }
                else{
                    Log.i(contextTAG, "The permission to read was granted");
                    refreshActivity();
                }
            }
        }
    }

    //Use this method to refresh the MainActivity when a new layouts is created or downloaded from the OSMTracker App
    private void refreshActivity(){
        Toast.makeText(getApplicationContext(), "Preparing the layouts", Toast.LENGTH_SHORT).show();
        ListView listLayoutContainer = (ListView) findViewById(R.id.list_layouts);
        listLayouts(listLayoutContainer);
    }

    //This method search the layouts downloaded in the /osmtracker/layouts/ directory and show their as a list in the main screen
    private void listLayouts(ListView container){
        //we need to search the layouts in the path /osmtracker/layouts/
        File layoutsDir = new File(Environment.getExternalStorageDirectory(),
                storageDir + File.separator + OsmtrackerLayoutsDesigner.Preferences.LAYOUTS_SUBDIR + File.separator);

        //verify if we can access to this path
        if(layoutsDir.exists() && layoutsDir.canRead()){
            //enlist all the xml files in this directory
            String[] layoutFiles = layoutsDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String fileName) {
                    return fileName.endsWith(OsmtrackerLayoutsDesigner.Preferences.LAYOUT_FILE_EXTENSION);
                }
            });

            //fill the array with the layouts founded
            itemsMainArray = new ArrayList<ItemListMain>();
            for(String fileName :  layoutFiles){
                itemsMainArray.add(new ItemListMain(CustomLayoutsUtils.convertFileName(fileName), ""));
            }
            //verify if there are some layouts in the list to hide the message of "you don't have layouts yet"
            if(itemsMainArray.size() > 0){
                TextView emptyMessage = (TextView) findViewById(R.id.empty_list);
                emptyMessage.setVisibility(View.INVISIBLE);
                CustomAdapterListMain adapterListMain = new CustomAdapterListMain(MainActivity.this, itemsMainArray);
                container.setAdapter(adapterListMain);
                container.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(MainActivity.this, "You press " + itemsMainArray.get(i).getLayoutCreatedName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                TextView emptyMessage = (TextView) findViewById(R.id.empty_list);
                emptyMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_btn_action_home) {
            // Handle the cbx_camera action
        } else if (id == R.id.nav_btn_action_editor) {
            showPopup();

        } else if (id == R.id.nav_btn_action_settings) {

        } else if (id == R.id.nav_btn_action_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
