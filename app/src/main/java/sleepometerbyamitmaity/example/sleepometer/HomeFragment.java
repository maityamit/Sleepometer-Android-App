package sleepometerbyamitmaity.example.sleepometer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {


    CircleImageView circleImageView;


    String userID;
    SharedPreferences sharedPreferences;
    DatabaseReference reference;
    Button button;
    LinearLayout linearLayout;
    TextView textView;
    TextView avg_text;
    PieChart pieChart;
    TextView time_count;
    ProgressDialog progressDialog;
    DatabaseReference Rootref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        
        
        showProgressDialog();
        avg_text = view.findViewById(R.id.avg_sleep_text);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        textView = view.findViewById(R.id.main_act_user_name);
        button = view.findViewById(R.id.start_button);
        linearLayout = view.findViewById(R.id.active_sleep_layout);

        time_count = view.findViewById(R.id.time_count);

        pieChart = (PieChart) view.findViewById(R.id.piechart);

        Rootref = FirebaseDatabase.getInstance ().getReference ().child("Users").child(userID);

        Graph();


        reference = FirebaseDatabase.getInstance().getReference("Users");


        circleImageView = view.findViewById(R.id.user_profile_image);
        dataRetriveFromFirebase();
        sharedPreferences = getContext().getSharedPreferences("MySharedPref",getContext().MODE_PRIVATE);


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });


        getOperationLocally();




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (sharedPreferences.getString("name","").equals("")){
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    myEdit.putString("name", "active");
                    myEdit.putString("age", timeStamp);

                    myEdit.commit();
                    getOperationLocally();
                }else {


                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    Timestamp date_1 = stringToTimestamp(sharedPreferences.getString("age",""));
                    Timestamp date_2 = stringToTimestamp(timeStamp);
                    long milliseconds = date_2.getTime() - date_1.getTime();




                    String today_date = new SimpleDateFormat("dd-M-yyyy").format(new Date());

                    upDateWinNode(milliseconds,today_date);


                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    myEdit.putString("name", null);
                    myEdit.putString("age", null);

                    myEdit.commit();
                    getOperationLocally();

                }







                getOperationLocally();

            }
        });


        return  view;

    }

    private void upDateWinNode(long milliseconds, String today_date) {


        Rootref.child("Win").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = (int) snapshot.getChildrenCount();

                
                if (!(snapshot.child(today_date).exists())){



                    HashMap userMap=new HashMap();
                    userMap.put("date",today_date);
                    userMap.put("sleep",String.valueOf(milliseconds));

                    Rootref.child("Win").child(today_date).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            updateTheAVGData(today_date,"new",milliseconds);

                        }
                    });

                }else{


                    String string = snapshot.child(today_date).child("sleep").getValue().toString();
                    double d = Double.parseDouble(string);
                    d = d+milliseconds;


                    HashMap userMap=new HashMap();
                    userMap.put("date",today_date);
                    userMap.put("sleep",String.valueOf(d));

                    double finalD = d;
                    Rootref.child("Win").child(today_date).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {


                            updateTheAVGData(today_date,"old", finalD);


                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void updateTheAVGData(String today_date, String old , double d) {

        double sec = (d/1000);
        sec = (float) sec/86400;
        sec = sec*100;


        double finalSec = Math.round(sec * 100.0) / 100.0;;


        if (old.equals("new")){
            Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String s = snapshot.child("7days").getValue().toString();
                    String[] fin  = new String[7];
                    String[] sp = s.split(";");

                    int i=1, j=0;
                    int x = 0, y=0;
                    while(i<7) {
                        fin[j] = sp[i];
                        i++;
                        j++;
                    }
                    while(y<x){
                        fin[j] = sp[i-1];
                        j++;
                        y++;
                    }
                    fin[fin.length-1] = String.valueOf(finalSec);


                    String up = "";
                    for(int iy=0; iy<6; iy++) {
                        up+=fin[iy]+";";
                    }
                    up+=fin[fin.length-1];


                    HashMap<String, Object> map = new HashMap<>();
                    map.put("7days", up);
                    Rootref.updateChildren ( map );

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String s = snapshot.child("7days").getValue().toString();
                    String[] sp = s.split(";");


                    String up = "";
                    for(int iy=0; iy<6; iy++) {
                        up+=sp[iy]+";";
                    }
                    up+= String.valueOf(finalSec);


                    HashMap<String, Object> map = new HashMap<>();
                    map.put("7days", up);
                    Rootref.updateChildren ( map );

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }








    }


    private void getOperationLocally() {


        if (sharedPreferences.getString("name","").equals("")){
            linearLayout.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            button.setText("Start Sleep");
        }else{

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            Timestamp date_1 = stringToTimestamp(sharedPreferences.getString("age",""));
            Timestamp date_2 = stringToTimestamp(timeStamp);
            long milliseconds = date_2.getTime() - date_1.getTime();


            time_count.setText(String.valueOf(milliseconds/1000/3600)+" hr  "+ milliseconds/1000/60+" mnt");


            linearLayout.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            button.setText("End Sleep");



        }

    }




    private Timestamp stringToTimestamp(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(date);
            return new Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    private void dataRetriveFromFirebase() {

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);

                if (userprofile != null) {
                    String fullname = userprofile.name;

                    if(fullname.contains(" ")){
                        fullname = fullname.substring(0, fullname.indexOf(" "));
                    }

                    textView.setText(fullname+" !");


                }

                // Getting the url of profile picture
                Object pfpUrl = snapshot.child("user_image").getValue();
                if (pfpUrl != null) {
                    // If the url is not null, then adding the image
                    Picasso.get().load(pfpUrl.toString()).placeholder(R.drawable.profile).error(R.drawable.profile).into(circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }





    public void Graph() {

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fetch = snapshot.child("7days").getValue().toString();
                String[] sp = fetch.split(";");


                float sum = 0;
                for(int i=0;i<7;i++){
                    sum+= Float.parseFloat(sp[i]);
                }

                sum = (float) sum/7;
                pieChart.addPieSlice(new PieModel("", sum, Color.parseColor("#0F9D58")));
                pieChart.addPieSlice(new PieModel("", (100-sum), Color.parseColor("#DB4437")));

                pieChart.startAnimation();

                progressDialog.dismiss();


                double hello = sum*86400*0.01;

                double Hours = hello / (60 * 60 * 1000) % 24;
                double Minutes = hello/ (60 * 1000) % 60;

                avg_text.setText("Avg Sleep: "+Math.round(Hours * 100.0) / 100.0+" hrs "+Math.round(Minutes * 100.0) / 100.0+" mnts.");







            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_diaglog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

    }



}