package com.rubenfig.plugin.contacts;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;

import static android.provider.ContactsContract.AUTHORITY;
import static com.rubenfig.plugin.contacts.AccountGeneral.*;


/**
 * The Authenticator activity.
 *
 * Called by the Authenticator and in charge of identifing the user.
 *
 * It sends back to the Authenticator the result.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    private AccountManager mAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("AuthenticatorActivity");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String accountType = ACCOUNT_TYPE;
        String accountName = settings.getString(ACCOUNT_NAME, "");

        Intent res = new Intent();
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        res.putExtra(AccountManager.KEY_AUTHTOKEN, AccountGeneral.ACCOUNT_TOKEN);
        Account account = new Account(accountName, accountType);
        mAccountManager = AccountManager.get(this);
        mAccountManager.addAccountExplicitly(account, null, null);
        //mAccountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, AccountGeneral.ACCOUNT_TOKEN);
        ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        ContentResolver.addPeriodicSync(account, AUTHORITY,
                Bundle.EMPTY, 3600);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
        setAccountAuthenticatorResult(res.getExtras());
        setResult(RESULT_OK, res);
        finish();
    }


}
