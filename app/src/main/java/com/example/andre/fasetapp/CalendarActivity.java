package com.example.andre.fasetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

public class CalendarActivity extends AppCompatActivity{
    private String url1 = "http://api.openweathermap.org/data/2.5/forecast?q=Chiayi&units=metric&appid=4b391b238bdfdf6a39509a9cd4bbf2cb";
    private HandJSON obj;
    FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    ImageView imageD,weatherimage;
    Button buttonToSelectC;
    Button buttonToSelectD;
    TextView weather,weathtxv;

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
        weathtxv=(TextView) findViewById(R.id.textView17);
        //weather
        weather=(TextView)findViewById(R.id.weath);
        weatherimage=(ImageView)findViewById(R.id.weatherImage);
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
        imageD.setVisibility(View.INVISIBLE);
        buttonToSelectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CalendarActivity.this, DisplayImagesDailyActivity.class);
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

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);


        Calendar calendar = Calendar.getInstance();
        materialCalendarView.setDateSelected(calendar.getTime(), true);

        materialCalendarView.getCurrentDate();



        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, CalendarDay date, boolean selected) {

                buttonToSelectC.setVisibility(View.VISIBLE);
                //buttonToSelectD.setVisibility(View.VISIBLE);
                imageD.setVisibility(View.VISIBLE);
                textView4.setVisibility(View.VISIBLE);
                aa = FORMATTER.format(date.getDate());
                String ab = aa.toString();

                //weather and Json
                obj = new HandJSON(url1, ab);
                obj.fetchJSON();
                weather=(TextView)findViewById(R.id.weath);
                while(obj.parsingComplete);
                {   weathtxv.setText("天氣:");
                    if(obj.getTemperature().equals("unknown")) {
                        weather.setText("今日平均溫度: 暫時不提供");
                        weatherimage.setImageResource(R.drawable.unknown);
                    }
                    else
                    {  weather.setText("今日平均溫度:"+obj.getTemperature()+" (°C)");
                        switch (obj.getWeather_type())
                        {
                            case "Rain":
                                weatherimage.setImageResource(R.drawable.rainy);
                                break;
                            case "Clear":
                                weatherimage.setImageResource(R.drawable.sunn);
                                break;
                            case "Clouds":
                                weatherimage.setImageResource(R.drawable.clounds);
                                break;
                        }
                    }
                }


                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference myRef = firebaseDatabase.getReference("users");
                textView4.setText(aa.toString());
                //myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userInfo").child("userName").setValue("test");

                //Query query = myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userGallery").orderByChild("imageName").equalTo("upup");
                Query query = myRef.child(firebaseAuth.getCurrentUser().getUid()).child("userDailyWear").orderByChild("imageDailyD").equalTo(ab);


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

                            else{
                                Toast.makeText(getApplicationContext(), "No record on " + aa, Toast.LENGTH_LONG).show();
                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Toast.makeText(getApplicationContext(), "No record on " + aa, Toast.LENGTH_LONG).show();
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
