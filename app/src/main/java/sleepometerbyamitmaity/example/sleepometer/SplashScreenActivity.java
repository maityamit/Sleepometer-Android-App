package sleepometerbyamitmaity.example.sleepometer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sleepometerbyamitmaity.example.sleepometer.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashScreenBinding binding;
    private boolean connected = false;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private int Splash_screen=3000;
    public  Animation top_welcome_anim,bottom_welcome_anim;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setting animation to imageView and Textview
        top_welcome_anim= AnimationUtils.loadAnimation(this,R.anim.welcome_top_animation);
        bottom_welcome_anim= AnimationUtils.loadAnimation(this,R.anim.welcome_bottom_animation);

        binding.splashIcon.setAnimation(top_welcome_anim);
        binding.splashAppName.setAnimation(bottom_welcome_anim);
        binding.splashSubText.setAnimation(bottom_welcome_anim);


        mAuth = FirebaseAuth.getInstance ();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            FirebaseUser currentUser = mAuth.getCurrentUser ();

            if (currentUser == null) {

                SendUserToLoginActivity();
            } else {

                currentUserID = mAuth.getCurrentUser ().getUid ();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent loginIntent = new Intent (SplashScreenActivity.this, MainActivity.class  );
                        loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( loginIntent );
                        finish ();
                    }
                }, 2900);
            }
        } else{
            connected=false;
            new AlertDialog.Builder(this)
                    .setTitle("You are Offline Dude ! ")
                    .setMessage("Make sure that you are in online Mode.")
                    .setNegativeButton("Ok",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }


    private void SendUserToLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent (SplashScreenActivity.this, LoginActivity.class  );
                loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( loginIntent );
                finish ();
            }
        }, 2900);
    }
}