package com.example.andre.fasetapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EmptyActivity1 extends AppCompatActivity {

    Button buttonCollege;
    Uri imageUriOfPage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    EditText CollectionName;
    private String formattedDate;

    String Storage_Path = "Collection_Image_Uploads/";
    private String dateOfDate;
    // Root Database Name for Firebase Database.
    public static final String Database_Path = "All_Image_Uploads_Database";
    ProgressDialog progressDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty1);
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
        progressDialog = new ProgressDialog(EmptyActivity1.this);

        dateOfDate = getIntent().getStringExtra("CatchDate");
        //text.setText(date);
        imageUriOfPage = (Uri) intent.getData();
        //imageUriOfPage = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUriOfPage != null) {
            //text.setText("Save This Outfit On " + dateOfDate + "?");
            // Update UI to reflect image being shared.  Here you would need to read the
            // data from the URI.
        }
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        formattedDate = df.format(c);

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
                            Intent i = new Intent(EmptyActivity1.this, SecondActivity.class);
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
                            Toast.makeText(EmptyActivity1.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

            Toast.makeText(EmptyActivity1.this, "ga ada", Toast.LENGTH_LONG).show();

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
