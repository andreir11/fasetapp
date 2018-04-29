package com.example.andre.fasetapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesGalleryActivity extends AppCompatActivity implements RecyclerViewwAdapterr.OnItemClickListener {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerViewwAdapterr adapter;
    FirebaseAuth firebaseAuth;
    // Creating Progress dialog
    ProgressDialog progressDialog;
    ImageView img;
    ImageView img1;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images_gallery);

        //img = (ImageView)findViewById(R.id.imageView4) ;
        //img1 = (ImageView)findViewById(R.id.imageView6) ;
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
        progressDialog = new ProgressDialog(DisplayImagesGalleryActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();

        adapter = new RecyclerViewwAdapterr(DisplayImagesGalleryActivity.this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(DisplayImagesGalleryActivity.this);

        //recyclerView.setAdapter(new G);
        mStorage = FirebaseStorage.getInstance();
        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("userGallery");

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


    @Override
    public void onItemClick(int position) {

        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        /*Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/image%2Fwhite_1.jpg?alt=media&token=c6cd5a49-7e5f-4352-9f02-5a7a5fcb4ccc")
                .into(img);*/
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        /*ImageUploadInfo selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(DisplayImagesGalleryActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });*/
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/4j4AfAqmPcPzVwOshvHwouhLvVw1%2FAll_Image_Uploads%2Fsample_5.jpg?alt=media&token=3b9dc544-53d1-47fb-b995-497693e644b0")
                .into(img1);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
    }


}