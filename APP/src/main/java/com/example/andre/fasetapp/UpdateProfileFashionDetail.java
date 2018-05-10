package com.example.andre.fasetapp;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UpdateProfileFashionDetail extends AppCompatActivity {

    private EditText newName, newPrice, newTag, newCategory, newBrand, newSeason, newSize, newSleeve ;
    private TextView newDate;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private String imgId, imgLink, imgDate;
    private String sameDate, sameLink, sameId;
    private FirebaseDatabase firebaseDatabase;
    private ImageView imgUploadNew;
    private int Image_Request_Code = 7;
    private Uri FilePathUri;

    private Button btnChoose;

    private String tagHolder, seasonHolder;
    private String itemCategory, itemSleeve;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_fashion_detail);

        newName = (EditText) findViewById(R.id.ImageNameEditTextUpdate);
        newCategory = (EditText)findViewById(R.id.ImageCategoryEditTextUpdate);
        newBrand =(EditText) findViewById(R.id.ImageBrandEditTextUpdate);
        newPrice = (EditText)findViewById(R.id.ImagePriceEditTextUpdate);
        newSeason = (EditText)findViewById(R.id.ImageSeasonEditTextUpdate);
        newTag = (EditText)findViewById(R.id.ImageTagEditTextUpdate);
        newSize = (EditText) findViewById(R.id.ImageSizeEditTextUpdate);
        newSleeve = (EditText) findViewById(R.id.ImageSleeveEditTextUpdate);
        //newDate = findViewById(R.id.dateOfInsert);

        textview =  (TextView)findViewById(R.id.textView1);
        btnChoose = (Button)findViewById(R.id.ButtonChooseImage);
        save = (Button)findViewById(R.id.ButtonUploadImageUpdate);
        imgUploadNew = (ImageView)findViewById(R.id.ShowImage) ;


        Intent intent = getIntent();
        imgId = intent.getStringExtra("imageId");
        imgLink = intent.getStringExtra("imageUrl");
        imgDate = intent.getStringExtra("date");



        Glide.with(this)
                .load(imgLink)
                .into(imgUploadNew);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newName.setOnEditorActionListener(new DoneOnEditorActionListener());

        /*imgUploadNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });*/



        newPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPrice.setOnEditorActionListener(new DoneOnEditorActionListener());

            }
        });

        //bring in tag dialog
        newTag.setFocusable(false);
        newTag.setOnClickListener(new View.OnClickListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileFashionDetail.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Tags");
                //set the keyboard to hide
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(newName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newPrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newSize.getWindowToken(), 0);


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



                        newTag.setText("");
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
                                newTag.setText("");
                            }

                        }
                        commaSepValueBuilder.deleteCharAt(commaSepValueBuilder.length()-2);
                        newTag.setText(newTag.getText() + commaSepValueBuilder.toString());
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

        newSeason.setFocusable(false);
        newSeason.setOnClickListener(new View.OnClickListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileFashionDetail.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Seasons");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(newName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newPrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newSize.getWindowToken(), 0);

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

                        newSeason.setText("");
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
                        newSeason.setText(newSeason.getText() + commaSepValueBuilder.toString());

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



        newCategory.setFocusable(false);
        newCategory.setOnClickListener(new View.OnClickListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileFashionDetail.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Category");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(newName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newPrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newSize.getWindowToken(), 0);

                builder.setSingleChoiceItems(category, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ImageTag.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        itemCategory = category[i].toString();
                        newCategory.setText(itemCategory);


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

        newSleeve.setFocusable(false);
        newSleeve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] category = new String[]{
                        "Long",
                        "Short"
                };

                // Boolean array for initial selected items

                final List<String> categoryList = Arrays.asList(category);

                //ImageTag.setRawInputType(Configuration.KEYBOARDHIDDEN_YES);
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileFashionDetail.this);

                builder.setIcon(R.drawable.icon);
                // Set a title for alert dialog
                builder.setTitle("Category");

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(newName.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newPrice.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newBrand.getWindowToken(), 0);
                im.hideSoftInputFromWindow(newSize.getWindowToken(), 0);

                builder.setSingleChoiceItems(category, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ImageTag.setText("Sort By : " +listitems[i]);
                        dialogInterface.dismiss();
                        itemSleeve = category[i].toString();
                        newSleeve.setText(itemSleeve);


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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        final DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(firebaseAuth.getUid()).child("userGallery").child(imgId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageUploadAttributes updateAttr = dataSnapshot.getValue(ImageUploadAttributes.class);
                newName.setText(updateAttr.getname());
                newPrice.setText(updateAttr.getprice());
                newTag.setText(updateAttr.gettag());

                newCategory.setText(updateAttr.getcategory());
                newSeason.setText(updateAttr.getseason());
                newBrand.setText(updateAttr.getbrand());
                newSize.setText(updateAttr.getsize());
                newSleeve.setText(updateAttr.getsleeve());
                sameDate = updateAttr.getdate().toString();
                sameLink = updateAttr.getImageURL().toString();
                sameId = updateAttr.getid().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateProfileFashionDetail.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUpdate = newName.getText().toString();
                String priceUpdate = newPrice.getText().toString();
                String tagUpdate = newTag.getText().toString();


                String sizeUpdate = newSize.getText().toString();
                String brandUpdate = newBrand.getText().toString();
                String categoryUpdate = newCategory.getText().toString();
                String seasonUpdate = newSeason.getText().toString();
                String sleeveUpdate = newSleeve.getText().toString();

                validate();

                ImageUploadAttributes userProfile = new ImageUploadAttributes(sameId, nameUpdate, sameLink, sameDate, tagUpdate, priceUpdate,
                        brandUpdate,seasonUpdate,categoryUpdate,sizeUpdate, sleeveUpdate);

                databaseReference.setValue(userProfile);

                finish();
            }
        });


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
                imgUploadNew.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                //ChooseButton.setText((CharSequence) FilePathUri.toString());
                //ChooseButton.setText("Change Image");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    private Boolean validate(){
        Boolean result = false;

        String CheckName = newName.getText().toString();
        String CheckPrice = newPrice.getText().toString();
        String CheckTag = newTag.getText().toString();
        String CheckSeason = newSeason.getText().toString();
        String CheckCategory = newCategory.getText().toString();
        String CheckSize = newSize.getText().toString();
        String CheckBrand = newBrand.getText().toString();


        /*String passwordHandler = password.getText().toString();
        if (password.isEmpty() || password.length() < 6) {  passwordText.setError("Password cannot be less than 6 characters!");
        }
        else {
            passwordText.setError(null);
            startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
        }*/
        if(TextUtils.isEmpty(CheckName)){
            newName.setError("The item cannot be empty");
        }
        if(TextUtils.isEmpty(CheckPrice)){
            newPrice.setError("The item cannot be empty");
        }
        if(TextUtils.isEmpty(CheckSize)) {
            newSize.setError("The item cannot be empty");
        }
        if(TextUtils.isEmpty(CheckBrand)){
            newBrand.setError("The item cannot be empty");
        }
        else if(TextUtils.isEmpty(CheckTag)){
            newTag.setError("The item cannot be empty");
        }
        else if(TextUtils.isEmpty(CheckSeason)){
            newSeason.setError("The item cannot be empty");
        }
        else if(TextUtils.isEmpty(CheckCategory)){
            newCategory.setError("The item cannot be empty");
        }






//isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || imagePath != null



        else{
            result = true;
        }

        return result;
    }



    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}