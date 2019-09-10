package Utils;

public class ServerDataUtils {

    public static final String SERVER_URL = "https://instats.es";
    public static final String SEARCH_CLOSE_ITEMS_URL = "https://us-central1-madridview-236702.cloudfunctions.net/SearchCloseItems";
    public static final String SEARCH_ITEM_TAG_URL = "https://us-central1-madridview-236702.cloudfunctions.net/SearchItemTag";
    public static final String SEARCH_ITEM_URL = "https://us-central1-madridview-236702.cloudfunctions.net/SearchItem";
    public static final String GET_FAVOURITE_ITEMS_URL = "https://us-central1-madridview-236702.cloudfunctions.net/GetFavouriteItems";

    public static final String EMT_LOGIN_URL = "https://openapi.emtmadrid.es/v1/mobilitylabs/user/login/";
    public static final String EMT_TRAVEL_PLAN_URL = "https://openapi.emtmadrid.es/v1/transport/busemtmad/travelplan/";
    public static final int EMT_DEFAULT_TIMEOUT = 15000;


    public static final int INTERNAL_ERROR = 1;
    public static final int USER_NOT_FOUND_ERROR = 2;
    public static final int INCORRECT_PASSWORD = 3;
    public static final int USERNAME_ALREADY_EXISTS = 4;
    public static final int MAIL_ALREADY_EXISTS = 5;
    public static final int DIFFERENT_PASSWORDS_ERROR = 6;
    public static final int MAIL_NOT_FOUND_ERROR = 7;
}
