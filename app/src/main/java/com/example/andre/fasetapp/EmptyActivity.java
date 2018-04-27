package com.example.andre.fasetapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.FileUriExposedException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import static android.content.Intent.EXTRA_STREAM;

public class EmptyActivity extends AppCompatActivity {

    Button buttonCollege;
    Uri imageUriOfPage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    EditText CollectionName;
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
        progressDialog = new ProgressDialog(EmptyActivity.this);


        imageUriOfPage = (Uri) intent.getData();
        //imageUriOfPage = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUriOfPage != null) {
            text.setText(imageUriOfPage.toString());
            // Update UI to reflect image being shared.  Here you would need to read the
            // data from the URI.
        }

        buttonCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        if (imageUriOfPage != null) {
           //Toast.makeText(EmptyActivity.this, "ada", Toast.LENGTH_LONG).show();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(Storage_Path + System.currentTimeMillis() + "." + ".jpg" );
            ref.putFile(imageUriOfPage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempImageName = CollectionName.getText().toString().trim();

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, taskSnapshot.getDownloadUrl().toString());

                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userCollage").child(ImageUploadId).setValue(imageUploadInfo);

                            startActivity(new Intent(EmptyActivity.this, SecondActivity.class));


                            //Toast.makeText(EmptyActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EmptyActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

            Toast.makeText(EmptyActivity.this, "ga ada", Toast.LENGTH_LONG).show();

        }
    }


}
