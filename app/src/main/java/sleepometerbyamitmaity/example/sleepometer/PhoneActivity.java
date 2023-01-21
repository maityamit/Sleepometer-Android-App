package sleepometerbyamitmaity.example.sleepometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import sleepometerbyamitmaity.example.sleepometer.databinding.ActivityPhoneBinding;

public class PhoneActivity extends AppCompatActivity {

    ActivityPhoneBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.username.getText().toString().isEmpty()){
                    binding.username.setError("Enter User Name");
                }else if(binding.phoneNumber.getText().toString().isEmpty()){
                    binding.phoneNumber.setError("Enter Phone Number");
                }else{
                    Intent intent = new Intent(PhoneActivity.this,OTPVerificationActivity.class);
                    intent.putExtra("username",binding.username.getText().toString());
                    intent.putExtra("phone",binding.phoneNumber.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}