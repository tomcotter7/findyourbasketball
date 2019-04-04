package com.thomas.findyourbasketball;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Home.OnFragmentInteractionListener, Profile.OnFragmentInteractionListener, MyCourts.OnFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the view to the main activity xml file.
        setContentView(R.layout.activity_main);
        // sets up the action bar so that you can perform actions on it.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // sets up the navigation drawer.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //This toggle is on the action bar and allows the user to open and close the navigation drawer.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // sets up the navigation view which is the content inside the navigation drawer.
        NavigationView navigationView = findViewById(R.id.nav_view);
        //sets up all options within the navigation drawer so that they have a listener and a method can be called off a click.
        navigationView.setNavigationItemSelectedListener(this);

        //the first screen displayed on start-up should be the map, so this function call loads the map fragment into a content frame.
        displaySelectedScreen(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        // Closes the drawer if drawer is open and the back button is pressed.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    public void replaceFragment(Fragment fragment, int content_frame) {
        //replaces the anything in the content frame with the new fragment that is selected.
        if (fragment != null) {
            // getSupportFragmentManager returns the FragmentManager for interacting with fragments that have been implemented into this activity.
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(content_frame, fragment);
            ft.commit();
        }
    }

    public void displaySelectedScreen(int itemId) {
        //creating fragment object
        Fragment fragment = null;

        //init the fragment object selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new Home();
                break;
            case R.id.nav_profile:
                fragment = new Profile();
                break;
            case R.id.nav_courts:
                fragment = new MyCourts();
                break;

        }

        //replace fragment

        replaceFragment(fragment, R.id.content_frame1);

        //closes the drawer once the fragment has been loaded in.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void onFragmentInteraction(Uri uri){

    }

}

