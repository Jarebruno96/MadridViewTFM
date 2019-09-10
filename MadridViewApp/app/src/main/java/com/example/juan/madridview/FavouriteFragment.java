package com.example.juan.madridview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import FavouriteItem.FavouriteItemAdapter;
import HttpHandler.HttpHandler;
import HttpHandler.VolleyCallback;
import Item.ItemTag;
import ListenerInterfaces.OnFragmentInteractionListener;
import User.User;
import Utils.ServerDataUtils;

public class FavouriteFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    public static final String TAG = "FavouriteFragment";
    private View view;
    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.favouriteFragmentTitle));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState){
        this.view = view;
        loadFavouriteItems();
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
    public void onHiddenChanged(boolean hidden){

        if(!hidden){
            loadFavouriteItems();
        }
    }

    private void loadFavouriteItems(){
        try {

            JSONObject parameters = new JSONObject();
            User user = User.getInstance();

            String userName =  user.getUserName(getContext());

            if(userName==null){
                Toast.makeText(getContext(), getResources().getString(R.string.unknownError),Toast.LENGTH_LONG).show();
            }
            else {

                parameters.put("userName", userName);

                HttpHandler.post(getContext(), ServerDataUtils.GET_FAVOURITE_ITEMS_URL, parameters, new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {

                        ArrayList<ItemTag> tags = new ArrayList<>();
                        TextView noFoundItems = (TextView) view.findViewById(R.id.favouriteItemsNotFoundText);

                        try {
                            Log.d(TAG, response.toString());
                            if (response.getInt("success") == 1) {

                                JSONArray t = response.getJSONArray("favourites");

                                for (int i = 0; i < t.length(); i++) {
                                    tags.add(new ItemTag(t.getJSONObject(i).getString("name"), t.getJSONObject(i).getString("type"), t.getJSONObject(i).getString("image")));
                                }

                                if (tags.size() == 0) {

                                    noFoundItems.setVisibility(View.VISIBLE);
                                } else {
                                    noFoundItems.setVisibility(View.GONE);

                                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.favouriteItemRecyclerView);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                    FavouriteItemAdapter adapter = new FavouriteItemAdapter(tags, getContext());
                                    recyclerView.setAdapter(adapter);
                                }

                            } else {
                                noFoundItems.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "Favoritos cargados");

                    }

                    @Override
                    public void onFailResponse(String error) {
                        Log.d(TAG, "Can not get favourite items");
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
