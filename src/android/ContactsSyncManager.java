package com.rubenfig.plugin.contacts;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.rubenfig.plugin.contacts.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;


public class ContactsSyncManager extends CordovaPlugin {

    private CallbackContext callbackContext; // The callback context from which we were invoked.
    private JSONArray executeArgs;
    private Activity context;
    AccountManager manager = null;

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
        this.context=this.cordova.getActivity();
        if(manager == null)
        {
            manager = AccountManager.get(cordova.getActivity());
        }
        // Events methods.
        if (action.equals("init")) {

            JSONArray calendars = new JSONArray();
            manager.addAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this.context, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        Bundle bnd = future.getResult();
                        Log.i("Account was created");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            ContactsManager.initiateContacts(context);
            callbackContext.success(calendars);

        } else if (action.equals("getContactFromUri")) {
             final String uri = args.getString(0);
             callbackContext.success(ContactsManager.getContactFromUri(context, uri));

        }else {
            return false;
        }
        return true;
    }




}
