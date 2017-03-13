package com.reidyn.innovandoapps.construccion.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by windows 8.1 on 12/03/2017.
 */

public class PreferencesManager {

    private static PreferencesManager INSTANCE = null;
    private SharedPreferences preferences;

    private final String NAME_FILE_PREFS = "construar";
    private final String KEY_TOKEN_FIREBASE = "token_firebase";

    private PreferencesManager(Context context){
        preferences=context.getSharedPreferences(NAME_FILE_PREFS,Context.MODE_PRIVATE);
    }

    public synchronized static void createInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new PreferencesManager(context);
        }
    }

    public static PreferencesManager getInstance(Context context){
        if(INSTANCE == null){
            createInstance(context);
        }
        return INSTANCE;
    }

    public void setTokenFirebase(String token){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN_FIREBASE, token);
        editor.commit();
    }

    public String getTokenFirebase(){
        return preferences.getString(KEY_TOKEN_FIREBASE,"");
    }
}
