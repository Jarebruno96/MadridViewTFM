package Elements.ImprovedMapView;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class ImprovedMapView extends MapView {

    public ImprovedMapView(Context context) {
        super(context);

    }

    public ImprovedMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ImprovedMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ImprovedMapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }
}
