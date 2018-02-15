package com.xte.xte;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import static com.xte.xte.App.Server_Address;

public class GetName extends AppCompatActivity {


    private EditText Edt_Name;
    private TextView xte_text;
    private Button Btn_Login;

    private int status;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_get_name);

        Edt_Name = (EditText) findViewById(R.id.Edt_Name);
        Btn_Login = (Button) findViewById(R.id.Btn_Login);
        xte_text = (TextView) findViewById(R.id.xte_text);


//
        Edt_Name.setTypeface(App.roboto_reguler);
        Btn_Login.setTypeface(App.roboto_reguler);
        xte_text.setTypeface(App.segoe_thin);


        SharedPreferences shared = getSharedPreferences("Prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        String string_from_sp = shared.getString("FullName", "");

        if(!string_from_sp.equals("")){
            SendName();
        }

        Edt_Name.setText(string_from_sp);


        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(Edt_Name.getText().toString())){

                    Edt_Name.setError("Please insert fullname");

                    return;
                }



                SendName();







            }
        });
    }

    public void SendName() {

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Communicating with the server", "Please Wait", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Address + "login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response


                        Log.e("Gettt", s);

                        try {
                            showJSON(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
//                        Toast.makeText(GetName.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new Hashtable<String, String>();

                params.put("fullname", Edt_Name.getText().toString());


                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    private void showJSON(String response) throws JSONException {


        JSONObject jsonObject = new JSONObject(response);

        status = jsonObject.getInt("status");

        if(status==2){

            Edt_Name.setError("This Full Name has been entered by other users");
            return;

        }

        JSONObject Jobject_data = jsonObject.getJSONObject("data");
        JSONArray jArray_messages = Jobject_data.getJSONArray("messages");
        JSONArray jArray_guys = Jobject_data.getJSONArray("guys");





        for (int i = 0; i < jArray_messages.length(); i++) {
            try {


                JSONObject json_data = jArray_messages.getJSONObject(i);



                App.sender.add(i, json_data.getString("sender"));
                App.text.add(i, json_data.getString("text"));






            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        for (int i = 0; i < jArray_guys.length(); i++) {
            try {


                JSONObject json_data = jArray_guys.getJSONObject(i);



                App.guys.add(i, json_data.getString("fullname"));




            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

        App.full_name =Edt_Name.getText().toString();


        SharedPreferences shared = getSharedPreferences("Prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("FullName",App.full_name);
        editor.apply();


        startActivity(new Intent(GetName.this, Hall.class));
        finish();








    }


}
