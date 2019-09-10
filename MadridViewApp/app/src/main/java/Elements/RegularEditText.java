package Elements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.juan.madridview.R;

public class RegularEditText extends android.support.v7.widget.AppCompatEditText {

    private Context context;

    public RegularEditText(Context context){
        super(context);
        this.context = context;

    }

    public RegularEditText(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    @Override
    public void setError(CharSequence error) {
        super.setError(error);

        if(error!=null) {
            Drawable drawable = (Drawable) getResources().getDrawable(R.drawable.icon_error);
            drawable.setBounds(0, 0,
                    drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            this.setError(error, drawable);
        }
    }
}
