package com.example.juan.madridview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;

import AppSettings.AppSettings;
import AppSettings.OnMarkerColorSeekBarChangeListener;
import AppSettings.OnSearchCheckedClickListener;
import AppSettings.OnCloseDistanceTextWatcher;
import Elements.RegularEditText;
import ListenerInterfaces.OnFragmentInteractionListener;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.settingsFragmentTitle));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState){

        CheckBox museumsSearchCheckBox = (CheckBox) view.findViewById(R.id.settingsMuseumCheckBox);
        CheckBox monumentsSearchCheckBox = (CheckBox) view.findViewById(R.id.settingsMonumentCheckBox);
        CheckBox churchesSearchCheckBox = (CheckBox) view.findViewById(R.id.settingsChurchCheckBox);
        CheckBox parksSearchCheckBox = (CheckBox) view.findViewById(R.id.settingsParkCheckBox);
        CheckBox touristOfficeSearchCheckBox = (CheckBox) view.findViewById(R.id.settingsTouristOfficeCheckBox);

        SeekBar museumSeekBar = (SeekBar) view.findViewById(R.id.settingsMuseumSeekBarColor);
        SeekBar monumentSeekBar = (SeekBar) view.findViewById(R.id.settingsMonumentSeekBarColor);
        SeekBar churchSeekBar = (SeekBar) view.findViewById(R.id.settingsChurchSeekBarColor);
        SeekBar parkSeekBar = (SeekBar) view.findViewById(R.id.settingsParkSeekBarColor);
        SeekBar touristOfficeSeekBar = (SeekBar) view.findViewById(R.id.settingsTouristOfficeSeekBarColor);
        SeekBar destinationSeekBar = (SeekBar) view.findViewById(R.id.settingsDestinationSeekBarColor);

        RegularEditText closeDistanceEditText = (RegularEditText) view.findViewById(R.id.settingsCloseDistanceRegularInput);

        AppSettings appSettings = AppSettings.getInstance();

        museumsSearchCheckBox.setChecked(appSettings.searchMuseumsEnabled(getContext()));
        monumentsSearchCheckBox.setChecked(appSettings.searchMonumentsEnabled(getContext()));
        churchesSearchCheckBox.setChecked(appSettings.searchChurchesEnabled(getContext()));
        parksSearchCheckBox.setChecked(appSettings.searchParksEnabled(getContext()));
        touristOfficeSearchCheckBox.setChecked(appSettings.searchTouristOfficeEnabled(getContext()));

        museumsSearchCheckBox.setOnClickListener(new OnSearchCheckedClickListener(getContext()));
        monumentsSearchCheckBox.setOnClickListener(new OnSearchCheckedClickListener(getContext()));
        churchesSearchCheckBox.setOnClickListener(new OnSearchCheckedClickListener(getContext()));
        parksSearchCheckBox.setOnClickListener(new OnSearchCheckedClickListener(getContext()));
        touristOfficeSearchCheckBox.setOnClickListener(new OnSearchCheckedClickListener(getContext()));

        museumSeekBar.setOnSeekBarChangeListener(new OnMarkerColorSeekBarChangeListener(view, getContext()));
        monumentSeekBar.setOnSeekBarChangeListener(new OnMarkerColorSeekBarChangeListener(view, getContext()));
        churchSeekBar.setOnSeekBarChangeListener(new OnMarkerColorSeekBarChangeListener(view, getContext()));
        parkSeekBar.setOnSeekBarChangeListener(new OnMarkerColorSeekBarChangeListener(view, getContext()));
        touristOfficeSeekBar.setOnSeekBarChangeListener(new OnMarkerColorSeekBarChangeListener(view, getContext()));
        destinationSeekBar.setOnSeekBarChangeListener(new OnMarkerColorSeekBarChangeListener(view, getContext()));

        museumSeekBar.setProgress((int)appSettings.getMuseumMarkerColor(getContext()));
        monumentSeekBar.setProgress((int)appSettings.getMonumentMarkerColor(getContext()));
        churchSeekBar.setProgress((int)appSettings.getChurchMarkerColor(getContext()));
        parkSeekBar.setProgress((int)appSettings.getParkMarkerColor(getContext()));
        touristOfficeSeekBar.setProgress((int)appSettings.getTouristOfficeMarkerColor(getContext()));
        destinationSeekBar.setProgress((int)appSettings.getDestinationMarkerColor(getContext()));

        closeDistanceEditText.setText(Float.toString(appSettings.getCloseDistance(getContext())));
        closeDistanceEditText.addTextChangedListener(new OnCloseDistanceTextWatcher(getContext()));

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
}
