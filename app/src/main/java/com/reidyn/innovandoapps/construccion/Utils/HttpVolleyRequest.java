package com.reidyn.innovandoapps.construccion.Utils;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
/**
 * @autor : Marcos Ramirez
 * Clase Singleton que realiza los request a un webservice usando libreria Volley
 */
public class HttpVolleyRequest {

    /**Instancia singleton*/
    private static HttpVolleyRequest mInstance;
    /**Objeto request*/
    private RequestQueue mRequestQueue;
    /**timeout del login*/
    private final int TIME_OUT_LOGIN = 60000;
    private Context context;

    /**
     * Constructor. Inicializa las variables globales
     * @param context Context del Activity padre
     */
    public HttpVolleyRequest(Context context){
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Recuperar instancia de la clase
     * @param context
     * @return Instancia HttpVolleyRequest
     */
    public static HttpVolleyRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpVolleyRequest(context);
        }
        return mInstance;
    }

    /**
     * Registra firebasetoken en el servidor de mensaje
     * @param url           URL para request
     * @param parametros    parametros del request
     */
    public void registrarTokenPush(String url, final Map<String,String>parametros){
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
               // eventBus.post(new LoginResult(volleyError.toString()));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>param=new HashMap<>();
                param=parametros;
                return param;
            }
        };
        mRequestQueue.add(request);
    }
}
