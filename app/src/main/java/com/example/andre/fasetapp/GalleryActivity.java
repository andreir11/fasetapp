package com.example.andre.fasetapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity {

    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "All_Image_Uploads_Database";

    // Creating button.
    Button ChooseButton, UploadButton, DisplayImageButton;

    // Creating EditText.
    EditText ImageName, ImageCategory, ImageTag, ImagePrice, ImageSeason, ImageBrand, ImageSize, ImageSleeve ;
    TextInputLayout ImageSleeveInput;
    String itemCategory;
    String itemSleeve;
    // Creating ImageView.
    ImageView SelectImage;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference ,dbRef;
    FirebaseAuth firebaseAuth;
    TextView textview;
    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog ;

    String formattedDate;
    String tagHolder, seasonHolder;
    private String nameCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        dbRef=FirebaseDatabase.getInstance().getReference("users");
        textview =  (TextView)findViewById(R.id.textView1);
        //Assign ID'S to button.
        ChooseButton = (Button)findViewById(R.id.ButtonChooseImage);
        UploadButton = (Button)findViewById(R.id.ButtonUploadImage);


        //DisplayImageButton = (Button)findViewById(R.id.DisplayImagesButton);

        // Assign ID's to EditText.
        ImageName = (EditText)findViewById(R.id.ImageNameEditText);
        ImageCategory = (EditText)findViewById(R.id.ImageCategoryEditText);
        ImagePrice = (EditText)findViewById(R.id.ImagePriceEditText);
        ImageSeason = (EditText)findViewById(R.id.ImageSeasonEditText);
        ImageTag = (EditText)findViewById(R.id.ImageTagEditText);
        ImageBrand = (EditText)findViewById(R.id.ImageBrandEditText);
        ImageSize = (EditText)findViewById(R.id.ImageSizeEditText);
        ImageSleeve = (EditText)findViewById(R.id.ImageSleeveEditText);
        ImageSleeveInput = (TextInputLayout)findViewById(R.id.ImageSleeveTextInput);

        // Assign ID'S to image view.
        SelectImage = (ImageView)findViewById(R.id.ShowImageUpload);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        formattedDate = df.format(c);



        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(GalleryActivity.this);


        ImageSleeve.setVisibility(View.GONE);
        ImageSleeveInput.setVisibility(View.GONE);

        ImageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageName.setOnEditorActionListener(new DoneOnEditorActionListener());
                FirebaseDatabase firebaseDatabaseCheck = FirebaseDatabase.getInstance();
                DatabaseReference myRef = firebaseDatabaseCheck.getReference("users");

                //myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userInfo").child("userName").setValue("test");

                //Query query = myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userGallery").orderByChild("imageName").equalTo("upup");
                final String aa = ImageName.getText().toString().trim();
                Query query = myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userDailyWear").orderByChild("imageDailyD").equalTo("May 27, 2018");


                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //imageD.setImageResource(0);
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            if (singleSnapshot != null) {

                                Toast.makeText(GalleryActivity.this, "Name already exist", Toast.LENGTH_LONG).show();
                                //ImageName.setError("[Unique]Name already exist");


                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Toast.makeText(GalleryActivity.this, "Name not exist", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

       // ImageBrand.setFocusable(false);
       // ImagePrice.setFocusable(false);
       // ImageSize.setFocusable(false);

        SelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        ImagePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePrice.setOnEditorActionListener(new DoneOnEditorActionListener());

            }
        });

        //bring in tag dialog
        ImageTag.setFocusable(false);
        ImageTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] tags = new String[]{
                        "Casual",
                        "Party",
                        "Formal",
                        "Comfy",
                        "Sport"
                };

                // Boolean array for initial selected items
                final boolean[] checkedTags = new boolean[]{
                        true, // Red
                        false, // Green
                        false, // Blue
                        false, // Purple
                        false // Olive

                };
                final List<String> tagsList = Arrays.asList(tags);

                //ImageTag.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Tags");
                //set the keyboard to hide
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(ImageName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImagePrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageSize.getWindowToken(), 0);


                builder.setMultiChoiceItems(tags, checkedTags, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        // Update the current focused item's checked status
                        checkedTags[which] = isChecked;
                        /*
                        // Get the current focused item
                        String currentItem = tagsList.get(which);

                        // Notify the current action
                        Toast.makeText(getApplicationContext(),
                                currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();*/

                    }
                });

                // Specify the dialog is not cancelable
                builder.setCancelable(false);



                // Set the positive/yes button click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        //ImageTag.setText("Your preferred colors..... \n");



                        ImageTag.setText("");
                        StringBuilder commaSepValueBuilder = new StringBuilder();
                        for (int i = 0; i<checkedTags.length; i++){
                            boolean checked = checkedTags[i];
                            if (checked) {


                                    commaSepValueBuilder.append(tagsList.get(i));

                                    //if the value is not the last element of the list
                                    //then append the comma(,) as well
                                    if ( i != tagsList.size()){
                                        commaSepValueBuilder.append(", ");

                                    }


                                //ImageTag.setText(ImageTag.getText() + colorsList.get(i) + ", ");
                            }
                            else
                            {
                                ImageTag.setText("");
                            }

                        }
                        commaSepValueBuilder.deleteCharAt(commaSepValueBuilder.length()-2);
                        ImageTag.setText(ImageTag.getText() + commaSepValueBuilder.toString());
                        tagHolder = commaSepValueBuilder.toString();


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



                /*mBuilder.setSingleChoiceItems(listitems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ImageTag.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        itemTag = listitems[i].toString();
                        ImageTag.setText(itemTag);


                    }
                });
                mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                mBuilder.setMultiChoiceItems(
                        listitems, clickedCategories, new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        clickedCategories[which]=isChecked;
                    }
                });
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder sb=new StringBuilder();
                        for(int i=0; i<clickedCategories.length;i++)
                        {
                            if(clickedCategories[i])
                            {
                                sb.append((sb.length()==0?"":", ")+categoryStrings[i]);
                            }
                        }
                        ImageTag.setText(sb.toString());
                        dialog.dismiss();
                    }
                });


                AlertDialog mDialog = mBuilder.create();
                mDialog.show();*/

            }

        });

        ImageSeason.setFocusable(false);
        ImageSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] seasons = new String[]{
                        "Spring",
                        "Summer",
                        "Fall",
                        "Winter"
                };

                // Boolean array for initial selected items
                final boolean[] checkedSeasons = new boolean[]{
                        true, // Red
                        false, // Green
                        false, // Blue
                        false, // Purple
                        false // Olive

                };
                final List<String> seasonsList = Arrays.asList(seasons);

                //ImageTag.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Seasons");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(ImageName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImagePrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageSize.getWindowToken(), 0);

                builder.setMultiChoiceItems(seasons, checkedSeasons, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        // Update the current focused item's checked status
                        checkedSeasons[which] = isChecked;

                        // Get the current focused item
                        /*String currentItem = seasonsList.get(which);

                        // Notify the current action
                        Toast.makeText(getApplicationContext(),
                                currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();*/
                    }
                });

                // Specify the dialog is not cancelable
                builder.setCancelable(false);



                // Set the positive/yes button click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        //ImageTag.setText("Your preferred colors..... \n");

                        ImageSeason.setText("");
                        StringBuilder commaSepValueBuilder = new StringBuilder();
                        for (int i = 0; i<checkedSeasons.length; i++){
                            boolean checked = checkedSeasons[i];
                            if (checked) {


                                commaSepValueBuilder.append(seasonsList.get(i));

                                //if the value is not the last element of the list
                                //then append the comma(,) as well
                                if ( i != seasonsList.size()){
                                    commaSepValueBuilder.append(", ");
                                }
                                //ImageTag.setText(ImageTag.getText() + colorsList.get(i) + ", ");
                            }


                        }
                        commaSepValueBuilder.deleteCharAt(commaSepValueBuilder.length()-2);
                        ImageSeason.setText(ImageSeason.getText() + commaSepValueBuilder.toString());

                        seasonHolder = commaSepValueBuilder.toString();

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

        ImageCategory.setFocusable(false);
        ImageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] category = new String[]{
                        "Top",
                        "Bottom",
                        "Jacket",
                        "Shoes",
                        "Accesories"
                };

                // Boolean array for initial selected items

                final List<String> categoryList = Arrays.asList(category);

                //ImageTag.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Category");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(ImageName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImagePrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageSize.getWindowToken(), 0);

                builder.setSingleChoiceItems(category, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ImageTag.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        itemCategory = category[i].toString();
                        ImageCategory.setText(itemCategory);

                        if(itemCategory == "Top" || itemCategory == "Bottom"){
                            ImageSleeve.setVisibility(View.VISIBLE);
                            ImageSleeveInput.setVisibility(View.VISIBLE);

                        }

                        else if(itemCategory=="Jacket"){
                            ImageSleeve.setVisibility(View.GONE);
                            ImageSleeveInput.setVisibility(View.GONE);
                            itemSleeve = "Long";
                        }


                        else{
                            ImageSleeveInput.setVisibility(View.GONE);
                            ImageSleeve.setVisibility(View.GONE);
                            itemSleeve = "none";

                        }


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

        ImageSleeve.setFocusable(false);
        ImageSleeve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] category = new String[]{
                        "Long",
                        "Short"
                };

                // Boolean array for initial selected items

                final List<String> categoryList = Arrays.asList(category);

                //ImageTag.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Category");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(ImageName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImagePrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(ImageSize.getWindowToken(), 0);

                builder.setSingleChoiceItems(category, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ImageTag.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        itemSleeve = category[i].toString();
                        ImageSleeve.setText(itemSleeve);


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




        // Adding click listener to Choose image button.
        ChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });


        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate();
                // Calling method to upload selected image on Firebase storage.
                UploadImageFileToFirebaseStorage();




            }
        });



    }

    private Boolean validate(){
        Boolean result = false;

        String CheckName = ImageName.getText().toString();
        String CheckPrice = ImagePrice.getText().toString();
        String CheckTag = ImageTag.getText().toString();
        String CheckSeason = ImageSeason.getText().toString();
        String CheckCategory = ImageCategory.getText().toString();
        String CheckSize = ImageSize.getText().toString();
        String CheckBrand = ImageBrand.getText().toString();


        /*String passwordHandler = password.getText().toString();
        if (password.isEmpty() || password.length() < 6) {  passwordText.setError("Password cannot be less than 6 characters!");
        }
        else {
            passwordText.setError(null);
            startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
        }*/
        if(SelectImage.getDrawable() == null){
            Toast.makeText(GalleryActivity.this, "You Haven't Selected An Image", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(CheckName)){
            ImageName.setError("The item cannot be empty");
        }
        if(TextUtils.isEmpty(CheckPrice)){
            ImagePrice.setError("The item cannot be empty");
        }
        if(TextUtils.isEmpty(CheckSize)) {
            ImageSize.setError("The item cannot be empty");
        }
        if(TextUtils.isEmpty(CheckBrand)){
            ImageBrand.setError("The item cannot be empty");
        }
        else if(TextUtils.isEmpty(CheckTag)){
            ImageTag.setError("The item cannot be empty");
        }
        else if(TextUtils.isEmpty(CheckSeason)){
            ImageSeason.setError("The item cannot be empty");
        }
        else if(TextUtils.isEmpty(CheckCategory)){
            ImageCategory.setError("The item cannot be empty");
        }






//isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || imagePath != null



        else{
            result = true;
        }

        return result;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();


            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                SelectImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                //ChooseButton.setText((CharSequence) FilePathUri.toString());
                ChooseButton.setText("Change Image");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    private void collectPhoneNumbers(Map<String,Object> users) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((String) singleUser.get("name"));
        }
        nameCheck = ImageName.getText().toString();

        System.out.println(phoneNumbers.toString());
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null && validate()) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            //databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userGallery").child("name");
            /*dbRef.child(firebaseAuth.getCurrentUser().getUid()).child("userGallery");
            final String nameCheck = ImageName.getText().toString();
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
                    /*if(dataSnapshot.child(nameCheck).exists())
                    {
                        ImageName.setError("Name have to be unique");
                    }
                    String UserNameCheck = "";
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        UserNameCheck = (String) dsp.getValue(); //add result into array list

                    }
                    if(UserNameCheck == ImageName.getText().toString())
                    {
                        ImageName.setError("Name have to be unique");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/



            // Creating second StorageReference.
           final String ImageUploadId = databaseReference.push().getKey();
            StorageReference storageReference2nd = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(Storage_Path).child(ImageUploadId).child(ImageName.getText().toString()+ "." + GetFileExtension(FilePathUri));


            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.
                            String TempImageName = ImageName.getText().toString().trim();
                            String TempImageBrand = ImageBrand.getText().toString().trim();
                            String TempImagePrice = ImagePrice.getText().toString().trim();
                            String TempImageSize = ImageSize.getText().toString().trim();
                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            //price

                            // Getting image upload ID.

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadAttributes imageUploadInfo = new ImageUploadAttributes(ImageUploadId.toString(),TempImageName, taskSnapshot.getDownloadUrl().toString(),
                                    formattedDate,tagHolder,TempImagePrice,TempImageBrand,seasonHolder,itemCategory, TempImageSize, itemSleeve);



                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userGallery").child(ImageUploadId).setValue(imageUploadInfo);
                            Intent i = new Intent(GalleryActivity.this, DisplayImagesGalleryActivity.class);

                            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(GalleryActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        }
        else {

            Toast.makeText(GalleryActivity.this, "Please Check If You Have Selected Image or Fields", Toast.LENGTH_LONG).show();

        }
    }


}