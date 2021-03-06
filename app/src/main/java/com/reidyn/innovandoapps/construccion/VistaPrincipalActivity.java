package com.reidyn.innovandoapps.construccion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.reidyn.innovandoapps.construccion.Utils.HttpVolleyRequest;
import com.reidyn.innovandoapps.construccion.Utils.PreferencesManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by windows 8.1 on 24/01/2017.
 */

public class VistaPrincipalActivity extends FragmentActivity {

    private WebView webView;
    private ProgressBar progresbar;
    private String urlload="";
    private PreferencesManager preferencesManager;

    private final String URL = "http://www.mobiup.com:5600/construar/accesochat.php";
    private final String URL_REGISTER_TOKEN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vista_principal);

        preferencesManager = PreferencesManager.getInstance(getApplicationContext());
        progresbar=(ProgressBar)findViewById(R.id.progressbar);
        webView=(WebView)findViewById(R.id.webconstruar);

        EnableJavaScript();
        webView.loadUrl(URL);
        cargarPagina();
    }

    //Habilita el javaScript
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void EnableJavaScript(){
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");//Interface Captura
        webView.addJavascriptInterface(new WebAppInterface(this), "take");
        webView.addJavascriptInterface(new WebAppInterface(this), "showAndroidUser");
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private void cargarPagina(){
        webView.setWebViewClient(new WebViewClient(){
            @Override//En caso de error
            public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VistaPrincipalActivity.this);
                builder.setMessage(description).setPositiveButton("ACEPTAR", null)
                        .setTitle("Sin Conexion");
                builder.show();
            }

            @Override///Url Actual
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                urlload=url;
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override//Definicion del progress de la carga
            public void onProgressChanged(WebView view, int newProgress) {
                progresbar.setProgress(0);
                progresbar.setVisibility(View.VISIBLE);
                VistaPrincipalActivity.this.setProgress(newProgress*1000);
                progresbar.incrementProgressBy(newProgress);
                if(newProgress==100){
                    progresbar.setVisibility(View.GONE);
                }
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                return true;
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {}

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
    }

    public class WebAppInterface{
        Context context;

        public WebAppInterface(Context context) {
            this.context=context;
        }

        @JavascriptInterface
        public void showToast(String idpedido,String cantidad) {
            Bundle bundle=new Bundle();
            bundle.putString("idpedido", idpedido);
            bundle.putString("cantidad", cantidad);
            Intent intent=new Intent(VistaPrincipalActivity.this,TakeImagenActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @JavascriptInterface
        public void showAndroidUser(String user) {
            Log.i("Usuario",user);
            String token = FirebaseInstanceId.getInstance().getToken();
            preferencesManager.setTokenFirebase(token);
            Map<String, String> parametros = new HashMap<>();
            parametros.put("usuario", user);
            parametros.put("firebasetoken", token);
            HttpVolleyRequest.getInstance(context).registrarTokenPush(URL_REGISTER_TOKEN,parametros);
        }
    }

    @Override
    public void onBackPressed() {
        //si el posible ir atras regresa sino lanza Alarte de Salida
        if(webView.canGoBack()){
            urlload="";
        	webView.goBack();
        }else{
            finish();
        }
    }
}
