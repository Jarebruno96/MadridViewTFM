package Elements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;

import com.example.juan.madridview.R;

public class MailEditText extends android.support.v7.widget.AppCompatEditText {

    private Context context;
    private boolean isValid;

    public MailEditText(Context context){
        super(context);
        this.context = context;
        this.isValid = false;
        configure();
    }

    public MailEditText(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        this.isValid = false;
        configure();
    }

    public MailEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.isValid = false;
        configure();
    }

    private void configure(){
        final MailEditText mailEditText = this;
        mailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    isValid = checkMailAddress(mailEditText.getText().toString());

                    if(!isValid){
                        mailEditText.setError(getResources().getString(R.string.mailNotValidErrorText));
                    }
                    else{
                        mailEditText.setError(null);
                    }
                }
            }
        });
    }

    @Override
    public void setError(CharSequence error) {
        super.setError(error);

        if(error!=null){
            Drawable drawable = (Drawable) getResources().getDrawable(R.drawable.icon_error);
            drawable.setBounds(0, 0,
                    drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            this.setError(error, drawable);
        }

    }

    private boolean checkMailAddress(String mailAddress){
        return !TextUtils.isEmpty(mailAddress) && Patterns.EMAIL_ADDRESS.matcher(mailAddress).matches();
    }

    public boolean getIsValid(){
        isValid = checkMailAddress(this.getText().toString());
        return isValid;
    }


}
