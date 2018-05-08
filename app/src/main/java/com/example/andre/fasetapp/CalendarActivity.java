package com.example.andre.fasetapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarActivity extends AppCompatActivity{
    FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    ImageView imageD;
    Button buttonToSelectC;
    Button buttonToSelectD;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @BindView(R.id.textView)
    TextView textView4;
    private static final String TAG = "CalendarActivity";

    //private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;
    String aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        textView4 = (TextView) findViewById(R.id.textView4);
        imageD = (ImageView) findViewById(R.id.imageDaily);
        buttonToSelectC = (Button) findViewById(R.id.buttonToSelectCollection);
        buttonToSelectD = (Button) findViewById(R.id.buttonToDelete);
        //databaseReference = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
/*
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        //Setup initial text
        textView.setText(getSelectedDatesString());

*/
      /*  BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_arrow:
                        Intent intent1 = new Intent(CalendarActivity.this, SecondActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.ic_android:

                        break;

                    case R.id.ic_books:
                        Intent intent2 = new Intent(CalendarActivity.this, GalleryActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.ic_center_focus:
                        Intent intent3 = new Intent(CalendarActivity.this, DisplayImagesActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.ic_backup:
                        Intent intent4 = new Intent(CalendarActivity.this, ProfileActivity.class);
                        startActivity(intent4);
                        break;


                }


                return false;
            }
        });

*/

        buttonToSelectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CalendarActivity.this, PickFashion1.class);
                i.putExtra("CatchDate",aa);
                //i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //onStop();
                //finish();
                startActivity(i);

            }
        });

        buttonToSelectD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(CalendarActivity.this, PickFashion1.class);
                i.putExtra("CatchDate",aa);
                finish();
                startActivity(i);*/
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                 final DatabaseReference myReff = firebaseDatabase.getReference("users");




            }
        });

        buttonToSelectC.setVisibility(View.INVISIBLE);
        buttonToSelectD.setVisibility(View.INVISIBLE);

        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(CalendarDay.from(2000,1,1))
                .setMaximumDate(CalendarDay.from(2025,1,1))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();


        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, CalendarDay date, boolean selected) {

            buttonToSelectC.setVisibility(View.VISIBLE);
            //buttonToSelectD.setVisibility(View.VISIBLE);

            aa = FORMATTER.format(date.getDate());
            String ab = aa.toString();


            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference("users");
            textView4.setText(aa.toString());
            //myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userInfo").child("userName").setValue("test");

            //Query query = myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userGallery").orderByChild("imageName").equalTo("upup");
            Query query = myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userDailyWear").orderByChild("imageDateD").equalTo(ab);


            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    imageD.setImageResource(0);
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        if (singleSnapshot != null) {
                            //String Title = (String) singleSnapshot.child("userName").getValue();
                            String Title = (String) singleSnapshot.child("imageURL").getValue();
                            Glide.with(CalendarActivity.this).load(Title).override(300, 200).into(imageD);
                            //textView4.setText(Title);
                        }
                    }
                    }


                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            });





            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, new FragmentOne()).commit();
            Toast.makeText(CalendarActivity.this,"" + aa,Toast.LENGTH_SHORT).show();
        }
    });

    }

/*
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        textView.setText(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }*/

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        finish();
    }
}
