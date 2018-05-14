package com.example.andre.fasetapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesCollageActivity extends AppCompatActivity implements RecyclerViewwAdapterrr.OnItemClickListener {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private Button btnSort;
    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerViewwAdapterrr adapter;
    FirebaseAuth firebaseAuth;
    // Creating Progress dialog
    ProgressDialog progressDialog;
    ImageView img;
    ImageView img1;
    String[] listitems;
    Button camerabtn;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();

    ImageView imgViewCamera;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_cloth:
                    //mTextMessage.setText(R.string.title_home);
                    finish();
                    Intent i = new Intent(DisplayImagesCollageActivity.this, DisplayImagesGalleryActivity.class);
                    //i.setFlags(i.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    break;
                case R.id.navigation_collage:
                   // mTextMessage.setText(R.string.title_dashboard);
                    return true;

            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images_collage);
        //imgViewCamera =(ImageView)findViewById(R.id.imagecameraView);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        getSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Wardrobe");

        //img = (ImageView)findViewById(R.id.imageView4) ;
        //img1 = (ImageView)findViewById(R.id.imageView6) ;
        btnSort = (Button) findViewById(R.id.buttonSorting);
        firebaseAuth = FirebaseAuth.getInstance();
        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        //recyclerView.setLayoutManager(new LinearLayoutManager(DisplayImagesActivity.this));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DisplayImagesCollageActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images...");

        // Showing progress dialog.
        progressDialog.show();
        adapter = new RecyclerViewwAdapterrr(DisplayImagesCollageActivity.this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(DisplayImagesCollageActivity.this);

        //recyclerView.setAdapter(new G);
        mStorage = FirebaseStorage.getInstance();

        //btnSort = (Button)findViewById()R.id.buu;
        camerabtn = (Button)findViewById(R.id.buttonSorting2);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("userCollage");

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listitems = new String[]{"All","Casual",
                        "Party",
                        "Formal",
                        "Comfy",
                        "Sport"};
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DisplayImagesCollageActivity.this);
                mBuilder.setTitle("Sort By :");
                mBuilder.setIcon(R.drawable.icon);
                mBuilder.setSingleChoiceItems(listitems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btnSort.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        String item = listitems[i].toString();

                        if(listitems[i] == "All"){
                            mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    list.clear();
                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                        ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
                                        imageUploadInfo.setKey(postSnapshot.getKey());
                                        list.add(imageUploadInfo);
                                    }
                                    adapter.notifyDataSetChanged();

                                    // Hiding the progress dialog.
                                    progressDialog.dismiss();
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    // Hiding the progress dialog.
                                    progressDialog.dismiss();

                                }
                            });

                        }

                        else {

                            Query query = databaseReference.orderByChild("category").equalTo(listitems[i]);
                            // Adding Add Value Event Listener to databaseReference.
                            mDBListener = query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    list.clear();
                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                        ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
                                        imageUploadInfo.setKey(postSnapshot.getKey());
                                        list.add(imageUploadInfo);
                                    }
                                    adapter.notifyDataSetChanged();

                                    // Hiding the progress dialog.
                                    progressDialog.dismiss();
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    // Hiding the progress dialog.
                                    progressDialog.dismiss();

                                }
                            });


                        }
                    }
                });
                mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });



        // Adding Add Value Event Listener to databaseReference.
        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
                    imageUploadInfo.setKey(postSnapshot.getKey());
                    list.add(imageUploadInfo);
                }
                adapter.notifyDataSetChanged();

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });




    }

    private void getSupportActionBar(Toolbar toolbar) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        imgViewCamera.setImageBitmap(bitmap);
        //Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onItemClick(int position) {
        ImageUploadInfo selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());
        /*
        Glide.with(this)
                .load(selectedItem.getImageURL())
                .into(imgChoose2);*/


        Intent i = new Intent(DisplayImagesCollageActivity.this , ShowImageGalleryOfCollage.class);

        i.putExtra("imgId", selectedItem.getid());
        i.putExtra("link", selectedItem.getImageURL());
        i.putExtra("date", selectedItem.getDate());

       /* i.putExtra("imgId", selectedItem.getid());
        i.putExtra("link", selectedItem.getImageURL());
        i.putExtra("alias", selectedItem.getname());
        i.putExtra("date", selectedItem.getdate());
        i.putExtra("price", selectedItem.getprice());
        i.putExtra("tag", selectedItem.gettag());*/
        startActivity(i);
        //Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        /*Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/image%2Fwhite_1.jpg?alt=media&token=c6cd5a49-7e5f-4352-9f02-5a7a5fcb4ccc")
                .into(img);*/

        ImageUploadInfo selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        Intent i = new Intent(DisplayImagesCollageActivity.this , ShowImageGalleryOfCollage.class);
        i.putExtra("imgName",selectedItem.getImageName());
        i.putExtra("link",selectedItem.getImageURL());
        i.putExtra("imgId",selectedItem.getid());
        /*i.putExtra("imgId", selectedItem.getid());
        i.putExtra("link", selectedItem.getImageURL());
        i.putExtra("alias", selectedItem.getname());
        i.putExtra("date", selectedItem.getdate());
        i.putExtra("price", selectedItem.getprice());
        i.putExtra("tag", selectedItem.gettag());*/
        startActivity(i);
        //Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onDeleteClick(int position) {
        ImageUploadInfo selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(DisplayImagesCollageActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
        /*Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/4j4AfAqmPcPzVwOshvHwouhLvVw1%2FAll_Image_Uploads%2Fsample_5.jpg?alt=media&token=3b9dc544-53d1-47fb-b995-497693e644b0")
                .into(img1);*/

    }

    public void runQrcode(View v) {

        Intent it = new Intent(DisplayImagesCollageActivity.this, QRcode.class);
        it.putExtra("userid",firebaseAuth.getCurrentUser().toString());
        startActivity(it);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menudica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.AddMenu:{
                startActivity(new Intent(DisplayImagesCollageActivity.this, PickFashion.class));
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(DisplayImagesCollageActivity.this,SecondActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(i);
        //finish();
    }

}