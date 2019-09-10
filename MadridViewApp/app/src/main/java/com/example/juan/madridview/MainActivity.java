package com.example.juan.madridview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import Elements.RegularEditText;
import HttpHandler.HttpHandler;
import HttpHandler.VolleyCallback;
import User.User;
import Utils.AppUtils;
import Utils.ServerDataUtils;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private RegularEditText userNameInput = null;
    private RegularEditText passwordInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.StatusBarUtils.makeTraslucent(this);
        userNameInput = (RegularEditText) findViewById(R.id.mainUserNameInput);
        passwordInput = (RegularEditText) findViewById(R.id.mainPasswordInput);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){

        super.onResume();
        userNameInput = (RegularEditText) findViewById(R.id.mainUserNameInput);
        passwordInput = (RegularEditText) findViewById(R.id.mainPasswordInput);

        checkSession();

    }

    public void onRegisterButtonClick(View view){

        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordButtonClick(View view){

        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(intent);
    }

    public void onLoginButtonClick(View view) {

        boolean error = false;

        if(userNameInput.getText().toString().isEmpty()){
            error = true;
            userNameInput.setError(getResources().getString(R.string.fieldIsEmpty));
        }
        else{
            userNameInput.setError(null);
        }

        if(!error){
            if(passwordInput.getText().toString().isEmpty()){
                error = true;
                passwordInput.setError(getResources().getString(R.string.fieldIsEmpty));
            }
            else{
                passwordInput.setError(null);
            }
        }

        if(!error) {

            HashMap<String, String> params = new HashMap<>();
            params.put("userName", userNameInput.getText().toString());
            params.put("password", passwordInput.getText().toString());

            HttpHandler.post(getApplicationContext(), ServerDataUtils.SERVER_URL + "/pages/php/checkLogin.php", params, new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject response) {

                    try{
                        if(response!=null) {
                            if (response.getInt("success") == 1){

                                JSONObject data = response.getJSONObject("data");

                                if (data.getInt("activated")==0){
                                    requestConfirmation(data);
                                }
                                else{

                                    User user = User.getInstance();
                                    user.setUserName(getApplicationContext(),data.getString("userName"));
                                    user.setMail(getApplicationContext(), data.getString("mail"));
                                    user.setUserDateIn(getApplicationContext(), data.getString("dateIn"));
                                    user.setActivated(getApplicationContext(), data.getInt("activated"));
                                    user.setVerified(getApplicationContext(), data.getInt("verified"));

                                    Toast.makeText(getApplicationContext(), "Login complete", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else{
                                Log.d(TAG, response.toString());
                                switch (response.getInt("code")) {
                                    case ServerDataUtils.INTERNAL_ERROR:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
                                        break;
                                    case ServerDataUtils.USER_NOT_FOUND_ERROR:
                                        userNameInput.setError(getResources().getString(R.string.userNotFound));
                                        break;
                                    case ServerDataUtils.INCORRECT_PASSWORD:
                                        passwordInput.setError(getResources().getString(R.string.incorrectPassword));
                                        break;
                                    default:
                                        userNameInput.setError(null);
                                        passwordInput.setError(null);
                                }
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Can not get server response", Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException e){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.getMessage());
                    }
                }

                @Override
                public void onFailResponse(String error) {
                    Log.e(TAG, error);
                }
            });
        }
    }

    private void checkSession(){

        User user = User.getInstance();

        if(user.getUserName(getApplicationContext())!=null && user.getMail(getApplicationContext())!=null && user.getUserDateIn(getApplicationContext())!=null && user.getActivated(getApplicationContext())!=-1 && user.getVerified(getApplicationContext())!=-1){

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }

    private void requestConfirmation(final JSONObject data){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.cautionText)).setMessage(getResources().getString(R.string.activateDialogMessage));
        builder.setPositiveButton(getResources().getString(R.string.positiveAnswer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activateAccount(data);
                Log.d(TAG, "Activando");
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.negativeAnswer), null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorDialogAnswers));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorDialogAnswers));
            }
        });
        alertDialog.show();

    }

    private void activateAccount(final JSONObject data){

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.userNameSharedPreferences), userNameInput.getText().toString());
        params.put(getResources().getString(R.string.passwordSharedPreferences), passwordInput.getText().toString());

        HttpHandler.post(getApplicationContext(), ServerDataUtils.SERVER_URL + "/pages/php/activeAccount.php", params, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) {

                if(response!=null){
                    try{
                        if(response.getInt("success")==1){

                            User user = User.getInstance();
                            user.setUserName(getApplicationContext(),data.getString("userName"));
                            user.setMail(getApplicationContext(), data.getString("mail"));
                            user.setUserDateIn(getApplicationContext(), data.getString("dateIn"));
                            user.setActivated(getApplicationContext(), data.getInt("activated"));
                            user.setVerified(getApplicationContext(), data.getInt("verified"));

                            Toast.makeText(getApplicationContext(), "Login complete", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);

                        }
                        else{
                            switch (response.getInt("code")){
                                case ServerDataUtils.INTERNAL_ERROR:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.internalError), Toast.LENGTH_LONG).show();
                                    break;
                                case ServerDataUtils.USER_NOT_FOUND_ERROR:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.internalError), Toast.LENGTH_LONG).show();
                                    break;
                                case ServerDataUtils.INCORRECT_PASSWORD:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.incorrectPassword), Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
                            }
                        }
                    }catch (JSONException e){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
                        Log.d(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailResponse(String error) {
                Log.d(TAG, error);
            }
        });
    }

}
