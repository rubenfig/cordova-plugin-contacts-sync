package com.rubenfig.plugin.contacts;


import java.util.ArrayList;
import java.util.List;


import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.rubenfig.plugin.contacts.AccountGeneral.*;


public class ContactsManager {
	private static String MIMETYPE = "vnd.android.cursor.item/com.plugin.profile";

	public static void addContact(Context context, ContactClass contact) {
		Log.i("Agregando contacto nuevo");
	}

	public static void initiateContacts(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        String url = settings.getString(SYNC_URL, "");
        String accountType = ACCOUNT_TYPE;
        String accountName = settings.getString(ACCOUNT_NAME, "");
        String appName = settings.getString(APP_NAME, "");
        String message = settings.getString(MESSAGE, "");

        ContentResolver resolver = context.getContentResolver();
  //		Cursor cur2 = resolver.query(RawContacts.CONTENT_URI, null,null, null,
  //				Data.CONTACT_ID);
  //		if ((cur2 != null ? cur2.getCount() : 0) > 0) {
  //
  //			while (cur2 != null && cur2.moveToNext()) {
  //				Log.i(cur2.getString(
  //						cur2.getColumnIndex(RawContacts._ID))+ "-" +cur2.getString(cur2.getColumnIndex(
  //						RawContacts.ACCOUNT_NAME))+ "-" +cur2.getString(cur2.getColumnIndex(
  //						RawContacts.ACCOUNT_TYPE))+ "-" +cur2.getString(cur2.getColumnIndex(
  //						RawContacts.DELETED)));
  //
  //
  //			}
  //
  //		}
  //		cur2.close();
  		resolver.delete(addCallerIsSyncAdapterParameter(RawContacts.CONTENT_URI,true), RawContacts.ACCOUNT_TYPE + " = ? AND " + RawContacts.ACCOUNT_NAME + " = ?", new String[] { accountType , accountName});

  		Cursor cur = resolver.query(Data.CONTENT_URI,
  				null,
  				Data.HAS_PHONE_NUMBER + "!=0 AND (" + Data.MIMETYPE + "=?)",
  				new String[]{ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
  				Data.CONTACT_ID);
  		ArrayList<ContactClass> list = new ArrayList<>();
  		JSONArray contactsRequest = new JSONArray();
  		String lastNumber = null;
  		if ((cur != null ? cur.getCount() : 0) > 0) {

  			while (cur != null && cur.moveToNext()) {
  				String id = cur.getString(
  						cur.getColumnIndex(Data.RAW_CONTACT_ID));
  				String name = cur.getString(cur.getColumnIndex(
  						Data.DISPLAY_NAME));
  				String phoneNo = cur.getString(cur.getColumnIndex(
  						ContactsContract.CommonDataKinds.Phone.NUMBER));
  				if(phoneNo != null && phoneNo.length() >= 8) {
					if (lastNumber == null || !phoneNo.equals(lastNumber)) {
						ContactClass temp = new ContactClass(Long.parseLong(id), name, phoneNo);
						list.add(temp);
						contactsRequest.put(phoneNo.replaceAll("\\s","").replaceAll("\\+",""));
						lastNumber = phoneNo.replaceAll("\\s","");
					}
				}
  			}
  		}

  		if(cur!=null){
  			cur.close();
  		}
		RequestQueue queue = Volley.newRequestQueue(context);
		JSONObject params = new JSONObject();
		try {
			params.put("contactos", contactsRequest);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest postRequest = new JsonObjectRequest(url, params,
                response -> {
                    try {
                        JSONArray contactosFinales = response.getJSONArray("contactos");
                        ArrayList<ContactClass> listaFinal = new ArrayList<>();
                        for (int i = 0; i< contactosFinales.length(); i++){
                            for (int j = 0; j < list.size(); j++){
                                String contacto1 = contactosFinales.getString(i);
                                String contacto2 = list.get(j).phone.replaceAll("\\s","");

                                if (contacto1.substring(Math.max(0, contacto1.length() - 8)).equals(contacto2.substring(Math.max(0, contacto2.length() - 8)))){
                                    listaFinal.add(list.get(j));
                                }
                            }
                        }

                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                        int j =0;
                        for(int i = 0; i < listaFinal.size(); i++) {
                            ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(RawContacts.CONTENT_URI, true))
                                    .withValue(RawContacts.ACCOUNT_NAME, accountName)
                                    .withValue(RawContacts.ACCOUNT_TYPE, accountType)
                                    //					.withValue(ContactsContract.Settings.UNGROUPED_VISIBLE, 1)

                                    //.withValue(RawContacts.SOURCE_ID, 12345)
                                    //.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DISABLED)
                                    .build());


                            ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
                                    .withValueBackReference(Data.RAW_CONTACT_ID, (3*j))
                                    .withValue(Data.MIMETYPE, MIMETYPE)
                                    .withValue(Data.DATA1, listaFinal.get(i).phone)
                                    .withValue(Data.DATA2, appName)
                                    .withValue(Data.DATA3, message + listaFinal.get(i).name)
                                    .build());
                            ops.add(ContentProviderOperation.newUpdate(addCallerIsSyncAdapterParameter(ContactsContract.AggregationExceptions.CONTENT_URI, true))
                                    .withValue(ContactsContract.AggregationExceptions.TYPE, ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER)
                                    .withValue(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1, listaFinal.get(i).id)
                                    .withValueBackReference(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2, (3*j))
                                    .build());
                            if (j == 100) {
                                try {
                                    resolver.applyBatch(ContactsContract.AUTHORITY, ops);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                j=0;
                                ops = new ArrayList<>();
                            }else {
                                j++;
                            }

                        }
                        try {
                            if (ops.size() != 0){
                                ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, ops);
                                if (results.length == 0)
                                    ;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> VolleyLog.e("Error: ", error.getMessage()));
		postRequest.setRetryPolicy(new DefaultRetryPolicy(
				10000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(postRequest);








  	}

	private static Uri addCallerIsSyncAdapterParameter(Uri uri, boolean isSyncOperation) {
        if (isSyncOperation) {
            return uri.buildUpon()
                    .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                    .build();
        }
        return uri;
    }

	public static List<ContactClass> getMyContacts() {
		return null;
	}

	public static JSONObject getContactFromUri(Context context, String dataString) {
        Uri uri = Uri.parse(dataString);
//        Cursor cur2 = context.getContentResolver().query(uri, null,null, null,
//        				null);
//        		String tableString = "";
//		if ((cur2 != null ? cur2.getCount() : 0) > 0) {
//			String[] columnNames = cur2.getColumnNames();
//
//			while (cur2 != null && cur2.moveToNext()) {
//				for (String nameC: columnNames) {
//					tableString += String.format("%s: %s\n", nameC,
//							cur2.getString(cur2.getColumnIndex(nameC)));
//				}
//				tableString += "\n";
//
//			}
//		}
//		Log.i(tableString);
//		cur2.close();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);

        return Tools.row2JSON(cursor, projection);
    }


	public static void updateMyContact(Context context, String name) {
		int id = -1;
		 Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI, new String[] { Data.RAW_CONTACT_ID, Data.DISPLAY_NAME, Data.MIMETYPE, Data.CONTACT_ID },
				 		 StructuredName.DISPLAY_NAME + "= ?",
				 		 new String[] {name}, null);
		 if (cursor != null && cursor.moveToFirst()) {
			 do {
				 id = cursor.getInt(0);
				 Log.i(cursor.getString(0));
				 Log.i(cursor.getString(1));
				 Log.i(cursor.getString(2));
				 Log.i(cursor.getString(3));

			 } while (cursor.moveToNext());
		 }
		 if (id != -1) {
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValue(Data.RAW_CONTACT_ID, id)
				.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
				.withValue(Email.DATA, "sample")
				.build());

			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValue(Data.RAW_CONTACT_ID, id)
				.withValue(Data.MIMETYPE, MIMETYPE)
				.withValue(Data.DATA1, "profile")
				.withValue(Data.DATA2, "profile")
				.withValue(Data.DATA3, "profile")
				.build());

			try {
				context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 else {
			 Log.i("id not found");
		 }


	}

}
