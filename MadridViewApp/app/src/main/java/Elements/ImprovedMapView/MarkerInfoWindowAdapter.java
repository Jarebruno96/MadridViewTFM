package Elements.ImprovedMapView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juan.madridview.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import DirectionManager.DirectionManager;
import HttpHandler.DownloadImageTask;
import HttpHandler.VolleyCallback;
import HttpHandler.HttpHandler;
import Item.ItemTag;
import ListenerInterfaces.OnMyLocationChangedEventListener;
import PubSub.EventNotifier;
import PubSub.Publisher;
import User.User;
import Utils.AppUtils;
import Utils.ServerDataUtils;
/*
 *
 * Reference: https://stackoverflow.com/questions/14123243/google-maps-android-api-v2-interactive-infowindow-like-in-original-android-go/15040761#15040761
 *
 * */

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, OnMyLocationChangedEventListener
{

    private Context context;
    private View infoWindow;
    private MapWrapperLayout mapWrapperLayout;
    DirectionManager directionManager;
    private LatLng origin;

    private final static String TAG = "MarkerInfoWindowAdapter";

    public MarkerInfoWindowAdapter(Context context, MapWrapperLayout mapWrapperLayout){
        this.context = context;
        this.mapWrapperLayout = mapWrapperLayout;
        this.directionManager = new DirectionManager(context);
        this.infoWindow =  ((Activity) context).getLayoutInflater().inflate(R.layout.item_info_card_view, null);

        EventNotifier.subscribe(this, AppUtils.ON_MY_LOCATION_CHANGED_ACTION);

    }

    @Override
    public View getInfoWindow(final Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);

        Button goButton = (Button) infoWindow.findViewById(R.id.cardViewGoButton);
        goButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        getDirectionToItem(marker);
                        break;

                }
                return false;
            }
        });

        Button favouriteButton = (Button) infoWindow.findViewById(R.id.cardViewFavouriteButton);
        favouriteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        addItemToFavourites(marker);
                        break;

                }
                return false;
            }
        });

        ScrollView scrollView = (ScrollView) infoWindow.findViewById(R.id.aaa);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "Muevo el scroll");
                        if(marker.getSnippet().equalsIgnoreCase("0")){
                            marker.showInfoWindow();
                        }
                }
                return false;
            }
        });


        final ItemTag tag = (ItemTag) marker.getTag();

        if(marker.getSnippet().equalsIgnoreCase("1") && tag!=null) {

            try {
                JSONObject params = new JSONObject();
                params.put("item", tag.getName());

                HttpHandler.post(context, ServerDataUtils.SEARCH_ITEM_URL, params, new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {

                        try {
                            if (response != null) {

                                int code = response.getInt("success");

                                if (code != 1) {
                                    Toast.makeText(context, response.getString("error"), Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d(TAG, response.toString());

                                    CardView cardview = (CardView) infoWindow.findViewById(R.id.itemCardView);

                                    ImageView itemImage = (ImageView) cardview.findViewById(R.id.cardViewItemImage);
                                    TextView itemName = (TextView) cardview.findViewById(R.id.cardViewItemName);
                                    TextView itemType = (TextView) cardview.findViewById(R.id.cardViewItemType);
                                    TextView itemDescription = (TextView) cardview.findViewById(R.id.cardViewItemDescription);
                                    TextView itemAdress = (TextView) cardview.findViewById(R.id.cardViewItemAdress);
                                    TextView itemSchedule = (TextView) cardview.findViewById(R.id.cardViewItemSchedule);
                                    TextView itemServices = (TextView) cardview.findViewById(R.id.cardViewItemServices);
                                    BarChart ocupationBarChart = (BarChart) cardview.findViewById(R.id.cardViewItemOcupationChart);

                                    new DownloadImageTask(itemImage)
                                    {
                                        @Override
                                        public void onPostExecute(Bitmap bitmap) {
                                            super.onPostExecute(bitmap);
                                            marker.setSnippet("0");
                                            marker.showInfoWindow();
                                        }
                                    }.execute(response.getJSONObject("item").getString("image"));

                                    itemName.setText(response.getJSONObject("item").getString("title"));
                                    itemType.setText(response.getJSONObject("item").getString("type"));
                                    itemDescription.setText(response.getJSONObject("item").getJSONObject("organization").getString("organization-desc"));
                                    itemSchedule.setText(response.getJSONObject("item").getJSONObject("organization").getString("schedule"));
                                    itemServices.setText(response.getJSONObject("item").getJSONObject("organization").getString("services"));
                                    String address = response.getJSONObject("item").getJSONObject("address").getString("street-address") + " ("+response.getJSONObject("item").getJSONObject("address").getString("locality")+", "+response.getJSONObject("item").getJSONObject("address").getString("postal-code")+")";
                                    itemAdress.setText(address);

                                    tag.setImage(response.getJSONObject("item").getString("image"));

                                    JSONArray itemOcupation = response.getJSONObject("item").getJSONArray("ocupation");
                                    ArrayList<BarEntry> entries = getBarData(itemOcupation);
                                    //ArrayList<BarEntry> entries = getBarData(null);

                                    BarDataSet bardataset = new BarDataSet(entries, "People per day");

                                    BarData data = new BarData(getXAxis(), bardataset);
                                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                    ocupationBarChart.setData(data);
                                    ocupationBarChart.setDescription("");

                                    marker.setTag(tag);
                                    marker.setSnippet("0");
                                    marker.showInfoWindow();
                                    Log.d(TAG, "Datos cargados");

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailResponse(String error) {
                        marker.showInfoWindow();
                        Log.e(TAG, error);
                    }

                    private ArrayList<BarEntry> getBarData(JSONArray jsonArray) {
                        ArrayList<BarEntry> ocupation = new ArrayList<>();

                        try {

                            for(int i = 0;i<jsonArray.length();i++){
                                ocupation.add(new BarEntry(jsonArray.getInt(i),i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;
                        }

                        return ocupation;
                    }

                    private ArrayList<String> getXAxis() {
                        ArrayList<String> days = new ArrayList<>();
                        days.add("Mon");
                        days.add("Tue");
                        days.add("Wed");
                        days.add("Thu");
                        days.add("Fri");
                        days.add("Sat");
                        days.add("Sun");
                        return days;
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return infoWindow;
    }

    private ArrayList<BarEntry> getBarData(JSONArray jsonArray) {

        ArrayList<BarEntry> NoOfEmp = new ArrayList<>();

        NoOfEmp.add(new BarEntry(945f, 0));
        NoOfEmp.add(new BarEntry(1040f, 1));
        NoOfEmp.add(new BarEntry(1133f, 2));
        NoOfEmp.add(new BarEntry(1240f, 3));
        NoOfEmp.add(new BarEntry(1369f, 4));
        NoOfEmp.add(new BarEntry(1487f, 5));
        NoOfEmp.add(new BarEntry(1501f, 6));

        return NoOfEmp;
    }

    private ArrayList<String> getXAxis() {
        ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        days.add("Sat");
        days.add("Sun");
        return  days;
    }

    private void getDirectionToItem(Marker marker){

        Log.d(TAG, "Searching new route: "+marker.getTitle());

        if(directionManager.getAccessToken()!=null){

            if(origin!=null)
                directionManager.getDirection(origin, marker.getPosition(), (ItemTag) marker.getTag());
        }

    }

    private void addItemToFavourites(Marker marker){
        Log.d(TAG, "Adding new item to favourites: "+marker.getTitle());

        ItemTag tag = (ItemTag) marker.getTag();

        if(tag!=null){

            HashMap<String, String> parameters = new HashMap<>();

            User user = User.getInstance();
            parameters.put("userName", user.getUserName(context));
            parameters.put("name", tag.getName());
            parameters.put("type", tag.getType());
            parameters.put("image", tag.getImage());

            HttpHandler.post(context, ServerDataUtils.SERVER_URL + "/pages/php/addFavouriteItem.php", parameters, new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject response) {

                    try{
                        Toast.makeText(context, response.getString("description"), Toast.LENGTH_LONG).show();
                    }catch (JSONException e){
                        Toast.makeText(context, R.string.internalError, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailResponse(String error) {
                    Toast.makeText(context, R.string.internalError, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onMyLocationPublished(JSONObject location) {

        Log.d(TAG, "Guardo nueva localizacion");
        try {
                double latitude = location.getDouble("latitude");
                double longitude = location.getDouble("longitude");

                origin = new LatLng(latitude, longitude);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
