package net.osmtracker.layoutsdesigner.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    //This lists are only for test
    private String[] names = new String[]{"Item 1", "Item 2", "Item 3"};
    private String[] descriptions = new String[]{"Description 1", "Description 2", "Description 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create_new_layout);
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
                    Toast.makeText(MainActivity.this, "You press the item: " + itemsMainArray.get(i).getLayoutCreatedName(), Toast.LENGTH_SHORT).show();
                }
            });
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
