package Item;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemTag implements Parcelable {

    private String type;
    private String name;
    private String image;


    public ItemTag(String name, String type, String image){
        this.name = name;
        this.type = type;
        this.image = image;
    }

    private ItemTag(Parcel in) {
        type = in.readString();
        name = in.readString();
        image = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", this.name);
            jsonObject.put("type", this.type);
            jsonObject.put("image", this.image);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static ItemTag fromJSON(JSONObject tag){
        try {
            return new ItemTag(tag.getString("name"), tag.getString("type"), tag.getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(this.type);
        parcel.writeString(this.name);
        parcel.writeString(this.image);
    }

    public static final Creator<ItemTag> CREATOR = new Creator<ItemTag>() {
        @Override
        public ItemTag createFromParcel(Parcel in) {
            return new ItemTag(in);
        }

        @Override
        public ItemTag[] newArray(int size) {
            return new ItemTag[size];
        }
    };
}
