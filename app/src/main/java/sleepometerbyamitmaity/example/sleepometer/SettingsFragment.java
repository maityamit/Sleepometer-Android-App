package sleepometerbyamitmaity.example.sleepometer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import sharelayoutbyamit.example.sharelibrary.ShareLayout;

public class SettingsFragment extends Fragment {

    TextView user_name;
    TextView rateus,share,feedback,privacy;
    ImageView github,linkedin,project;

    String userID;
    DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");

        user_name = view.findViewById(R.id.settings_name);
        rateus = view.findViewById(R.id.rateus);
        share = view.findViewById(R.id.share);
        feedback = view.findViewById(R.id.feedback);
        privacy = view.findViewById(R.id.privacy);
        github = view.findViewById(R.id.github);
        linkedin = view.findViewById(R.id.linkedin);
        project = view.findViewById(R.id.github_button);


        dataRetriveFromFirebase();




        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/Sleepometer-Android-App"));
                startActivity(browserIntent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=maityamit308@gmail.com");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","maityamit308@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                startActivityForResult(Intent.createChooser(emailIntent, "Send email..."),0);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                        String send = "Hi , I would like to invite you to install this app called Sleepometer \n" + "https://play.google.com/store/apps/details?id=sleepometerbyamitmaity.example.sleepometer";
                        Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.banner);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        share.putExtra(Intent.EXTRA_TEXT,send);
                        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b, "Invite", null);
                        Uri imageUri =  Uri.parse(path);
                        share.putExtra(Intent.EXTRA_STREAM, imageUri);



            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/Sleepometer-Android-App"));
                startActivity(browserIntent);
            }
        });


        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/Sleepometer-Android-App"));
                startActivity(browserIntent);
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/maityamit"));
                startActivity(browserIntent);
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/maityamit"));
                startActivity(browserIntent);
            }
        });



        return view;
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

                    user_name.setText(fullname+" !");


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}