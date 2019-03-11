package net.osmtracker.layoutsdesigner.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.osmtracker.layoutsdesigner.R;

public class AboutActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        setUpElemets();
    }

    private void setUpElemets(){

        Toolbar main_toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(main_toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }


}