package AppSettings;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

public class OnCloseDistanceTextWatcher implements TextWatcher {

    private final Context context;

    public OnCloseDistanceTextWatcher(Context context){
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        AppSettings appSettings = AppSettings.getInstance();
        try {

            float value = Float.parseFloat(String.valueOf(editable.toString()));
            appSettings.setCloseDistance(context, value);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
