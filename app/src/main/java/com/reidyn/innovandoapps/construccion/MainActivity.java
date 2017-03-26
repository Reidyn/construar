package com.reidyn.innovandoapps.construccion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reidyn.innovandoapps.construccion.Utils.PreferencesManager;

public class MainActivity extends AppCompatActivity {

    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("construar");

        startActivity(new Intent(getApplicationContext(),VistaPrincipalActivity.class));

        preferencesManager = PreferencesManager.getInstance(getApplicationContext());
        if(preferencesManager.getTokenFirebase().equals("")){
            preferencesManager.setTokenFirebase(FirebaseInstanceId.getInstance().getToken());
        }

        finish();
    }
}
