package com.example.andre.fasetapp;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class PickFashion extends AppCompatActivity implements RecyclerViewAdapterr.OnItemClickListener {

    //private List<Retrieve> mRetrieve = new ArrayList<>();
    DatabaseReference databaseReference;
    public ImageView imgChoose;
    public ImageView imgChoose1;
    public ImageView imgChoose2;
    public ImageView imgChoose3;
    public ImageView imgChoose4;
    public Button OnClickShare;
    RelativeLayout idForSaveView;
    // Creating RecyclerView.
    RecyclerView recyclerView;
    public TextView textDisplay;
    // Creating RecyclerView.Adapter.
    RecyclerViewAdapterr adapter;
    FirebaseAuth firebaseAuth;
    // Creating Progress dialog
    ProgressDialog progressDialog;
    public String position;
    // Creating List of ImageUploadInfo class.
    List<ImageUploadAttributes> list = new ArrayList<>();
    ArrayAdapter<String> adapterarray;
    EditText editText;
    ArrayList<String> itemList = new ArrayList<String>();
    List<String> words = new ArrayList<>();
    //variables sharedpref

    //private MySharedPreference sharedPreference;
    private HashSet<String> scoreset;
    private Gson gson;

    public static final String PREFS = "examplePrefs";



    private FirebaseStorage mStorage;
    ImageView img;


    private ValueEventListener mDBListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_fashion);
        textDisplay = (TextView) findViewById(R.id.textView1);
        //imgChoose = (ImageView)findViewById(R.id.imageView9);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imgChoose = (ImageView) findViewById(R.id.imageView12);
        imgChoose1 = (ImageView) findViewById(R.id.imageView5);
        imgChoose2 = (ImageView) findViewById(R.id.imageView8);
        imgChoose3 = (ImageView) findViewById(R.id.imageView10);
        imgChoose4 = (ImageView) findViewById(R.id.imageView11);
        //OnClickShare = (Button) findViewById(R.id.onClickShare);
        idForSaveView = (RelativeLayout) findViewById(R.id.idForSaveView);
/*
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < 5; i++) {
            ImageView image = new ImageView(PickFashion.this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(500, 480));

            image.set

            MaxHeight(200);
            image.setMaxWidth(180);
            int width = 50;
            image.setX(60 + width);
            //image.setImageResource(R.drawable.sample_0);
            //textDisplay.setText(itemList.get(0));
            //textDisplay.setText(textDisplay.getText() + stringData.get(i) + " , ");
            Glide.with(getApplicationContext()).load(position).into(image);
            relativeLayout.addView(image);
        }*/


        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);
        // Setting RecyclerView layout as LinearLayout.
        //recyclerView.setLayoutManager(new LinearLayoutManager(DisplayImagesActivity.this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(PickFashion.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images...");

        // Showing progress dialog.
        progressDialog.show();
        //recyclerView.setOnClickListener(this);
        // imgChoose = (ImageView) findViewById(R.id.imageView5);

        /*Glide.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/image%2Fwhite_1.jpg?alt=media&token=c6cd5a49-7e5f-4352-9f02-5a7a5fcb4ccc")
                .override(300, 200)
                .into(imgChoose);*/
        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        //adapter = new RecyclerAdapter(getApplicationContext(), list);
        adapter = new RecyclerViewAdapterr(PickFashion.this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(PickFashion.this);

        mStorage = FirebaseStorage.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("userGallery");
        // Adding Add Value Event Listener to databaseReference.
        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadAttributes imageUploadInfo = postSnapshot.getValue(ImageUploadAttributes.class);

                    list.add(imageUploadInfo);
                }
                /*
                adapter = new RecyclerAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);*/
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

    public void OnClickShare(View view){
        //File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Logicchip");
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), idForSaveView);
        Bitmap bitmap =getBitmapFromView(idForSaveView);
        BitmapHelper.getInstance().setBitmap(bitmap);
        try {
            File file = new File(this.getExternalCacheDir(),"logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            Intent intent = new Intent(this, EmptyActivity1.class);
            //intent.putExtra("Bitmap", bitmap);

            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);

            if (type == null)
                type = "*/*";

            //Uri data = Uri.fromFile(file);

            intent.setDataAndType(Uri.fromFile(file), type);
            BitmapHelper.getInstance().getBitmap();
            //intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            startActivity(intent);
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
    public void onItemClick(int position) {
        ImageUploadAttributes selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());
       String categoryHolder = selectedItem.getcategory();

       if(categoryHolder.equalsIgnoreCase("Top")){
           Glide.with(this)
                   .load(selectedItem.getImageURL())
                   .into(imgChoose1);
           Toast.makeText(this, "You had put " +selectedItem.getname()+ " On " + categoryHolder, Toast.LENGTH_SHORT).show();
       }

        if(categoryHolder.equalsIgnoreCase("Bottom")){
            Glide.with(this)
                    .load(selectedItem.getImageURL())
                    .into(imgChoose);
            Toast.makeText(this, "You had put " +selectedItem.getname()+ " On " + categoryHolder, Toast.LENGTH_SHORT).show();
        }

        if(categoryHolder.equalsIgnoreCase("Jacket")){
            Glide.with(this)
                    .load(selectedItem.getImageURL())
                    .into(imgChoose2);
            Toast.makeText(this, "You had put " +selectedItem.getname()+ " On " + categoryHolder, Toast.LENGTH_SHORT).show();
        }
        if(categoryHolder.equalsIgnoreCase("Shoes")){
            Glide.with(this)
                    .load(selectedItem.getImageURL())
                    .into(imgChoose3);
            Toast.makeText(this, "You had put " +selectedItem.getname()+ " On " + categoryHolder, Toast.LENGTH_SHORT).show();
        }
        if(categoryHolder.equalsIgnoreCase("Accesories")){
            Glide.with(this)
                    .load(selectedItem.getImageURL())
                    .into(imgChoose4);
            Toast.makeText(this, "You had put " +selectedItem.getname()+ " On " + categoryHolder, Toast.LENGTH_SHORT).show();
        }


        //Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        ImageUploadAttributes selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());

        Glide.with(this)
                .load(selectedItem.getImageURL())
                .into(imgChoose1);
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
        });
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/fasetapp-e5b56.appspot.com/o/4j4AfAqmPcPzVwOshvHwouhLvVw1%2FAll_Image_Uploads%2Fsample_5.jpg?alt=media&token=3b9dc544-53d1-47fb-b995-497693e644b0")
                .into(img1);*/
        ImageUploadAttributes selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());

        Glide.with(this)
                .load(selectedItem.getImageURL())
                .into(imgChoose);
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();



    }

    public void onHats(int position) {
        ImageUploadAttributes selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());

        Glide.with(this)
                .load(selectedItem.getImageURL())
                .into(imgChoose2);
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    public void onShoes(int position) {
        ImageUploadAttributes selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());

        Glide.with(this)
                .load(selectedItem.getImageURL())
                .into(imgChoose3);
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    public void onAccesories(int position) {
        ImageUploadAttributes selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageURL());
        //textDisplay.setText(selectedItem.getImageURL());

        Glide.with(this)
                .load(selectedItem.getImageURL())
                .into(imgChoose4);
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
    }



}


