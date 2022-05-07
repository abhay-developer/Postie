package com.example.postie.Registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.postie.R;
import com.example.postie.shared.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CreateAccountFragment extends Fragment {

    private EditText email,phone,password,confirmPassword;
    private Button createAccountBtn;
    private ProgressBar progressBar;
    private TextView loginTV;

    private FirebaseAuth firebaseAuth;

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        firebaseAuth = FirebaseAuth.getInstance();
        loginTV.setOnClickListener(view1 -> ((RegisterActivity) requireActivity()).setFragment(new LoginFragment()));

        createAccountBtn.setOnClickListener(view12 -> {
            email.setError(null);
            phone.setError(null);
            password.setError(null);
            confirmPassword.setError(null);
            @Nullable String isEmailValid = Validation.isValidEmail(email.getText().toString().trim().toLowerCase(Locale.ROOT));
            @Nullable String isPhoneValid = Validation.isValidPhone(phone.getText().toString().trim().toLowerCase(Locale.ROOT));
            @Nullable String isPasswordValid = Validation.isValidPassword(password.getText().toString());
            @Nullable String isConfirmPasswordValid = Validation.isValidConfirmPassword(password.getText().toString(),confirmPassword.getText().toString());
            boolean isDataOk = true;
            if(isEmailValid!=null){
                email.setError(isEmailValid);
                email.requestFocus();
                isDataOk=false;
            }
            if(isPhoneValid!=null) {
                phone.setError(isPhoneValid);
                phone.requestFocus();
                isDataOk=false;
            }
            if(isPasswordValid!=null){
                password.setError(isPasswordValid);
                password.requestFocus();
                isDataOk=false;
            }
            if(isConfirmPasswordValid!=null){
                confirmPassword.setError(isConfirmPasswordValid);
                confirmPassword.requestFocus();
                isDataOk=false;
            }
            if(isDataOk) {
                createAccount();
            }
        });
    }

    private void init(View view){
        email = view.findViewById(R.id.emailSignUp);
        phone = view.findViewById(R.id.phoneSignUp);
        password = view.findViewById(R.id.passwordSignUp);
        confirmPassword = view.findViewById(R.id.confirmPasswordSignUp);
        createAccountBtn = view.findViewById(R.id.signUpBtn);
        progressBar = view.findViewById(R.id.progressBarSignUp);
        loginTV = view.findViewById(R.id.signInLink);
    }

    private void createAccount(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone",phone.getText().toString()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
                if(!documentSnapshot.isEmpty()){
                    phone.setError("User already registered with this phone number");
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            if(Objects.requireNonNull(task1.getResult().getSignInMethods()).isEmpty()){
                                ((RegisterActivity) requireActivity()).setFragment(new OtpFragment(email.getText().toString(),phone.getText().toString(),password.getText().toString()));
                            }else{
                                email.setError("User already exits with this mail");
                                email.requestFocus();
                            }
                        }else{
                            String error = Objects.requireNonNull(task1.getException()).getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                }
            }else{
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}