package FavouriteItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juan.madridview.HomeActivity;
import com.example.juan.madridview.MainActivity;
import com.example.juan.madridview.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import HttpHandler.DownloadImageTask;
import HttpHandler.HttpHandler;
import HttpHandler.VolleyCallback;
import Item.ItemTag;
import PubSub.Publisher;
import Services.LocationService;
import User.User;
import Utils.AppUtils;
import Utils.ServerDataUtils;

public class FavouriteItemAdapter extends RecyclerView.Adapter<FavouriteItemAdapter.FavouriteItemViewHolder> {

    private Context context;
    private ArrayList<ItemTag> favouriteItems;

    public FavouriteItemAdapter(ArrayList<ItemTag> favouriteItems, Context context){
        this.favouriteItems = favouriteItems;
        this.context = context;
    }

    @Override
    public FavouriteItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item_card_view, null, false);
        return new FavouriteItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteItemViewHolder holder, int position) {
        holder.setFavouriteItemInfo(favouriteItems.get(position));
    }

    @Override
    public int getItemCount() {
        return favouriteItems.size();
    }

    public class FavouriteItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout favouriteItemLayout;
        ItemTag tag;
        private final static String TAG = "FavouriteItemViewHolder";


        public FavouriteItemViewHolder(View itemView) {
            super(itemView);
            favouriteItemLayout = (LinearLayout) itemView.findViewById(R.id.favouriteItemLayout);
        }

        public void setFavouriteItemInfo(final ItemTag tag) {

            ImageView itemImage = (ImageView) favouriteItemLayout.findViewById(R.id.favouriteItemImage);
            TextView itemName = (TextView) favouriteItemLayout.findViewById(R.id.favouriteItemName);
            TextView itemType = (TextView) favouriteItemLayout.findViewById(R.id.favouriteItemType);
            ImageButton removeButton = (ImageButton) favouriteItemLayout.findViewById(R.id.favouriteRemoveButton);
            ImageButton showButton = (ImageButton) favouriteItemLayout.findViewById(R.id.favouriteShowInMapButton);

            new DownloadImageTask(itemImage).execute(tag.getImage());
            itemName.setText(tag.getName());
            itemType.setText(tag.getType());

            Log.d(TAG, "Creado");

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.cautionText)).setMessage(context.getResources().getString(R.string.removeFromFavouritesDialogMessage));
                    builder.setPositiveButton(context.getResources().getString(R.string.positiveAnswer), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            HashMap<String, String> parameters = new HashMap<>();

                            User user = User.getInstance();
                            String userName = user.getUserName(context);

                            if(userName==null){
                                Toast.makeText(context, R.string.internalError, Toast.LENGTH_LONG).show();
                            }
                            else{

                                parameters.put("userName", userName);
                                parameters.put("name", tag.getName());
                                parameters.put("type", tag.getType());
                                parameters.put("image", tag.getImage());

                                HttpHandler.post(context, ServerDataUtils.SERVER_URL + "/pages/php/removeFavouriteItem.php", parameters, new VolleyCallback() {
                                    @Override
                                    public void onSuccessResponse(JSONObject response) {

                                        Log.d(TAG, response.toString());

                                        try {
                                            if(response.getInt("success")==1){

                                                Toast.makeText(context, response.getString("description"), Toast.LENGTH_LONG).show();

                                                int pos = getAdapterPosition();
                                                favouriteItems.remove(pos);
                                                notifyItemRemoved(pos);
                                                notifyItemRangeChanged(pos, favouriteItems.size());
                                            }
                                            else{
                                                Toast.makeText(context, response.getString("description"), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, R.string.internalError, Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailResponse(String error) {
                                    }
                                });
                            }
                        }
                    });

                    builder.setNegativeButton(context.getResources().getString(R.string.negativeAnswer), null);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }
            });

            showButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Publisher.publish(context, AppUtils.ON_SEARCH_FAVOURITE_ITEM, tag.toJSON());

                }
            });
        }
    }
}
