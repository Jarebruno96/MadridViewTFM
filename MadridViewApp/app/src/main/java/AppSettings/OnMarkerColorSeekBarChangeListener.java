package AppSettings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.juan.madridview.R;

public class OnMarkerColorSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    private final View view;
    private final Context context;

    public OnMarkerColorSeekBarChangeListener(View view, Context context){
        this.view = view;
        this.context = context;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean b) {

        TextView textView = null;

        switch (seekBar.getId()){
            case R.id.settingsMuseumSeekBarColor:
                textView = (TextView) view.findViewById(R.id.settingsMuseumColorMakerSample);
                break;
            case R.id.settingsMonumentSeekBarColor:
                textView = (TextView) view.findViewById(R.id.settingsMonumentColorMakerSample);
                break;
            case R.id.settingsChurchSeekBarColor:
                textView = (TextView) view.findViewById(R.id.settingsChurchColorMakerSample);
                break;
            case R.id.settingsParkSeekBarColor:
                textView = (TextView) view.findViewById(R.id.settingsParkColorMakerSample);
                break;
            case R.id.settingsTouristOfficeSeekBarColor:
                textView = (TextView) view.findViewById(R.id.settingsTouristOfficeColorMakerSample);
                break;
            case R.id.settingsDestinationSeekBarColor:
                textView = (TextView) view.findViewById(R.id.settingsDestinationColorMakerSample);
                break;
        }

        if(textView!=null){
            float[] hsv = {value,100,100};
            int color = Color.HSVToColor(hsv);

            textView.setBackgroundColor(color);
            seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        AppSettings appSettings = AppSettings.getInstance();

        switch (seekBar.getId()) {
            case R.id.settingsMuseumSeekBarColor:
                appSettings.setMuseumMarkerColor(context, seekBar.getProgress());
                break;
            case R.id.settingsMonumentSeekBarColor:
                appSettings.setMonumentMarkerColor(context, seekBar.getProgress());
                break;
            case R.id.settingsChurchSeekBarColor:
                appSettings.setChurchMarkerColor(context, seekBar.getProgress());
                break;
            case R.id.settingsParkSeekBarColor:
                appSettings.setParkMarkerColor(context, seekBar.getProgress());
                break;
            case R.id.settingsTouristOfficeSeekBarColor:
                appSettings.setTouristOfficeMarkerColor(context, seekBar.getProgress());
                break;
            case R.id.settingsDestinationSeekBarColor:
                appSettings.setDestinationMarkerColor(context, seekBar.getProgress());
                break;
        }
    }
}
