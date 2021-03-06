package com.example.andre.fasetapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity{

    private EditText userName, userPassword, userEmail, userAge, userSex;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;
    String email, name, age, password, sex;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    String itemCategory;
    Uri imagePath;
    private StorageReference storageReference;

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        /*firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        //StorageReference myRef = storageReference.child("users").setValue(firebaseAuth.getUid());
        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("images/");
                //intent.setType("users/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), PICK_IMAGE);
            }
        });*/
        userSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] category = new String[]{

                        "Male",
                        "Female"
                };

                // Boolean array for initial selected items

                final List<String> categoryList = Arrays.asList(category);

                //ImageTag.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Category");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(userName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(userEmail.getWindowToken(), 0);
                im.hideSoftInputFromWindow(userPassword.getWindowToken(), 0);
                im.hideSoftInputFromWindow(userAge.getWindowToken(), 0);

                builder.setSingleChoiceItems(category, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ImageTag.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        itemCategory = category[i].toString();
                        userSex.setText(itemCategory);



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


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    //Upload data to the database
                    String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendEmailVerification();
                                //sendUserData();
                                //firebaseAuth.signOut();
                                //Toast.makeText(RegistrationActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                                //finish();
                                //startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            }else{
                                /*password = userPassword.getText().toString();
                                if( password.length() < 5 )
                                {
                                    Toast.makeText(RegistrationActivity.this, "The password you enter must be at least 6 characters", Toast.LENGTH_SHORT).show();
                                }*/
                                Toast.makeText(RegistrationActivity.this, "Registration Failed, Check All The Fields Again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

    }

    private void setupUIViews(){
        userName = (EditText)findViewById(R.id.etUserName);
        userPassword = (EditText)findViewById(R.id.etUserPassword);
        userEmail = (EditText)findViewById(R.id.etUserEmail);
        regButton = (Button)findViewById(R.id.btnRegister);
        userLogin = (TextView)findViewById(R.id.tvUserLogin);
        userAge = (EditText)findViewById(R.id.etAge);
        userSex  = (EditText)findViewById(R.id.etSex);
        userProfilePic = (ImageView)findViewById(R.id.ivProfile);

        userName.setOnEditorActionListener(new DoneOnEditorActionListener());
        userPassword.setOnEditorActionListener(new DoneOnEditorActionListener());
        userEmail.setOnEditorActionListener(new DoneOnEditorActionListener());
        userAge.setOnEditorActionListener(new DoneOnEditorActionListener());

    }

    private Boolean validate(){
        Boolean result = false;

        name = userName.getText().toString();
        password = userPassword.getText().toString();
        email = userEmail.getText().toString();
        age = userAge.getText().toString();
        sex = itemCategory;

        /*String passwordHandler = password.getText().toString();
        if (password.isEmpty() || password.length() < 6) {  passwordText.setError("Password cannot be less than 6 characters!");
        }
        else {
            passwordText.setError(null);
            startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
        }*/
        if(TextUtils.isEmpty(name)){
            userName.setError("The item cannot be empty");
        }
        if(TextUtils.isEmpty(password)){
            userPassword.setError("Password field cannot be empty");
        }
        emailValidate();

        if(password.length() < 5 ){ userPassword.setError("Password must be at least 6 characters");}

        if(TextUtils.isEmpty(email)){
            userEmail.setError("E-mail field cannot be empty");
        }

        if(TextUtils.isEmpty(sex)){
            userEmail.setError("Choose your sex");
        }

       /*
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        if(matcher.matches() == false){
            userEmail.setError("Please Input Your E-mail Address Correctly");
            //Toast.makeText(this, "Email address invalid", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(this, "Email address valid", Toast.LENGTH_SHORT).show();
            //result = true;
            //matcher.matches(); }

        }*/



        if(TextUtils.isEmpty(age)){
            userAge.setError("Age field cannot be empty");
        }



//isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || imagePath != null
        if(name.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || imagePath != null){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();

            if( password.length() < 5 )
            {
                //Toast.makeText(this, "The password you enter must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }

        }



        else{
            result = true;
        }

        return result;
    }

    private Boolean emailValidate(){
        Boolean result = false;

        email = userEmail.getText().toString();

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        if(matcher.matches() == false){
            userEmail.setError("Please Input Your E-mail Address Correctly");
            //Toast.makeText(this, "Email address invalid", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(this, "Email address valid", Toast.LENGTH_SHORT).show();
            result = true;
            //matcher.matches(); }

        }

        return result;
    }



    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegistrationActivity.this, "Verification mail sent! Check Your Email Address For Verification", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(RegistrationActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //satu or dua
        //DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        DatabaseReference myRef = firebaseDatabase.getReference("users");
        /*StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegistrationActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
            }
        });*/


        UserProfile userProfile = new UserProfile(age, email, name, sex);
        myRef.child(firebaseAuth.getUid()).child("userInfo").setValue(userProfile);
    }
}