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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private boolean connected = false;

    private FirebaseAuth mAuth;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        setContentView(R.layout.activity_splash_screen);

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