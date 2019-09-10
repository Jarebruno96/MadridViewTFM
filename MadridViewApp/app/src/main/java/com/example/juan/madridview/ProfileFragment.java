package com.example.juan.madridview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Elements.RegularEditText;
import HttpHandler.HttpHandler;
import HttpHandler.VolleyCallback;
import ListenerInterfaces.OnFragmentInteractionListener;
import Services.LocationService;
import User.User;
import Utils.AppUtils;
import Utils.ServerDataUtils;

public class ProfileFragment extends Fragment{

    private final String TAG = "ProfileFragment";
    private OnFragmentInteractionListener mListener;
    private TextView userNameTextView = null;
    private TextView mailTextView = null;
    private TextView userStatusTextView = null;
    private ImageView userStatusImageView = null;
    private RegularEditText oldPasswordInput = null;
    private RegularEditText newPassword1Input= null;
    private RegularEditText newPassword2Input = null;
    private RegularEditText passwordInput = null;
    private Button changePasswordButton = null;
    private Button deactivateAccountButton = null;

    public ProfileFragment() {
        // Required empty public constructo

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.profileFragmentTitle));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userNameTextView = (TextView) getActivity().findViewById(R.id.profileUsername);
        mailTextView = (TextView) getActivity().findViewById(R.id.profileMail);

        userStatusTextView = (TextView) getActivity().findViewById(R.id.profileUserStatus);

        User user = User.getInstance();

        int status = user.getVerified(getActivity());

        userStatusImageView = (ImageView) getActivity().findViewById(R.id.profileUserStatusIcon);

        if(status == 1){
            userStatusTextView.setText(getResources().getString(R.string.verifiedText));
            userStatusImageView.setBackgroundResource(R.drawable.icon_verified);
        }
        else{
            userStatusTextView.setText(getResources().getString(R.string.notVerifiedText));
            userStatusImageView.setBackgroundResource(android.R.drawable.ic_delete);
        }

        oldPasswordInput = (RegularEditText) getActivity().findViewById(R.id.profileOldPasswordInput);
        newPassword1Input = (RegularEditText) getActivity().findViewById(R.id.profileNewPassword1Input);

        newPassword2Input = (RegularEditText) getActivity().findViewById(R.id.profileNewPassword2Input);
        newPassword2Input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b){
                    if(!newPassword1Input.getText().toString().equals(newPassword2Input.getText().toString())){
                        newPassword2Input.setError(getResources().getString(R.string.passwordDontMatch));
                    }
                    else{
                        newPassword2Input.setError(null);
                    }
                }
            }
        });

        changePasswordButton = (Button) getActivity().findViewById(R.id.profileChangePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangePasswordButtonClick(view);
            }
        });

        deactivateAccountButton = (Button) getActivity().findViewById(R.id.profileDeactivateAccountButton);
        deactivateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeactivateButtonClick(view);
            }
        });

        passwordInput = (RegularEditText) getActivity().findViewById(R.id.profilePasswordInput);

    }

    @Override
    public void onResume(){
        super.onResume();

        User user = User.getInstance();
        String userName = user.getUserName(getActivity());
        String mail = user.getMail(getActivity());

        userNameTextView.setText(userName != null? userName: getResources().getString(R.string.unknownText));
        mailTextView.setText(mail != null? mail: getResources().getString(R.string.unknownText));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("oldPassword", oldPasswordInput.getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onChangePasswordButtonClick(final View view){

        boolean error = false;

        if (oldPasswordInput.getText().toString().isEmpty()){
            oldPasswordInput.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }
        else{
            oldPasswordInput.setError(null);
        }

        if(newPassword1Input.getText().toString().isEmpty()){
            newPassword1Input.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }
        else{
            newPassword1Input.setError(null);
        }

        if(newPassword2Input.getText().toString().isEmpty()){
            newPassword2Input.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }
        else{
            newPassword2Input.setError(null);
        }


        if(!newPassword1Input.getText().toString().equals(newPassword2Input.getText().toString())){
            newPassword2Input.setError(getResources().getString(R.string.passwordDontMatch));
            error = true;
        }
        else{
            newPassword2Input.setError(null);
        }


        if(!error){

            HashMap<String, String> params = new HashMap<>();
            params.put("userName", userNameTextView.getText().toString());
            params.put("oldPassword", oldPasswordInput.getText().toString());
            params.put("newPassword1", newPassword1Input.getText().toString());
            params.put("newPassword2", newPassword2Input.getText().toString());

            HttpHandler.post(getContext(), ServerDataUtils.SERVER_URL + "/pages/php/changePassword.php", params, new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject response) {

                    if(response!=null){
                        try{
                            if(response.getInt("success")== 1){
                                oldPasswordInput.setText("");
                                newPassword1Input.setText("");
                                newPassword2Input.setText("");
                                oldPasswordInput.clearFocus();
                                newPassword1Input.clearFocus();
                                newPassword2Input.clearFocus();

                                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                                Toast.makeText(getContext(), getResources().getString(R.string.passwordChangedText), Toast.LENGTH_LONG).show();
                            }
                            else{
                                switch (response.getInt("code")){
                                    case ServerDataUtils.INTERNAL_ERROR:
                                        Toast.makeText(getContext(), getResources().getString(R.string.internalError), Toast.LENGTH_LONG).show();
                                        break;
                                    case ServerDataUtils.DIFFERENT_PASSWORDS_ERROR:
                                        Toast.makeText(getContext(), getResources().getString(R.string.passwordDontMatch), Toast.LENGTH_LONG).show();
                                        newPassword2Input.setError(getResources().getString(R.string.passwordDontMatch));
                                        break;
                                    case ServerDataUtils.USER_NOT_FOUND_ERROR:
                                        Toast.makeText(getContext(), getResources().getString(R.string.userNotFound), Toast.LENGTH_LONG).show();
                                        break;
                                    case ServerDataUtils.INCORRECT_PASSWORD:
                                        oldPasswordInput.setError(getResources().getString(R.string.incorrectPassword));
                                        break;
                                    default:
                                        Toast.makeText(getContext(), getResources().getString(R.string.internalError), Toast.LENGTH_LONG).show();
                                }
                            }
                        }catch (JSONException e){
                            Toast.makeText(getContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
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

    private void onDeactivateButtonClick(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.cautionText)).setMessage(getResources().getString(R.string.deactivateDialogMessage));
        builder.setPositiveButton(getResources().getString(R.string.positiveAnswer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deactivateAccount();
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

    private void deactivateAccount(){

        boolean error = false;

        if(passwordInput.getText().toString().isEmpty()){
            passwordInput.setError(getResources().getString(R.string.fieldIsEmpty));
            error = true;
        }
        else{
            passwordInput.setError(null);
        }

        if(!error){
            HashMap<String, String> params = new HashMap<>();
            params.put(getResources().getString(R.string.userNameSharedPreferences), userNameTextView.getText().toString());
            params.put(getResources().getString(R.string.passwordSharedPreferences), passwordInput.getText().toString());

            HttpHandler.post(getContext(), ServerDataUtils.SERVER_URL + "/pages/php/cancelAccount.php", params, new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject response) {
                    if(response!=null){
                        try{
                            if(response.getInt("success")== 1){

                                Toast.makeText(getContext(), getResources().getString(R.string.deactivatedAccountText), Toast.LENGTH_LONG).show();

                                User user = User.getInstance();
                                user.removeActivated(getActivity());
                                user.removeDateIn(getActivity());
                                user.removeMail(getActivity());
                                user.removeUserName(getActivity());
                                user.removeVerified(getActivity());

                                getActivity().stopService(new Intent(getActivity(), LocationService.class));

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                switch (response.getInt("code")){
                                    case ServerDataUtils.INTERNAL_ERROR:
                                        Toast.makeText(getContext(), getResources().getString(R.string.internalError), Toast.LENGTH_LONG).show();
                                        break;
                                    case ServerDataUtils.USER_NOT_FOUND_ERROR:
                                        Toast.makeText(getContext(), getResources().getString(R.string.userNotFound), Toast.LENGTH_LONG).show();
                                        break;
                                    case ServerDataUtils.INCORRECT_PASSWORD:
                                        passwordInput.setError(getResources().getString(R.string.incorrectPassword));
                                        break;
                                    default:
                                        Toast.makeText(getContext(), getResources().getString(R.string.internalError), Toast.LENGTH_LONG).show();
                                }

                                Log.d(TAG, response.getString("description"));
                            }
                        }catch (JSONException e){
                            Toast.makeText(getContext(), getResources().getString(R.string.unknownError), Toast.LENGTH_LONG).show();
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

}
