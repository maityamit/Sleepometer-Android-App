package sleepometerbyamitmaity.example.sleepometer;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.Objects;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;


public class StatsFragment extends Fragment {


    DatabaseReference Rootref,HelloRef;
    String userID;
    ValueLineChart mCubicValueLineChart;
    ArrayList<DateData> dataArrayList;
    MCalendarView mCalendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_stats, container, false);

        mCalendarView = view.findViewById(R.id.history_calendarView);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mCubicValueLineChart = (ValueLineChart) view.findViewById(R.id.cubiclinechart);

        Rootref = FirebaseDatabase.getInstance ().getReference ().child("Users").child(userID);
        HelloRef = FirebaseDatabase.getInstance ().getReference ().child("Users").child(userID).child("Win");


        Graph();
        highLightDate();


        mCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {

                getAlertDialogBox(date.getDay(),date.getMonth(),date.getYear());
            }
        });


        return  view;

    }

    private void getAlertDialogBox(int day, int month, int year) {


        String dayu = String.valueOf(day);
        if(day<10){
            dayu = "0"+dayu;
        }

        String compare = dayu+"-"+month+"-"+year;
        HelloRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(compare).exists()){
                    String total = snapshot.child(compare).child("sleep").getValue().toString();
                    double diff = Double.parseDouble(total);

                    double Hours = diff / (60 * 60 * 1000) % 24;
                    double Minutes = diff / (60 * 1000) % 60;

                    String hello = Math.round(Hours * 100.0) / 100.0 + " hrs "+ Math.round(Minutes * 100.0) / 100.0 + " mnts.";



                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(),R.style.AlertDialogTheme);
                    builder.setTitle(compare);
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage(hello);
                    builder.setBackground(getResources().getDrawable(R.drawable.input_background , null));
                    builder.show();


                }else{
                    Toast.makeText(getContext(), "Not Exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void Graph() {

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fetch = snapshot.child("7days").getValue().toString();
                String[] sp = fetch.split(";");



                ValueLineSeries series = new ValueLineSeries();

                float x = Float.parseFloat(sp[6]);

                series.setColor(getResources().getColor(R.color.purple_700));

                series.addPoint(new ValueLinePoint("null", Float.parseFloat(sp[0])));
                series.addPoint(new ValueLinePoint("7th", Float.parseFloat(sp[0])));
                series.addPoint(new ValueLinePoint("6th", Float.parseFloat(sp[1])));
                series.addPoint(new ValueLinePoint("5th", Float.parseFloat(sp[2])));
                series.addPoint(new ValueLinePoint("4th", Float.parseFloat(sp[3])));
                series.addPoint(new ValueLinePoint("3rd", Float.parseFloat(sp[4])));
                series.addPoint(new ValueLinePoint("2nd", Float.parseFloat(sp[5])));
                series.addPoint(new ValueLinePoint("Today", Float.parseFloat(sp[6])));
                series.addPoint(new ValueLinePoint("null", Float.parseFloat(sp[6])));

                mCubicValueLineChart.addSeries(series);
                mCubicValueLineChart.startAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void   highLightDate(){

        //ArrayList<DateData> dataArrayList;
        HelloRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    dataArrayList = new ArrayList<DateData>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){

                        String strDate= snapshot1.getKey();
                        // Log.d("ParseException",strDate);
                        int day = Integer.parseInt(strDate.substring(0,strDate.indexOf("-")));
                        int month= Integer.parseInt(strDate.substring(3,strDate.lastIndexOf("-")));
                        int year= Integer.parseInt(strDate.substring(strDate.lastIndexOf("-")+1,9));
                        DateData date= new DateData(year,month,day);
                        dataArrayList.add(date);

                    }
                    // MCalendarView mCalendarView= findViewById(R.id.history_calendarView);
                    for(int i=0; i< dataArrayList.size();i++){

                        DateData date= dataArrayList.get(i);

                        mCalendarView.markDate(date.getYear(),
                                date.getMonth(),
                                date.getDay());

                        mCalendarView.setMarkedStyle(MarkStyle.BACKGROUND, Color.BLUE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}