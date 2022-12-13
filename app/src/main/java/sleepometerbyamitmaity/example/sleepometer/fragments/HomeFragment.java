package sleepometerbyamitmaity.example.sleepometer.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import sleepometerbyamitmaity.example.sleepometer.ProfileActivity;
import sleepometerbyamitmaity.example.sleepometer.R;
import sleepometerbyamitmaity.example.sleepometer.databinding.FragmentHomeBinding;
import sleepometerbyamitmaity.example.sleepometer.modelClasses.Users;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    Thread thread;
    String userID;
    SharedPreferences sharedPreferences;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    DatabaseReference Rootref;
    volatile boolean countSec = true;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        binding = FragmentHomeBinding.bind(view);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Rootref = FirebaseDatabase.getInstance ().getReference ().child("Users").child(userID);
        sharedPreferences = getContext().getSharedPreferences("MySharedPref",getContext().MODE_PRIVATE);

        setSleepTime();
        setWakeTime();



        binding.addSleepExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExtraSleepAddFunction();
            }
        });
        binding.sleepScoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users");

        dataRetriveFromFirebase();


        binding.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.alarmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openClockIntent);
            }
        });

        getOperationLocally();




        binding.startButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("name","").equals("")){
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    myEdit.putString("name", "active");
                    myEdit.putString("age", timeStamp);
                    myEdit.putString("sleepAt", timeStamp);
                    myEdit.putString("wakeUp", "");

                    myEdit.commit();

                    setSleepTime();
                    countSec = true;
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
                    myEdit.putString("wakeUp", timeStamp);

                    myEdit.commit();
                    setWakeTime();
                    countSec = false;
                    getOperationLocally();
                }
            }
        });


        return view;
    }

    private void setWakeTime() {
        try{
            String wake = sharedPreferences.getString("wakeUp","");
            Timestamp timecurr = stringToTimestamp(wake);

            long hour = Objects.requireNonNull(timecurr).getHours();
            long min = timecurr.getMinutes();
            if(hour>=12) binding.wakeUpTimeHintAmOrpm.setText("pm");
            else binding.wakeUpTimeHintAmOrpm.setText("am");
            hour = hour%12;
            if(hour>=10 && min>=10) binding.wakeUpTimeHintTime.setText(hour+":"+min+" ");
            else if(hour<10 && min>=10) binding.wakeUpTimeHintTime.setText("0"+hour+":"+min+" ");
            else if(hour>=10 && min<10) binding.wakeUpTimeHintTime.setText(hour+":"+"0"+min+" ");
            else binding.wakeUpTimeHintTime.setText("0"+hour+":"+"0"+min+" ");
        }catch (Exception e){
            binding.wakeUpTimeHintTime.setText("--:--");
            binding.wakeUpTimeHintAmOrpm.setText("");
        }
    }

    private void setSleepTime() {
        try {
            String sleep = sharedPreferences.getString("sleepAt", "");
            Timestamp sleepTimeStamp = stringToTimestamp(sleep);
            long hour = Objects.requireNonNull(sleepTimeStamp).getHours();
            long min = sleepTimeStamp.getMinutes();
            if (hour >= 12) binding.SleepAtTimeAmOrpm.setText("pm");
            else binding.SleepAtTimeAmOrpm.setText("am");
            hour = hour % 12;
            if (hour >= 10 && min >= 10) binding.SleepAtTimeTime.setText(hour + ":" + min + " ");
            else if (hour < 10 && min >= 10)
                binding.SleepAtTimeTime.setText("0" + hour + ":" + min + " ");
            else if (hour >= 10 && min < 10)
                binding.SleepAtTimeTime.setText(hour + ":" + "0" + min + " ");
            else binding.SleepAtTimeTime.setText("0" + hour + ":" + "0" + min + " ");
            binding.wakeUpTimeHintTime.setText("--:--");
            binding.wakeUpTimeHintAmOrpm.setText("");
        }catch (Exception e){
            binding.SleepAtTimeTime.setText("--:--");
            binding.SleepAtTimeAmOrpm.setText("");
        }
    }

    private void ExtraSleepAddFunction() {


        new LovelyTextInputDialog(getContext(), R.style.EditTextTintTheme)
                .setTopColorRes(R.color.purple_200)
                .setTitle("Missed to Click Button ?")
                .setMessage("How many minutes you sleep ?")
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setIcon(R.drawable.ic_baseline_edit_24)
                .setInputFilter("Wrong Input, please try again!", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return text.matches("\\w+");
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        String myText = text; //Saving Entered name in String
                        int Days = Integer.parseInt(myText);
                        if(myText.isEmpty())
                            Toast.makeText(getContext(), "Please input minitus", Toast.LENGTH_SHORT).show();
                        else {
                            long longg = Long.parseLong(myText) ;
                            longg = longg * 60 *1000;
                            String today_datee = new SimpleDateFormat("dd-M-yyyy").format(new Date());
                            upDateWinNode(longg,today_datee);
                            Toast.makeText(getContext(), "Done. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();



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
            binding.activeSleepLayout.setVisibility(View.GONE);
            binding.startButton.setText("Start Sleep");
        }else{
            timeCountSetText();
            binding.activeSleepLayout.setVisibility(View.VISIBLE);
            binding.startButton.setText("End Sleep");

        }

    }

    private void timeCountSetText() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (countSec){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!countSec){
                                return;
                            }
                            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                            Timestamp date_1 = stringToTimestamp(sharedPreferences.getString("age",""));
                            Timestamp date_2 = stringToTimestamp(timeStamp);
                            long milliseconds = date_2.getTime() - date_1.getTime();
                            long hour = (milliseconds/1000)/3600;
                            long min = ((milliseconds/1000)/60)%60;
                            long sec = (milliseconds/1000)%60;

                            if(hour<24) binding.timeCount.setText(hour+" hr  "+ min+" min "+ sec+" sec");
                            else binding.timeCount.setText("24 hr+");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();


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

                    binding.mainActUserName.setText(fullname+" !");


                }

                // Getting the url of profile picture
                Object pfpUrl = snapshot.child("user_image").getValue();
                if (pfpUrl != null) {
                    // If the url is not null, then adding the image
                    Picasso.get().load(pfpUrl.toString()).placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(binding.userProfileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Title")
                .setMessage("Body")
                .setNegativeButton("Ok",null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


    //Avg Function
    private void avg_sleep(){
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fetch = snapshot.child("7days").getValue().toString();
                String[] sp = fetch.split(";");


                float sum = 0;
                for(int i=0;i<7;i++){
                    sum+= Float.parseFloat(sp[i]);
                }

                //this is the percentage

                sum = (float) sum/7;
                String avg="";
                String reaction="";

                if (0<= sum && sum <=20){
                    reaction="😴";
                }else if (20 < sum && sum <= 40){
                    reaction="😪";
                }else if (40 < sum && sum <= 60){
                    reaction="😐";
                }else if (60 < sum && sum <= 80){
                    reaction="😊";
                }else if (80< sum && sum <= 100){
                    reaction="😁";
                }
                binding.sleepScoreDataEmoji.setText(reaction);

                float hello = (sum*24)/100;
                int Hours = (int) hello;
                int temp_mnt = (int) ((hello - Math.floor( hello))*100) ;
                int Minutes = (temp_mnt*60)/100;

                String avgsleep = "Avg Sleep: "+Hours+" hrs "+Minutes+" mnts.";


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        countSec = false;
    }
}