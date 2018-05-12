package com.example.andre.fasetapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.FileUriExposedException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.cert.Extension;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Intent.EXTRA_STREAM;

public class EmptyActivity extends AppCompatActivity {

    Button buttonCollege;
    Uri imageUriOfPage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    EditText CollectionName, ImageCategory;
    public String formattedDate, categoryHolder;
    String Storage_Path = "Collection_Image_Uploads/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "All_Image_Uploads_Database";
    ProgressDialog progressDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
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

        progressDialog = new ProgressDialog(EmptyActivity.this);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        formattedDate = df.format(c);

        imageUriOfPage = (Uri) intent.getData();

        CollectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionName.setOnEditorActionListener(new DoneOnEditorActionListener());

            }
        });



        //imageUriOfPage = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUriOfPage != null) {
            //text.setText(imageUriOfPage.toString());
            // Update UI to reflect image being shared.  Here you would need to read the
            // data from the URI.
            //text.setText("");
        }

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
                AlertDialog.Builder builder = new AlertDialog.Builder(EmptyActivity.this);

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

                validate();
                // Calling method to upload selected image on Firebase storage.
                UploadImageFileToFirebaseStorage();

            }
        });


        //text.setText(Intent.EXTRA_STREAM);
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (imageUriOfPage != null && validate()) {
            //Toast.makeText(EmptyActivity.this, "ada", Toast.LENGTH_LONG).show();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(Storage_Path + System.currentTimeMillis() + "." + ".jpg");
            ref.putFile(imageUriOfPage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempImageName = CollectionName.getText().toString().trim();

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();
                            String ImageUploadId = databaseReference.push().getKey();
                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(ImageUploadId, TempImageName, taskSnapshot.getDownloadUrl().toString(), formattedDate,categoryHolder);

                            // Getting image upload ID.

                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userCollage").child(ImageUploadId).setValue(imageUploadInfo);

                            Intent i = new Intent(EmptyActivity.this, DisplayImagesDailyActivity.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            Toast.makeText(EmptyActivity.this, "New Collage Has Been Uploaded", Toast.LENGTH_SHORT).show();

                            //Toast.makeText(EmptyActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EmptyActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {

            Toast.makeText(EmptyActivity.this, "Give initial/name to your outfit", Toast.LENGTH_LONG).show();

        }





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




//isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || imagePath != null



        else{
            result = true;
        }

        return result;
    }






}
