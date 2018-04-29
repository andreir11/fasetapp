package com.example.andre.fasetapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class VirturalCabinet extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView clothList;
    private DatabaseReference mDatabase;
    private DrawerLayout cDrawLayout;
    private ActionBarDrawerToggle cTogger;
    String userID;
    Intent it;
    String[] listItems;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtural_cabinet);
        Intent it=getIntent();
        userID=it.getStringExtra("ID");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("userGallery");
        mDatabase.keepSynced(true);

        clothList=(RecyclerView)findViewById(R.id.recycleview);
        clothList.setHasFixedSize(true);//add itemas maintain height
        clothList.setLayoutManager(new LinearLayoutManager(this));//線性排列
        cDrawLayout=(DrawerLayout) findViewById(R.id.drawer);
        cTogger =new ActionBarDrawerToggle(this,cDrawLayout,R.string.open,R.string.close);
        cDrawLayout.addDrawerListener(cTogger);
        cTogger.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(VirturalCabinet.this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(cTogger.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //call firebase
    protected void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<Cloth,ClothViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Cloth, ClothViewHolder>
                (Cloth.class,R.layout.cloth_row,ClothViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(ClothViewHolder viewHolder, Cloth model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setDate(model.getDate());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setTag(model.getTag());
                viewHolder.setImage(getApplicationContext(),model.getImageURL());

                Button btnRemove = (Button) viewHolder.itemView.findViewById(R.id.button3);
                Button btnEdit  =(Button) viewHolder.itemView.findViewById(R.id.button2);
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ID=getItem(position).getID();
                        mDatabase.child(ID).removeValue();

                    }
                });
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listItems = new String[]{"Beautiful", "Cute", "Cool", "Fashion", "Comfortable"};
                        AlertDialog.Builder clothBuilder = new AlertDialog.Builder(VirturalCabinet.this);
                        clothBuilder.setTitle("Edit your clothes of Tag ");
                        clothBuilder.setIcon(R.drawable.icon);
                        clothBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String ID=getItem(position).getID();
                                mDatabase.child(ID).child("tag").setValue(listItems[i]);
                                dialogInterface.dismiss();
                            }
                        });
                        clothBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog clothDialog=clothBuilder.create();
                        clothDialog.show();
                    }
                });

            }
        };
        clothList.setAdapter(firebaseRecyclerAdapter);

    }

    //call QRcode Activity
    public void runQrcode(View v) {

        Intent it = new Intent(VirturalCabinet.this, QRcode.class);
        it.putExtra("userid",userID);
        startActivity(it);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //首頁
        if (id == R.id.menu_home) {
            // Handle the camera action
            Intent it=new Intent(getApplicationContext(),SecondActivity.class);//CALL Saved Class
            startActivity(it);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //go into XML
    public static class ClothViewHolder extends RecyclerView.ViewHolder
    {   View mView;
        public ClothViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name)
        {
            TextView post_name=(TextView)mView.findViewById(R.id.post_name);
            post_name.setText("Name: "+name);
        }
        public void setDate(String date)
        {
            TextView post_date=(TextView)mView.findViewById(R.id.post_date);
            post_date.setText("Date: "+date);
        }
        public void setPrice(String price)
        {
            TextView post_price=(TextView)mView.findViewById(R.id.post_price);
            post_price.setText("Price: "+price);
        }
        public void setTag(String tag)
        {
            TextView post_tag=(TextView)mView.findViewById(R.id.post_tag);
            post_tag.setText("Tag: "+tag);

        }
        public void setImage(Context ctx, String imageURL)
        {
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(imageURL).into(post_image);

        }
    }
}
