package Elements.ImprovedMapView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class MapFragment extends SupportMapFragment {
    public View mapView;
    public TouchableWrapper mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mapView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mapView);

        //Cambiar el botón de centrar en mi localización de sitio

        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
        RelativeLayout.LayoutParams locationButtonRPL = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        locationButtonRPL.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        locationButtonRPL.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        locationButtonRPL.setMargins(0, 225, 30, 0);

        //Cambiar el botón de la brújula de sitio

        View compassButton = mapView.findViewWithTag("GoogleMapCompass");
        RelativeLayout.LayoutParams compassButtonRPL = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
        compassButtonRPL.addRule(RelativeLayout.ALIGN_PARENT_END);
        compassButtonRPL.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        compassButtonRPL.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
        compassButtonRPL.topMargin = 500;
        compassButtonRPL.rightMargin = 30;

        return mTouchView;

    }

    @Override
    public View getView() {
        return mapView;
    }
}

