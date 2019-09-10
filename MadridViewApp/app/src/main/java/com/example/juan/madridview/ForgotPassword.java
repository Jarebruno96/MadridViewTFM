package com.example.juan.madridview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Elements.MailEditText;
import HttpHandler.HttpHandler;
import HttpHandler.VolleyCallback;
import Utils.ServerDataUtils;

public class ForgotPassword extends AppCompatActivity {

    private final String TAG = "ForgotPasswordActivity";
    private MailEditText mailEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Utils.StatusBarUtils.makeTraslucent(this);
        mailEditText = (MailEditText) findViewById(R.id.forgotPasswordMailInput);
    }
    @Override
    protected void onResume(){

        super.onResume();
        mailEditText = (MailEditText) findViewById(R.id.forgotPasswordMailInput);
    }

    public void onSubmitButtonClick(View view){

        if(mailEditText.getText().toString().isEmpty()){
            mailEditText.setError(getResources().getString(R.string.fieldIsEmpty));
        }
        else if(!mailEditText.getIsValid()){
            mailEditText.setError(getResources().getString(R.string.mailNotValidErrorText));
        }
        else{
            mailEditText.setError(null);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("mail", mailEditText.getText().toString());
            HttpHandler.post(getApplicationContext(), ServerDataUtils.SERVER_URL + "/pages/php/updatePassword.php", params, new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject response) {
                    try {
                        if(response!=null){
                            if(response.getInt("success")==1){

                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.newPasswordSent), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                switch (response.getInt("code")){
                                    case ServerDataUtils.INTERNAL_ERROR:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG);
                                        break;
                                    case ServerDataUtils.MAIL_NOT_FOUND_ERROR:
                                        mailEditText.setError(getResources().getString(R.string.mailNotFound));
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG);
                                }
                            }
                        }
                    }catch (JSONException e){
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG);
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
