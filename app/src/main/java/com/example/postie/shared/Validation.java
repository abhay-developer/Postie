package com.example.postie.shared;

import androidx.annotation.Nullable;

import java.util.regex.Pattern;


public class Validation {
    private static final Pattern EMAIL_REGEX =Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final String PASSWORD_REGEX ="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final String PHONE_REGEX = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

    public static @Nullable String isValidEmail(String email){
        if(email.isEmpty()){
            return "Email is required";
        }
        if(!EMAIL_REGEX.matcher(email).matches()){
            return "Please enter a valid email";
        }
        return null;
    }

    public static @Nullable String isValidPhone(String phone){
        if(phone.isEmpty()){
            return "Phone number is required";
        }
        if(phone.length()!=10){
            return "Please enter a valid phone number";
        }

        if(!phone.matches(PHONE_REGEX)){
            return "Please enter a valid phone number";
        }
        return null;
    }

    public static @Nullable String isValidConfirmPassword(String password,String confirmPassword){
        if(confirmPassword.isEmpty()){
            return "Confirm password is required";
        }

        if(!password.equals(confirmPassword)){
            return "Password and Confirm Password must be same";
        }

        return null;
    }

    public static @Nullable String isValidPassword(String password){
        if(password.isEmpty()){
            return "Password is required";
        }

        if(password.length()<8){
            return "Password length must be 8 char long";
        }

        if(!password.matches(PASSWORD_REGEX)){
            return "Password must contain atleast 1 Upper case letter, 1 lower case letter 1 number, 1 special char and no spaces";
        }

        return null;
    }
}
