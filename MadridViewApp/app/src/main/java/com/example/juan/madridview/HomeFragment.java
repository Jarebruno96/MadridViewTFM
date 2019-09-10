package com.example.juan.madridview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import AppSettings.AppSettings;
import DirectionManager.Destination;
import Elements.ImprovedMapView.MapFragment;
import Elements.ImprovedMapView.MapWrapperLayout;
import Elements.ImprovedMapView.MarkerInfoWindowAdapter;
import Item.ItemTag;
import ListenerInterfaces.OnMySupportMapFragmentTouched;
import ListenerInterfaces.OnFragmentInteractionListener;
import ListenerInterfaces.OnItemEventListener;
import ListenerInterfaces.OnMyLocationChangedEventListener;
import ListenerInterfaces.OnNewRouteCalculated;
import ListenerInterfaces.OnSearchFavouriteItemListener;
import PubSub.EventNotifier;
import Route.Route;
import Route.Section;
import Route.RoutePoint;
import Utils.AppUtils;
import Utils.ServerDataUtils;
import HttpHandler.HttpHandler;
import HttpHandler.VolleyCallback;
import HttpHandler.DownloadImageTask;

public class HomeFragment extends Fragment
        implements OnItemEventListener,
        OnMapReadyCallback,
        OnMyLocationChangedEventListener,
        OnMySupportMapFragmentTouched,
        OnNewRouteCalculated,
        OnSearchFavouriteItemListener {

    private OnFragmentInteractionListener mListener;
    private final static String TAG = "HomeFragment";
    private GoogleMap map;
    private boolean followLocationEnabled = true;
    private float defaultZoomLevel = 16;
    private SearchView searchView;
    private MapWrapperLayout mapWrapperLayout;

    private View routeSummaryView;
    private ArrayList<Marker> closeItemMarkers = new ArrayList<>();
    private Marker searchItemMarker;
    private Destination destination;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.homeFragmentTitle));

        EventNotifier.subscribe(this, AppUtils.CLOSE_ITEMS_FOUND_ACTION);
        EventNotifier.subscribe(this, AppUtils.ON_MY_LOCATION_CHANGED_ACTION);
        EventNotifier.subscribe(this, AppUtils.ON_MY_SUPPORT_MAP_FRAGMENT_TOUCHED);
        EventNotifier.subscribe(this, AppUtils.ON_NEW_ROUTE_CALCULATED);
        EventNotifier.subscribe(this, AppUtils.ON_SEARCH_FAVOURITE_ITEM);


    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.homeMapFragment);

        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mapWrapperLayout = (MapWrapperLayout) getActivity().findViewById(R.id.homeMapWrapperLayout);

        ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.homeMapMenuOptionsImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapMenuButtonClick(view);
            }
        });

        searchView = (SearchView) getActivity().findViewById(R.id.homeItemSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String itemName) {

                showSearchedItemInMap(itemName);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStart() {


        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
    }

    @Override
    public void onItemPublished(JSONObject item) {

        if (item != null) {
            //Log.d(TAG, item.toString());
            if (map != null) {

                try {

                    if (item.getInt("success") == 1) {

                        JSONArray closeItems = item.getJSONArray("items");

                        MarkerOptions options;
                        ArrayList<MarkerOptions> markerOptions = new ArrayList<>();
                        ArrayList<ItemTag> tags = new ArrayList<>();

                        for (int i = 0; i < closeItems.length(); i++) {

                            JSONObject closeItem = closeItems.getJSONObject(i);
                            JSONObject location = closeItem.getJSONObject("location");
                            LatLng position = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));

                            if (!(searchItemMarker != null && searchItemMarker.getTitle().equals(closeItem.getString("title")))) {
                                options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(AppSettings.getInstance().getItemTypeMarkerColor(getContext(), closeItem.getString("type")))).position(position).title(closeItem.getString("title"));
                                markerOptions.add(options);
                                tags.add(new ItemTag(closeItem.getString("title"), closeItem.getString("type"), closeItem.getString("image")));
                            }
                        }

                        //Añade los que no están
                        for(int i=0;i<markerOptions.size();i++){

                            boolean found = false;
                            Iterator<Marker> it = closeItemMarkers.iterator();

                            while(it.hasNext()){
                                Marker marker = it.next();
                                if(marker.getTitle().equalsIgnoreCase(markerOptions.get(i).getTitle())){
                                    found = true;
                                    break;
                                }
                            }

                            if(!found){
                                if(map!=null){
                                    Marker m = map.addMarker(markerOptions.get(i));
                                    m.setTag(tags.get(i));
                                    closeItemMarkers.add(m);
                                }

                            }
                        }

                        //Eliminar los que no están cerca
                        Iterator<Marker> it = closeItemMarkers.iterator();

                        while (it.hasNext()){
                            Marker m = it.next();
                            boolean found = false;
                            for(int i = 0 ;i<markerOptions.size(); i++){
                                if(m.getTitle().equalsIgnoreCase(markerOptions.get(i).getTitle())){
                                    found = true;
                                    break;
                                }
                            }
                            if(!found){
                                m.remove();
                                it.remove();
                            }
                        }

                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.closeItemsNotFoundText), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if (AppUtils.checkPermissionsGranted(getContext())) {
            map.setMyLocationEnabled(true);
        } else {
            AppUtils.requestPermissions(getActivity());
        }
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setIndoorEnabled(true);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (followLocationEnabled) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                }
            }
        });
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                followLocationEnabled = true;
                return false;
            }
        });
        map.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {

                followLocationEnabled = true;

                if (map.getCameraPosition().zoom < defaultZoomLevel) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), defaultZoomLevel));

                }
            }
        });

        map.setInfoWindowAdapter(new MarkerInfoWindowAdapter(getActivity(), mapWrapperLayout));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setSnippet("1");
                marker.showInfoWindow();
                return true;
            }
        });

        mapWrapperLayout.init(map, getPixelsFromDp(getContext(), 39 + 20));

    }

    @Override
    public void onMyLocationPublished(JSONObject location) {

        try {

            if (followLocationEnabled && map != null) {
                double latitude = location.getDouble("latitude");
                double longitude = location.getDouble("longitude");

                LatLng latLng = new LatLng(latitude, longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoomLevel));
                EventNotifier.unSubscribe(this, AppUtils.ON_MY_LOCATION_CHANGED_ACTION);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showSearchedItemInMap(final String itemName){

        followLocationEnabled = false;

        try {

            JSONObject params = new JSONObject();
            params.put("item", itemName);

            HttpHandler.post(getContext(), ServerDataUtils.SEARCH_ITEM_TAG_URL, params,  new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject response) {
                    try {

                        Log.d(TAG, response.toString());
                        if (response != null) {

                            int code = response.getInt("success");
                            JSONObject item = response.getJSONObject("item");

                            if (code != 1) {
                                Toast.makeText(getContext(), response.getString("description"), Toast.LENGTH_LONG).show();
                            } else {

                                if (searchItemMarker != null) {
                                    if (searchItemMarker.isInfoWindowShown()) {
                                        searchItemMarker.hideInfoWindow();
                                    }

                                    boolean found = false;

                                    for(int i =0;i<closeItemMarkers.size();i++){
                                        if(searchItemMarker.getTitle().equalsIgnoreCase(closeItemMarkers.get(i).getTitle())){
                                            found = true;
                                            searchItemMarker = null;
                                            break;
                                        }
                                    }

                                    if(!found){
                                        searchItemMarker.remove();
                                        searchItemMarker = null;
                                    }
                                }

                                LatLng position = new LatLng(item.getJSONObject("location").getDouble("latitude"), item.getJSONObject("location").getDouble("longitude"));

                                MarkerOptions searchItemMarkerOptions = new MarkerOptions()
                                        .title(item.getString("title"))
                                        .position(position)
                                        .icon(BitmapDescriptorFactory.defaultMarker(AppSettings.getInstance().getItemTypeMarkerColor(getContext(), item.getString("type"))));

                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, defaultZoomLevel));

                                boolean found = false;

                                for(int i=0;i<closeItemMarkers.size();i++){
                                    if(searchItemMarkerOptions.getTitle().equalsIgnoreCase(closeItemMarkers.get(i).getTitle())){
                                        found = true;
                                        closeItemMarkers.get(i).setSnippet("1");

                                        closeItemMarkers.get(i).showInfoWindow();
                                        searchItemMarker = closeItemMarkers.get(i);
                                    }
                                }
                                if(!found){
                                    searchItemMarker = map.addMarker(searchItemMarkerOptions);
                                    searchItemMarker.setTag(new ItemTag(item.getString("title"), item.getString("type"), item.getString("image")));
                                    searchItemMarker.setSnippet("1");
                                    searchItemMarker.showInfoWindow();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailResponse(String error) {
                    Log.e(TAG, error);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMySupportMapFragmentTouched(JSONObject item) {

        try {
            if (item.getBoolean("touched")) {
                followLocationEnabled = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onMapMenuButtonClick(View view) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.menu_map_mode);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        RadioButton normalButton = (RadioButton) dialog.findViewById(R.id.normalMapModeRadioButton);
        RadioButton satelliteButton = (RadioButton) dialog.findViewById(R.id.satelliteMapModeRadioButton);
        RadioButton terrrainButton = (RadioButton) dialog.findViewById(R.id.terrainMapModeRadioButton);
        RadioButton hybridButton = (RadioButton) dialog.findViewById(R.id.hybridMapModeRadioButton);

        if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            radioGroup.check(normalButton.getId());
        } else if (map.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            radioGroup.check(satelliteButton.getId());
        } else if (map.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
            radioGroup.check(terrrainButton.getId());
        } else if (map.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            radioGroup.check(hybridButton.getId());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {


                RadioButton normalButton = (RadioButton) dialog.findViewById(R.id.normalMapModeRadioButton);
                RadioButton satelliteButton = (RadioButton) dialog.findViewById(R.id.satelliteMapModeRadioButton);
                RadioButton terrainButton = (RadioButton) dialog.findViewById(R.id.terrainMapModeRadioButton);
                RadioButton hybridButton = (RadioButton) dialog.findViewById(R.id.hybridMapModeRadioButton);

                if (normalButton.getId() == i) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (satelliteButton.getId() == i) {
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (terrainButton.getId() == i) {
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else if (hybridButton.getId() == i) {
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    @Override
    public void onNewRouteCalculated(JSONObject item) {

        clearRoute();

        if(showRoute(item)){
            showRouteDialog();
        }
        else {
            clearRoute();
        }

    }

    public boolean showRoute(JSONObject item){

        try{

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()) , defaultZoomLevel));
            followLocationEnabled = true;

            JSONObject data = item.getJSONObject("data");
            JSONObject tag = data.getJSONObject("tag");

            Route route = new Route(data.getDouble("distance"), data.getString("departureTime"), data.getString("arrivalTime"), data.getDouble("duration"));

            JSONArray sections = data.getJSONArray("sections");

            for(int i = 0; i<sections.length();i++){
                Section section = Section.fromJSON(sections.getJSONObject(i));
                route.addSection(section);

            }

            destination = new Destination(route);
            ArrayList<Section> secs = destination.getRoute().getSections();
            LatLng destinationLatLng = secs.get(secs.size()-1).getEndPoint();
            Marker destinationMaker = map.addMarker(new MarkerOptions().position(destinationLatLng).icon(BitmapDescriptorFactory.defaultMarker(AppSettings.getInstance().getDestinationMarkerColor(getContext()))));
            destinationMaker.setTag(ItemTag.fromJSON(tag));
            destination.setMarker(destinationMaker);
            destination.getRoute().setDestinationName(tag.getString("name"));
            destination.getRoute().setDestinationImage(tag.getString("image"));

            for(Section section: route.getSections()){
                section.setPolyline(map.addPolyline(section.getSectionPolylineOptions()));
            }

            return true;

        }catch (JSONException e){
            e.printStackTrace();
        }

        return false;
    }

    public void showRouteDialog(){

        RelativeLayout routeSummaryLayout = (RelativeLayout) getActivity().findViewById(R.id.homeRouteSummaryLayout);

        routeSummaryView = LayoutInflater.from(getContext()).inflate(R.layout.route_info_card_view, null, false);
        routeSummaryLayout.addView(routeSummaryView);

        TextView destinationName = (TextView) routeSummaryView.findViewById(R.id.routeSummaryDestinationName);
        TextView destinationTime = (TextView) routeSummaryView.findViewById(R.id.routeSummaryDestinationTime);
        TextView destinationDistance = (TextView) routeSummaryView.findViewById(R.id.routeSummaryDestinationDistance);
        ImageView destinationImage = (ImageView) routeSummaryView.findViewById(R.id.routeSummaryDestinationImage);

        ImageButton cancelRouteButton = (ImageButton) routeSummaryView.findViewById(R.id.routeSummaryDestinationCancelButton);
        final ImageButton showRouteDetailsButton = (ImageButton) routeSummaryView.findViewById(R.id.routeSummaryShowDetailsButton);

        cancelRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRoute(view);
            }
        });

        showRouteDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRouteDetails(view);
            }
        });

        new DownloadImageTask(destinationImage).execute(destination.getRoute().getDestinationImage());

        destinationName.setText(destination.getRoute().getDestinationName());

        String timeText = "Time: "+destination.getRoute().getDuration()+" Mins.";
        destinationTime.setText(timeText);

        String distanceText = "Distance: "+((float)Math.round(destination.getRoute().getDistance() * 100) / 100)+" Km.";
        destinationDistance.setText(distanceText);

    }

    public void cancelRoute(View view){

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Cancel route").setMessage("Do you want to cancel route?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                clearRoute();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            }
        });
        dialog.show();
    }

    public void showRouteDetails(View view){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.route_details_dialog);

        LinearLayout sectionsLinearLayout = (LinearLayout) dialog.findViewById(R.id.routeSections);

        for (Section section : destination.getRoute().getSections()) {

            View sectionDetails = LayoutInflater.from(getContext()).inflate(R.layout.section_details, null, false);
            TextView sectionTypeTitle = (TextView) sectionDetails.findViewById(R.id.routeSectionType);
            sectionTypeTitle.setText(section.getType());

            LinearLayout sectionStepsDetail = (LinearLayout) sectionDetails.findViewById(R.id.sectionStepsDetail);

            for (RoutePoint routePoint : section.getRoute()) {

                View step = LayoutInflater.from(getContext()).inflate(R.layout.route_details_step, null, false);

                TextView stepDescription = (TextView) step.findViewById(R.id.routeStepDescription);

                String description = routePoint.getDescription();

                if (!description.equals("")) {
                    description = description + " (" + Math.round(routePoint.getDuration()) + " mins.)";
                    stepDescription.setText(description);
                    sectionStepsDetail.addView(step);
                }
            }

            sectionsLinearLayout.addView(sectionDetails);
        }

        Button closeRouteDetailsButton =  (Button) dialog.findViewById(R.id.closeRouteDetailsButton);
        closeRouteDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void clearRoute(){

        if(destination != null) {
            for (Section section : destination.getRoute().getSections()) {
                if(section.getPolyline()!=null) {
                    section.getPolyline().remove();
                }
            }
            destination.getMarker().remove();
        }

        if(routeSummaryView!=null){
            RelativeLayout routeSummaryLayout = (RelativeLayout) getActivity().findViewById(R.id.homeRouteSummaryLayout);
            routeSummaryLayout.removeView(routeSummaryView);
        }
    }

    @Override
    public void onSearchFavouriteItem(JSONObject item) {

        if(searchView!=null){
            try {
                searchView.setQuery(item.getString("name"), true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
