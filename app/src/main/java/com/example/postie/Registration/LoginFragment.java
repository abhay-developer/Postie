package com.example.postie.Registration;

import android.content.Intent;
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

import com.example.postie.MainActivity;
import com.example.postie.R;
import com.example.postie.shared.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment {

    private EditText emailOrPhone,password;
    private Button loginBtn;
    private ProgressBar progressBar;
    private TextView createAccountTV, forgotPasswordTV;
    private FirebaseAuth mAuth;


    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        mAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(view12 -> {
            progressBar.setVisibility(View.VISIBLE);
            @Nullable String isValidEmail = Validation.isValidEmail(emailOrPhone.getText().toString());
            @Nullable String isValidPhone = Validation.isValidPhone(emailOrPhone.getText().toString());
            @Nullable String isValidPassword = Validation.isValidPassword(password.getText().toString());
            boolean isDataOk = true;
            if(emailOrPhone.getText().toString().isEmpty()){
                emailOrPhone.setError("This field is required");
                progressBar.setVisibility(View.INVISIBLE);
                isDataOk=false;
            }
            if(isValidPassword!=null){
                password.setError(isValidPassword);
                password.requestFocus();
                progressBar.setVisibility(View.INVISIBLE);
                isDataOk=false;
            }
            if(isValidEmail!=null && isValidPhone!=null){
                emailOrPhone.setError("Please enter a valid email or phone number");
                progressBar.setVisibility(View.INVISIBLE);
                isDataOk=false;
            }

            if(isDataOk){
                if(isValidEmail==null){
                    login(emailOrPhone.getText().toString());
                }

                if(isValidPhone==null){
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone",emailOrPhone.getText().toString())
                            .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
                            if(documentSnapshot.isEmpty()){
                                emailOrPhone.setError("No user found with this phone number");
                                progressBar.setVisibility(View.INVISIBLE);
                            }else{
                                String email = Objects.requireNonNull(documentSnapshot.get(0).get("email")).toString();
                                login(email);
                            }

                        }else{
                            String error = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        createAccountTV.setOnClickListener(view1 -> ((RegisterActivity) requireActivity()).setFragment(new CreateAccountFragment()));
        forgotPasswordTV.setOnClickListener(view1 -> ((RegisterActivity) requireActivity()).setFragment(new ForgotPasswordFragment()));
    }

    private void init(View view){
        emailOrPhone = view.findViewById(R.id.emailSignIn);
        password = view.findViewById(R.id.passwordSignIn);
        loginBtn = view.findViewById(R.id.loginBtn);
        forgotPasswordTV = view.findViewById(R.id.forgotPasswordLink);
        createAccountTV = view.findViewById(R.id.signUpLink);
        progressBar = view.findViewById(R.id.progressBarSignIn);
    }

    private void login(String email){
        mAuth.signInWithEmailAndPassword(email,password.getText().toString()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Intent mainIntent = new Intent(requireActivity(), MainActivity.class);
                startActivity(mainIntent);
                requireActivity().finish();
            }else{
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}