package com.example.andre.fasetapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateCollageDetail extends AppCompatActivity {

    Button buttonCollege;
    Uri imageUriOfPage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    EditText CollectionName, ImageCategory;
    private String formattedDate, categoryHolder;
    String Storage_Path = "Collection_Image_Uploads/";
    private String dateOfDate;
    // Root Database Name for Firebase Database.
    public static final String Database_Path = "All_Image_Uploads_Database";
    ProgressDialog progressDialog ;
    String imgId, imgLink, imgName;
    private String sameDate, sameLink,sameId;
    private FirebaseDatabase firebaseDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_collage_detail);

        Intent intent = getIntent();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        //Bitmap bitmap = (Bitmap)this.getIntent().getParcelableExtra("Bitmap");
        ImageView view = (ImageView) findViewById(R.id.imageView81);
        view.setImageBitmap(BitmapHelper.getInstance().getBitmap());
        //String t = getIntent().getExtras(EXTRA_STREAM);
        TextView text = (TextView) findViewById(R.id.textView2);
        buttonCollege = (Button) findViewById(R.id.buttonCl);
        CollectionName = (EditText) findViewById(R.id.CollectionNameText);
        ImageCategory = (EditText) findViewById(R.id.ImageCategoryEditText);
        progressDialog = new ProgressDialog(UpdateCollageDetail.this);

        //dateOfDate = getIntent().getStringExtra("CatchDate");
        //text.setText(date);
        imageUriOfPage = (Uri) intent.getData();
        //imageUriOfPage = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUriOfPage != null) {
            //text.setText("Save This Outfit On " + dateOfDate + "?");
            // Update UI to reflect image being shared.  Here you would need to read the
            // data from the URI.
        }

        intent = getIntent();

        imgId = intent.getStringExtra("imageId");
        imgName = intent.getStringExtra("imageName");
        imgLink = intent.getStringExtra("imageUrl");

        Glide.with(this)
                .load(imgLink)
                .into(view);
        CollectionName.setText(imgName);


        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(firebaseAuth.getUid()).child("userCollage").child(imgId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageUploadInfo updateAttr = dataSnapshot.getValue(ImageUploadInfo.class);
                CollectionName.setText(updateAttr.getImageName());
                ImageCategory.setText(updateAttr.getcategory());

                sameDate = updateAttr.getDate().toString();
                sameLink = updateAttr.getImageURL().toString();
                sameId = updateAttr.getid().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateCollageDetail.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        ImageCategory.setFocusable(false);
        ImageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] category = new String[]{
                        "Casual",
                        "Party",
                        "Formal",
                        "Comfy",
                        "Sport"
                };

                // Boolean array for initial selected items

                final List<String> categoryList = Arrays.asList(category);

                //ImageTag.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateCollageDetail.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Category");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(CollectionName.getWindowToken(), 0);


                builder.setSingleChoiceItems(category, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ImageTag.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        categoryHolder = category[i].toString();
                        ImageCategory.setText(categoryHolder);



                    }
                });

                /*
                // Set the negative/no button click listener
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the negative button
                    }
                });*/

                // Set the neutral/cancel button click listener
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the neutral button
                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();


            }

        });


        buttonCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling method to upload selected image on Firebase storage.
                //UploadImageFileToFirebaseStorage();
                String nameUpdate = CollectionName.getText().toString();


                validate();

                ImageUploadInfo userProfile = new ImageUploadInfo(sameId, nameUpdate , sameLink, sameDate,categoryHolder);

                databaseReference.setValue(userProfile);

                finish();

            }
        });


        //text.setText(Intent.EXTRA_STREAM);
    }

    private Boolean validate(){
        Boolean result = false;

        String CheckName = CollectionName.getText().toString();



        /*String passwordHandler = password.getText().toString();
        if (password.isEmpty() || password.length() < 6) {  passwordText.setError("Password cannot be less than 6 characters!");
        }
        else {
            passwordText.setError(null);
            startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
        }*/
        if(TextUtils.isEmpty(CheckName)){
            CollectionName.setError("The item cannot be empty");
        }



        else{
            result = true;
        }

        return result;
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    /*
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (imageUriOfPage != null) {
            //Toast.makeText(EmptyActivity.this, "ada", Toast.LENGTH_LONG).show();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(Storage_Path + System.currentTimeMillis()  + ".jpg" );
            ref.putFile(imageUriOfPage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempImageName = CollectionName.getText().toString().trim();
                            String date2 = "Apr 2 2018";
                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            String ImageUploadId = databaseReference.push().getKey();
                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(ImageUploadId, TempImageName,taskSnapshot.getDownloadUrl().toString(),formattedDate);

                            // Getting image upload ID.


                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userCollage").child(ImageUploadId).setValue(imageUploadInfo);

                            //finish();
                            Intent i = new Intent(UpdateCollageDetail.this, SecondActivity.class);
                            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP|i.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            //startActivity(new Intent(EmptyActivity1.this, CalendarActivity.class));



                            //Toast.makeText(EmptyActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UpdateCollageDetail.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

            Toast.makeText(UpdateCollageDetail.this, "ga ada", Toast.LENGTH_LONG).show();

        }*/
    }


