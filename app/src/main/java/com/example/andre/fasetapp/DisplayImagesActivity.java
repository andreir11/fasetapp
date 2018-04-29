package com.example.andre.fasetapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesActivity extends AppCompatActivity {

    private ImageView imgchooser;

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    FirebaseAuth firebaseAuth;
    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);
        imgchooser = (ImageView) findViewById(R.id.imageView5) ;
        firebaseAuth = FirebaseAuth.getInstance();
        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        //recyclerView.setLayoutManager(new LinearLayoutManager(DisplayImagesActivity.this));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DisplayImagesActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();


        //recyclerView.setAdapter(new G);

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("userGallery");

        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                    list.add(imageUploadInfo);
                }

                adapter = new RecyclerAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
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

    static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ItemClickListener itemClickListener;
        public TextView txt_description;
        public ImageView imageView;
        public ImageView imgView;
        public TextView imageNameTextView;
        public ImageView imgChoose;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            txt_description = (TextView)itemView.findViewById(R.id.txtDescription);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            //imgView = (ImageView) itemView.findViewById(R.id.imageView2);
            imgChoose = (ImageView)itemView.findViewById(R.id.imageView5);
            imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {

            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{

        Context context;
        List<ImageUploadInfo> MainImageUploadInfoList;
        private ItemClickListener itemClickListener;



        public RecyclerAdapter(Context context, List<ImageUploadInfo> TempList) {

            this.MainImageUploadInfoList = TempList;
            this.context = context;
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.recyclerview_items, parent, false);
            return new RecyclerViewHolder(itemView);

            //Yang lama
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);

        return viewHolder;*/


        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            final ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);
            holder.imageNameTextView.setText(UploadInfo.getImageName());
            //Loading image from Glide library.
            Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imageView);
            holder.txt_description.setText("hello");
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    //imageView2.set



                    if(isLongClick)
                        Toast.makeText(context, "Long Click: "+MainImageUploadInfoList.get(position), Toast.LENGTH_SHORT).show();
                        //Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imgChoose);
                    else
                        Toast.makeText(context, ""+UploadInfo.getImageURL(), Toast.LENGTH_SHORT).show();

                }
            });


        }

        @Override
        public int getItemCount() {

            return MainImageUploadInfoList.size();
        }



    }



}