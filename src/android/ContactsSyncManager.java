package com.rubenfig.plugin.contacts;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;


public class ContactsSyncManager extends CordovaPlugin {

    private CalendarAccessor calendarAccessor;
    private EventAccessor eventAccessor;

    private CallbackContext callbackContext; // The callback context from which we were invoked.
    private JSONArray executeArgs;

    private static final String LOG_TAG = "Calendar Query";

    public static final int UNKNOWN_ERROR = 0;
    public static final int INVALID_ARGUMENT_ERROR = 1;
    public static final int TIMEOUT_ERROR = 2;
    public static final int PENDING_OPERATION_ERROR = 3;
    public static final int IO_ERROR = 4;
    public static final int NOT_SUPPORTED_ERROR = 5;
    public static final int PERMISSION_DENIED_ERROR = 20;

    /**
     * Constructor.
     */
    public ContactsSyncManager() {
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback context used when calling back into JavaScript.
     * @return                  True if the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        this.callbackContext = callbackContext;
        this.executeArgs = args;


        // Events methods.
        if (action.equals("init")) {
            final String accountType = args.getString(0);
            final String accountName = args.getString(1);
            JSONArray calendars = new JSONArray();
            Log.i("Llego aca");
            callbackContext.success(calendars);

        } else {
            return false;
        }
        return true;
    }


}
