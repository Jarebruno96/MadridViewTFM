package com.example.juan.madridview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Elements.MailEditText;
import Elements.RegularEditText;
import HttpHandler.VolleyCallback;
import HttpHandler.HttpHandler;
import User.User;
import Utils.AppUtils;
import Utils.ServerDataUtils;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private RegularEditText userNameInput = null;
    private MailEditText mailInput = null;
    private RegularEditText passwordInput1 = null;
    private RegularEditText passwordInput2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Utils.StatusBarUtils.makeTraslucent(this);
        passwordInput1 = (RegularEditText) findViewById(R.id.registerPasswordInput1);
        passwordInput2 = (RegularEditText) findViewById(R.id.registerPasswordInput2);
    }

    @Override
    protected void onResume(){

        super.onResume();
        userNameInput = (RegularEditText) findViewById(R.id.registerUsernameInput);
        mailInput = (MailEditText) findViewById(R.id.registerMailInput);
        passwordInput1 = (RegularEditText) findViewById(R.id.registerPasswordInput1);
        passwordInput2 = (RegularEditText) findViewById(R.id.registerPasswordInput2);

        configure();
    }

    private void configure(){

        passwordInput2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b){
                    if(!passwordInput1.getText().toString().equals(passwordInput2.getText().toString())){
                        passwordInput2.setError(getResources().getString(R.string.passwordDontMatch));
                    }
                    else{
                        passwordInput2.setError(null);
                    }
                }
            }
        });
    }

    public void onRegisterButtonClick(View view){

        boolean error = false;

        if(userNameInput.getText().toString().isEmpty()){
            userNameInput.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }

        if(mailInput.getText().toString().isEmpty()) {
            mailInput.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }

        if(passwordInput1.getText().toString().isEmpty()){
            passwordInput1.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }

        if(passwordInput2.getText().toString().isEmpty()){
            passwordInput2.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }
        else{
            if(!passwordInput2.getText().toString().equals(passwordInput1.getText().toString())){
                passwordInput2.setError(getResources().getString(R.string.passwordDontMatch));
                error = true;
            }
        }

        if(!error){

            HashMap<String, String> params = new HashMap<>();
            params.put("userName", userNameInput.getText().toString());
            params.put("mail", mailInput.getText().toString());
            params.put("password1", passwordInput1.getText().toString());
            params.put("password2", passwordInput2.getText().toString());

            HttpHandler.post(getApplicationContext(), ServerDataUtils.SERVER_URL +"/pages/php/checkSignUp.php", params,new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject response) {
                    try {
                        if (response != null) {
                            if (response.getInt("success") == 1) {

                                User user = User.getInstance();
                                user.setUserName(getApplicationContext(), response.getString("userName"));
                                user.setMail(getApplicationContext(), response.getString("mail"));
                                user.setUserDateIn(getApplicationContext(), response.getString("dateIn"));
                                user.setActivated(getApplicationContext(), response.getInt("activated"));
                                user.setVerified(getApplicationContext(), response.getInt("verified"));

                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registerCompleted), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            }
                            else{
                                switch (response.getInt("code")){
                                    case ServerDataUtils.INTERNAL_ERROR:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
                                    break;
                                    case ServerDataUtils.USERNAME_ALREADY_EXISTS:
                                        userNameInput.setError(getResources().getString(R.string.userNameNotAvailable));
                                    break;
                                    case ServerDataUtils.MAIL_ALREADY_EXISTS:
                                        mailInput.setError(getResources().getString(R.string.mailNotAvailable));
                                    break;
                                    case ServerDataUtils.DIFFERENT_PASSWORDS_ERROR:
                                        passwordInput2.setError(getResources().getString(R.string.passwordDontMatch));
                                    break;
                                    default:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }catch(JSONException e){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.getMessage());
                    }
                }

                @Override
                public void onFailResponse(String error) {
                    Log.d(TAG, error);
                }
            });

        }
    }
}
