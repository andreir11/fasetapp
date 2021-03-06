package com.example.andre.fasetapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayImagesDailyActivity extends AppCompatActivity implements RecyclerViewwAdapterrr.OnItemClickListener {

    // Creating DatabaseReference.
    DatabaseReference databaseReference, databaseRef;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    // Creating RecyclerView.
    RecyclerView recyclerView;
    String[] listitems;
    // Creating RecyclerView.Adapter.
    RecyclerViewwAdapterrr adapter;
    FirebaseAuth firebaseAuth;
    // Creating Progress dialog
    ProgressDialog progressDialog;
    private Button btnSort;
    public Button buttonToPick;
    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();
    String date, temp , type;
    TextView tx;
    StorageReference storageReference;
    Uri imageUri;
    String Storage_Path = "Daily_Image_Uploads/";
    ImageView img;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images_daily);
        //buttonToPick = (Button)findViewById(R.id.buttonToP);
        img = (ImageView)findViewById(R.id.imageViewInvisible) ;
        btnSort = (Button) findViewById(R.id.buttonSorting);

        getSupportActionBar().setTitle("Choose Outfit");
        Intent intent = getIntent();
        date = intent.getStringExtra("CatchDate");
        temp = intent.getStringExtra("temp");
        type = intent.getStringExtra("weatherType");

        tx = (TextView)findViewById(R.id.textView6) ;
        //tx.setText(date + temp +type );
        //tx.setText("asdasdasafsfasfasfasfasfasfasfasfafasfasfasfasfasfasfasf");
        //Toast.makeText(getApplicationContext(),"No Button Clicked " +temp,Toast.LENGTH_SHORT).show();
        //tx.setText(temp + "   " + type);
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
        progressDialog = new ProgressDialog(DisplayImagesDailyActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images...");

        // Showing progress dialog.
        progressDialog.show();

        adapter = new RecyclerViewwAdapterrr(DisplayImagesDailyActivity.this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(DisplayImagesDailyActivity.this);

        //recyclerView.setAdapter(new G);
        mStorage = FirebaseStorage.getInstance();
        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("userCollage");
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("userDailyWear");

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listitems = new String[]{"All","Casual",
                        "Party",
                        "Formal",
                        "Comfy",
                        "Sport"};
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DisplayImagesDailyActivity.this);
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


    @Override
    public void onItemClick(int position) {
        ImageUploadInfo selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        final String DailyUrl =  selectedItem.getImageURL();
        final String CollageName = selectedItem.getImageName();
        final String Date = date;

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());

        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayImagesDailyActivity.this);

        // Set a title for alert dialog
        builder.setTitle("On " +date);

        // Ask the final question
        builder.setMessage("Are you sure want to select this outfit ?");

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when user clicked the Yes button
                // Set the TextView visibility GONE
                //tv.setVisibility(View.GONE);


                String ImageUploadId = databaseReference.push().getKey();
                ImageUploadInfoWithDateOnCalendar imageUploadInfo = new ImageUploadInfoWithDateOnCalendar(CollageName,DailyUrl,Date);

                // Getting image upload ID.


                // Adding image upload id s child element into databaseReference.
                databaseRef.child(ImageUploadId).setValue(imageUploadInfo);

                //finish();
                Intent i = new Intent(DisplayImagesDailyActivity.this, CalendarActivity.class);
                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                finish();
                //startActivity(n





                Toast.makeText(getApplicationContext(),
                        "Your outfit have been saved on " +date,Toast.LENGTH_SHORT).show();
            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when No button clicked
               // Toast.makeText(getApplicationContext(),"No Button Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();


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
        Intent i = new Intent(DisplayImagesDailyActivity.this , ShowImageGalleryOfCollage.class);
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
                Toast.makeText(DisplayImagesDailyActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
        /*Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/4j4AfAqmPcPzVwOshvHwouhLvVw1%2FAll_Image_Uploads%2Fsample_5.jpg?alt=media&token=3b9dc544-53d1-47fb-b995-497693e644b0")
                .into(img1);*/

    }

    public void UploadImageFileToFirebaseStorage() {

        Bitmap bitmap =getBitmapFromView(img);
        BitmapHelper.getInstance().setBitmap(bitmap);
        try {
            File file = new File(this.getExternalCacheDir(),"logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            //intent.putExtra("Bitmap", bitmap);

            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);

            if (type == null)
                type = "*/*";

            imageUri = Uri.fromFile(file);


            //intent.setDataAndType(Uri.fromFile(file), type);
            BitmapHelper.getInstance().getBitmap();
            //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Checking whether FilePathUri Is empty or not.
        if (imageUri != null) {
            //Toast.makeText(EmptyActivity.this, "ada", Toast.LENGTH_LONG).show();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            tx.setText((CharSequence) imageUri.toString());



            StorageReference ref = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(Storage_Path + System.currentTimeMillis() + "." + ".jpg" );
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            //CollectionUploadInfo imageUploadInfo = new CollectionUploadInfo(date , name, taskSnapshot.getDownloadUrl().toString());

                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                           // databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userDailyWear").child(ImageUploadId).setValue(imageUploadInfo);
                            finish();
                            startActivity(new Intent(DisplayImagesDailyActivity.this, CalendarActivity.class));
                            Toast.makeText(DisplayImagesDailyActivity.this, "Sipppp", Toast.LENGTH_LONG).show();


                            //Toast.makeText(EmptyActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(DisplayImagesDailyActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }


        else {

            Toast.makeText(DisplayImagesDailyActivity.this, "ga ada", Toast.LENGTH_LONG).show();

        }
    }







    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menudida, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.AddMenu:{
                Intent i = new Intent(DisplayImagesDailyActivity.this, PickFashion1.class);
                i.putExtra("CatchDate",date);
                i.putExtra("temp",temp);
                i.putExtra("type",type);
                //finish();
                startActivity(i);
                break;
            }


        }
        return super.onOptionsItemSelected(item);
    }



}