package com.example.andre.fasetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class save extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference cDatabase,mDatabase;
    private DrawerLayout cDrawLayout;
    private ActionBarDrawerToggle cTogger;
    String name="No Product";
    String userID;
    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        initialActionbar();
        Intent it=getIntent();
        ID = it.getStringExtra("ID");
        userID=it.getStringExtra("userid");
       getNamePictures();

    }
    public void getNamePictures()
    {
        cDatabase= FirebaseDatabase.getInstance().getReference().child("Cloth");
        cDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {

                    String id = ds.child("ID").getValue().toString();

                    if(ID.equals(id)) {
                        Calendar mCal = Calendar.getInstance();
                        CharSequence s = DateFormat.format("yyyy-MM-dd ", mCal.getTime());

                        TextView txv = (TextView) findViewById(R.id.textView);
                        ImageView post_image = (ImageView) findViewById(R.id.imageView);
                        String name = ds.child("name").getValue().toString();
                        String tag  = ds.child("tag").getValue().toString();
                        String pitures = ds.child("imageURL").getValue().toString();
                        String price=ds.child("price").getValue().toString();
                        String date=s.toString();
                        saveCloth(id,name,tag,pitures,price,date);
                        txv.setText(name);
                        Picasso.with(getApplicationContext()).load(pitures).into(post_image);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void saveCloth(String id,String name,String tag,String pictures,String price,String date)
    {   mDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("userGallery");
        mDatabase.child(id).child("ID").setValue(id);
        mDatabase.child(id).child("name").setValue(name);
        mDatabase.child(id).child("date").setValue(date);
        mDatabase.child(id).child("tag").setValue(tag);
        mDatabase.child(id).child("price").setValue(price);
        mDatabase.child(id).child("imageURL").setValue(pictures);
    }

    public void initialActionbar()
    {
        cDrawLayout=(DrawerLayout) findViewById(R.id.drawer2);
        cTogger =new ActionBarDrawerToggle(this,cDrawLayout,R.string.open,R.string.close);
        cDrawLayout.addDrawerListener(cTogger);
        cTogger.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(cTogger.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            // Handle the camera action
            Intent it=new Intent(this,SecondActivity.class);
            startActivity(it);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

