package sleepometerbyamitmaity.example.sleepometer.fragments;

import android.content.Context;
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

import sleepometerbyamitmaity.example.sleepometer.ProfileActivity;
import sleepometerbyamitmaity.example.sleepometer.R;
import sleepometerbyamitmaity.example.sleepometer.databinding.FragmentSettingsBinding;
import sleepometerbyamitmaity.example.sleepometer.modelClasses.Users;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    String userID;
    DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        binding = FragmentSettingsBinding.bind(view);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users");


        dataRetriveFromFirebase();

        binding.githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/Sleepometer-Android-App"));
                startActivity(browserIntent);
            }
        });

        binding.feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=maityamit308@gmail.com");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "maityamit308@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp(getContext());
            }
        });

        binding.privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs(getContext());
            }
        });


        binding.rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs(getContext());
            }
        });

        binding.github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/maityamit"));
                startActivity(browserIntent);
            }
        });

        binding.linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/maityamit"));
                startActivity(browserIntent);
            }
        });

        binding.settingsUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void rateUs(Context context) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
        startActivity(rateIntent);
    }

    private static void shareApp(Context context)
    {
        final String appPackageName = context.getPackageName();
        String send = "Hi , I would like to invite you to install this app called Sleepometer \n" + "https://play.google.com/store/apps/details?id=" + appPackageName;
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.banner);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        share.putExtra(Intent.EXTRA_TEXT, send);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "Invite", null);
        Uri imageUri = Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        context.startActivity(share);
    }

    private void dataRetriveFromFirebase() {

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);

                if (userprofile != null) {
                    String fullname = userprofile.name;

                    if (fullname.contains(" ")) {
                        fullname = fullname.substring(0, fullname.indexOf(" "));
                    }

                    Object pfpUrl = snapshot.child("user_image").getValue();
                    if (pfpUrl != null) {
                        // If the url is not null, then adding the image
                        Picasso.get().load(pfpUrl.toString()).placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .into(binding.settingsUserProfileImage);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}