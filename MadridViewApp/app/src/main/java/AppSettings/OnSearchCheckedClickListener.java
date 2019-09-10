package AppSettings;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.example.juan.madridview.R;

public class OnSearchCheckedClickListener implements View.OnClickListener {

    private final Context context;

    public OnSearchCheckedClickListener(Context context){
        this.context = context;
    }
    @Override
    public void onClick(View view) {

        CheckBox checkBox = (CheckBox) view;
        AppSettings appSettings = AppSettings.getInstance();

        switch (checkBox.getId()){
            case R.id.settingsMuseumCheckBox:
                appSettings.setSearchMuseums(context, checkBox.isChecked());
                break;
            case R.id.settingsMonumentCheckBox:
                appSettings.setSearchMonuments(context, checkBox.isChecked());
                break;
            case R.id.settingsChurchCheckBox:
                appSettings.setSearchChurches(context, checkBox.isChecked());
                break;
            case R.id.settingsParkCheckBox:
                appSettings.setSearchParks(context, checkBox.isChecked());
                break;
            case R.id.settingsTouristOfficeCheckBox:
                appSettings.setSearchTouristOffice(context, checkBox.isChecked());
                break;
        }
    }
}
