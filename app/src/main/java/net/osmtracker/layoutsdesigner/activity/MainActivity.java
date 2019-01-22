package net.osmtracker.layoutsdesigner.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.R;
import net.osmtracker.layoutsdesigner.utils.CustomAdapterListMain;
import net.osmtracker.layoutsdesigner.utils.ItemListMain;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listLayoutContainer;
    private ArrayList<ItemListMain> itemsMainArray;
    private static final int READ_PERMISSION_REQUEST = 001;
    FloatingActionButton fab;

    //This lists are only for test
    private String[] names = new String[]{"Item 1", "Item 2", "Item 3"};
    private String[] descriptions = new String[]{"Description 1", "Description 2", "Description 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_create_new_layout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //verify if the permissions of READ and WRITE DATA to storage are granted
        int readPermissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(readPermissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, getResources().getString(R.string.permission_read_storage_needed), Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(fab, getResources().getString(R.string.permission_read_storage_denied), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.snackbar_permission_request_denied_action), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("Intent", "Opening the app settings to grant the permission of read storage");
                            }
                        });
                snackbar.show();
            }
            else{
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case READ_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //TODO: makes the method to read the layouts from the directory of OSMTracker
                    listLayoutContainer = (ListView) findViewById(R.id.list_layouts);
                    itemsMainArray = new ArrayList<ItemListMain>();

                    //TODO: changes this for the populate of the existing layouts
                    for(int i = 0; i < names.length; i++){
                        itemsMainArray.add(new ItemListMain(names[i], descriptions[i]));
                    }

                    //when the layouts are populated then inflate the list with the elements in the array
                    if(itemsMainArray.size() > 0){
                        TextView emptyTextView = (TextView) findViewById(R.id.empty_list);
                        emptyTextView.setVisibility(View.INVISIBLE);
                        CustomAdapterListMain adapterListMain = new CustomAdapterListMain(this, itemsMainArray);
                        listLayoutContainer.setAdapter(adapterListMain);
                        listLayoutContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                //TODO: replace with the functionality to show the info of the layout pressed
                                Toast.makeText(MainActivity.this, "You press the item: " + itemsMainArray.get(i).getLayoutCreatedName(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else{
                    //TODO: show a message to the user to notify that the permisson was denied and the app can't read the layouts that was already created
                    Toast.makeText(this, getResources().getString(R.string.permission_read_storage_needed), Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST);
                }
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
            // Handle the camera action
        } else if (id == R.id.nav_btn_action_editor) {

        } else if (id == R.id.nav_btn_action_settings) {

        } else if (id == R.id.nav_btn_action_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
