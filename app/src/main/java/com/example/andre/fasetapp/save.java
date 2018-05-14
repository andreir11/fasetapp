package com.example.andre.fasetapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class save extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference cDatabase,mDatabase;
    private DrawerLayout cDrawLayout;
    private ActionBarDrawerToggle cTogger;
    String name="No Product";
    String userID;
    String ID;
    Uri ScanImageUri;
    TextView txt;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String nameCloth, dateCloth,brand,sleeve, category, season ,date, size, tag, price, pitures;
    String Storage_Path = "All_Image_Uploads/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        txt = (TextView)findViewById(R.id.textView2);
        Button btnGoto = (Button) findViewById(R.id.buttonUpload);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        initialActionbar();
        Intent it=getIntent();
        ID = it.getStringExtra("ID");
        userID=it.getStringExtra("userid");
        getNamePictures();

        btnGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(save.this, DisplayImagesGalleryActivity.class);
                finish();
                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP|i.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });


    }
    public void getNamePictures()
    {
        cDatabase= FirebaseDatabase.getInstance().getReference().child("Cloth");
        cDatabase.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {

                    String id = ds.child("id").getValue().toString();
                    if(ID.equals(id)) {
                        Calendar mCal = Calendar.getInstance();
                        CharSequence s = DateFormat.format("yyyy-MM-dd ", mCal.getTime());

                        TextView txv = (TextView) findViewById(R.id.textView);
                        ImageView post_image = (ImageView) findViewById(R.id.imageView);
                         name = ds.child("name").getValue().toString();
                        tag  = ds.child("tag").getValue().toString();
                        pitures = ds.child("imageURL").getValue().toString();
                        price=ds.child("price").getValue().toString();
                        category=ds.child("category").getValue().toString();
                       size=ds.child("size").getValue().toString();
                       season=ds.child("season").getValue().toString();
                        sleeve=ds.child("sleeve").getValue().toString();
                         brand=ds.child("brand").getValue().toString();
                        dateCloth =s.toString();

                        txv.setText(name);
                        Glide.with(getApplicationContext()).load(pitures).into(post_image);

                        Bitmap bitmap =getBitmapFromView(post_image);
                        BitmapHelper.getInstance().setBitmap(bitmap);
                        try {
                            File file = new File(save.this.getExternalCacheDir(),"logicchip.png");
                            FileOutputStream fOut = new FileOutputStream(file);

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            file.setReadable(true, false);
                            //Intent intent = new Intent(this, save.class);
                            //intent.putExtra("Bitmap", bitmap);

                            MimeTypeMap map = MimeTypeMap.getSingleton();
                            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                            String type = map.getMimeTypeFromExtension(ext);

                            if (type == null)
                                type = "*/*";

                            //Uri data = Uri.fromFile(file);
                            //intent.setDataAndType(Uri.fromFile(file), type);
                            BitmapHelper.getInstance().getBitmap();
                            //intent.setType("image/jpeg");
                            //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            ScanImageUri = Uri.fromFile(file);


                            //tartActivity(intent);
            /*
            i.setAction(android.content.Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "image/jpg");
            startActivity(i);*/
            /*final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share image via"));*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //txt.setText(ScanImageUri.toString());



                        uploadImage();
                        //saveCloth(id,name,tag,pitures,price,date,category,size,season,sleeve,brand);
                        
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void uploadImage() {


        if(ScanImageUri != null)
        {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final String ImageUploadId = databaseReference.push().getKey();
            StorageReference ref = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(Storage_Path + System.currentTimeMillis()  + ".jpg" );
            //StorageReference storageReference2nd = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(Storage_Path).child(ImageUploadId).child(ImageName.getText().toString()+ "." + GetFileExtension(FilePathUri));
            ref.putFile(ScanImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();



                            // Showing toast message after done uploading.
                            //Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            //ImageUploadInfo imageUploadInfo = new ImageUploadInfo(ImageUploadId, name,taskSnapshot.getDownloadUrl().toString(),date,category);
                                    ImageUploadAttributes imageUploadInfo = new ImageUploadAttributes(ImageUploadId.toString(),name, taskSnapshot.getDownloadUrl().toString(),
                                    dateCloth,tag,price,brand,season,category, size, sleeve);
                            // Getting image upload ID.


                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userGallery").child(ImageUploadId).setValue(imageUploadInfo);




                            Toast.makeText(save.this, "New Cloth Has Been added", Toast.LENGTH_SHORT).show();
                            Toast.makeText(save.this, "Press Back to leave this window", Toast.LENGTH_LONG).show();

                            //Toast.makeText(EmptyActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(save.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

            Toast.makeText(save.this, "Not uploaded", Toast.LENGTH_LONG).show();

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
    
    
    public void saveCloth(String id,String name,String tag,String pictures,String price,String date,String category,String size,String season,String sleeve,String brand)
    {

        mDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("userGallery");
        mDatabase.child(id).child("id").setValue(id);
        mDatabase.child(id).child("category").setValue(category);
        mDatabase.child(id).child("name").setValue(name);
        mDatabase.child(id).child("date").setValue(date);
        mDatabase.child(id).child("tag").setValue(tag);
        mDatabase.child(id).child("price").setValue(price);
        mDatabase.child(id).child("imageURL").setValue(pictures);
        mDatabase.child(id).child("size").setValue(size);
        mDatabase.child(id).child("sleeve").setValue(sleeve);
        mDatabase.child(id).child("season").setValue(season);
        mDatabase.child(id).child("brand").setValue(brand);
    }

    public void initialActionbar()
    {
        cDrawLayout=(DrawerLayout) findViewById(R.id.drawer2);
        cTogger =new ActionBarDrawerToggle(this,cDrawLayout,R.string.open,R.string.close);
        cDrawLayout.addDrawerListener(cTogger);
        cTogger.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(cTogger.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            // Handle the camera action
            Intent it=new Intent(this,SecondActivity.class);
            startActivity(it);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        finish();
    }
}

