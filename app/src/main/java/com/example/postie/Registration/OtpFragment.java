package com.example.postie.Registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.postie.MainActivity;
import com.example.postie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpFragment extends Fragment {

    private EditText otp;
    private ProgressBar progressBar;
    private Button verifyBtn,resendBtn;
    private TextView tvPhone;

    private final String email,phone,password;

    //private Timer timer;
    //private int count=60;

    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    private CountDownTimer otpTimer;

    public OtpFragment(String email,String phone,String password){
        this.email=email;
        this.password=password;
        this.phone=phone;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        mAuth = FirebaseAuth.getInstance();
        tvPhone.setText("Verification code has been sent to +91"+phone);
        sendOTP();
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                requireActivity().runOnUiThread(() -> {
//                    if(count==0){
//                        resendBtn.setText("Resend");
//                        resendBtn.setEnabled(true);
//                        resendBtn.setAlpha(1f);
//                    }else{
//                        resendBtn.setText("Resend in "+count--);
//                    }
//                });
//            }
//        },0,1000);

        otpTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                resendBtn.setText("Resend in "+(l/1000));
            }

            @Override
            public void onFinish() {
                resendBtn.setText("Resend");
                resendBtn.setEnabled(true);
                resendBtn.setAlpha(1f);
            }
        };

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTP();
                resendBtn.setEnabled(false);
                resendBtn.setAlpha(0.5f);
                //count=60;
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp.setError(null);
                if(otp.getText()==null || otp.getText().toString().isEmpty()){
                    otp.setError("Please enter OTP");
                    otp.requestFocus();
                    return;
                }
                String code = otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void init(View view){
        otp = view.findViewById(R.id.otpEditText);
        progressBar = view.findViewById(R.id.otpProgressBar);
        verifyBtn = view.findViewById(R.id.verifyBtn);
        resendBtn = view.findViewById(R.id.resendBtn);
        tvPhone = view.findViewById(R.id.verifyText);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(requireActivity(), "Account Successfully Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    otp.setError(e.getMessage());
                    otp.requestFocus();
                }else if(e instanceof FirebaseTooManyRequestsException){
                    otp.setError(e.getMessage());
                    otp.requestFocus();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                otpTimer.start();
                mVerificationId = s;
                mResendToken = forceResendingToken;
            }
        };
    }

    private void sendOTP(){
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91"+phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void resendOTP(){
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91"+phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallbacks)
                .setForceResendingToken(mResendToken)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            otpTimer.cancel();
                            FirebaseUser user = task.getResult().getUser();
                            AuthCredential authCredential = EmailAuthProvider.getCredential(email,password);
                            Objects.requireNonNull(user).linkWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Map<String,Object> map = new HashMap<>();
                                        map.put("email",email);
                                        map.put("phone",phone);

                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        firebaseFirestore.collection("users").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful()){
                                                    Intent mainIntent = new Intent(requireActivity(), MainActivity.class);
                                                    startActivity(mainIntent);
                                                    requireActivity().finish();
                                                }else{
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });
                                    }else{
                                        String error = task.getException().getMessage();
                                        Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                otp.setError("Invalid OTP");
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //timer.cancel();
    }
}