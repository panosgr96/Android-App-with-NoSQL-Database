package com.example.panagiotis.mytodolist;



import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ContextMenu;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class ActivityProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, View.OnCreateContextMenuListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private ProgressDialog progressDialog;

    private TextView textViewNameLabel;
    private TextView textViewSurnameLabel;
    private TextView textViewEmailLabel;

    private TextView textViewActivitySend;
    private Button buttonSend;

    //initializing firebase ref
    private DatabaseReference ref;

    //initializing ListView for activities feed
    ListView listview;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //get Drawer Navigation Header
        View hView =  navigationView.getHeaderView(0);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing firebase ref object
        ref = FirebaseDatabase.getInstance().getReference();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        progressDialog = new ProgressDialog(this);
        textViewNameLabel = (TextView)hView.findViewById(R.id.textViewNameLabel);
        textViewSurnameLabel = (TextView)hView.findViewById(R.id.textViewSurnameLabel);
        textViewEmailLabel = (TextView)hView.findViewById(R.id.textViewEmailLabel);

        textViewActivitySend = (TextView) findViewById(R.id.textViewActivitySend);
        buttonSend = (Button) findViewById(R.id.buttonSend);

        listview = (ListView)findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listview.setAdapter(adapter);

        registerForContextMenu(listview);


        //Get User Name, Surname and Email
        //name listener
        ref.child("users").child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                textViewNameLabel.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ActivityProfile.this,"Could not fetch data",Toast.LENGTH_LONG).show();
            }
        });

        //surname listener
        ref.child("users").child(user.getUid()).child("surname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                textViewSurnameLabel.setText(" " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ActivityProfile.this,"Could not fetch data",Toast.LENGTH_LONG).show();
            }
        });

        //email listener
        ref.child("users").child(user.getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                textViewEmailLabel.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ActivityProfile.this,"Could not fetch data",Toast.LENGTH_LONG).show();
            }
        });

        //retrieve all activities
        ref.child("users").child(user.getUid()).child("Activities").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object activities = dataSnapshot.getValue();

                // for each Activity get value from the HashMap Structure
                String value    = ((Map.Entry)((java.util.HashMap)activities).entrySet().toArray()[0]).getValue().toString();
                list.add("  " + value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //adding listener to button
        buttonSend.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /** This will be invoked when an item in the listview is long pressed */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.action , menu);
    }

    /** This will be invoked when a menu item is selected */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.cnt_mnu_delete:

                //delete activity here from list only
                list.remove(info.position);
                adapter.notifyDataSetChanged();

                Toast.makeText(this,"Activity Deleted", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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
        getMenuInflater().inflate(R.menu.activity_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(ActivityProfile.this).create();
            alertDialog.setTitle("About");
            alertDialog.setMessage("Application developed exclusively for university lab use.\n\n Developer name: Panagiotis Stathopoulos");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_exit) {
            //finish activity
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSend){
            //send activity
            setActivity();
        }
    }

    private void setActivity(){
        ref = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        DatabaseReference mypostref = ref.child("users").child(user.getUid()).child("Activities").push();
        String pushId = mypostref.getKey();


        final String activity  = textViewActivitySend.getText().toString().trim();

        if(TextUtils.isEmpty(activity)){
            Toast.makeText(this,"Please enter an activity",Toast.LENGTH_LONG).show();
            return;
        }

        //if inputs are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Adding Activity...");
        progressDialog.show();

        mypostref.child("Activity").setValue(activity).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //checking if success
                if(task.isSuccessful()){
                    Toast.makeText(ActivityProfile.this,"Activity added",Toast.LENGTH_LONG).show();
                }else{
                    //display some message here
                    Toast.makeText(ActivityProfile.this,"Activity not added",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

        textViewActivitySend.setText("");
        textViewActivitySend.clearFocus();
    }
}
