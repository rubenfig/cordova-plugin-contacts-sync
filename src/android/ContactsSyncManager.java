package com.rubenfig.plugin.contacts;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.rubenfig.plugin.contacts.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import static com.rubenfig.plugin.contacts.AccountGeneral.*;

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

            SharedPreferences settings = this.context.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SYNC_URL, args.getString(0));
            editor.putString(ACCOUNT_NAME, args.getString(1));
            editor.putString(APP_NAME, args.getString(2));
            editor.putString(MESSAGE, args.getString(3));
            editor.commit();
            JSONObject response = new JSONObject();
            try {

                manager.addAccount(ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this.context, future -> {
                    try {
                        Bundle bnd = future.getResult();
                        Log.i("Account was created");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, null);
            }catch (Exception e){
                response.put("error", true);
                response.put("mensaje", "Ha ocurrido un error al crear cuenta.");
                callbackContext.success(response);
            }
            try{
                ContactsManager.initiateContacts(context);
                response.put("error", false);
                response.put("mensaje", "Se creó la cuenta");
                callbackContext.success(response);
            }catch (Exception e){
                response.put("error", true);
                response.put("mensaje", "Ha ocurrido un error al inicializar contactos.");
                callbackContext.success(response);
            }

        } else if (action.equals("getContactFromUri")) {
            final String uri = args.getString(0);
            callbackContext.success(ContactsManager.getContactFromUri(context, uri));

        } else {
            return false;
        }

        return true;
    }




}
