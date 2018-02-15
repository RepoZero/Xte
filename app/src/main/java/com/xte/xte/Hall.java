package com.xte.xte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.socketio.client.IO;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import static com.xte.xte.App.Server_Address;

public class Hall extends AppCompatActivity {

    private ListView Lst_Hall;
    private TextView Txt_NumberOfOnlineGus;
    private TextView Txt_FullName;
    private Button Btn_Send;
    private Button Btn_GuysDialog;
    private EditText Edt_message;
    private ArrayList<Msg> listMessages;
    private int lengh;
    private MessagesListAdapter adapter;
    private int status = 2;
    private int messages_group = 2;



    private String sender;
    private String text;




    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://xte.ibben.org:7620");
        } catch (URISyntaxException e) {}
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hall);

//..............................................................................................................




        Lst_Hall = (ListView) findViewById(R.id.Lst_Hall);
        Txt_NumberOfOnlineGus = (TextView) findViewById(R.id.Txt_NumberOfOnlineGus);
        Txt_FullName = (TextView) findViewById(R.id.Txt_FullName);
        Btn_Send = (Button) findViewById(R.id.Btn_Send);
        Btn_GuysDialog = (Button) findViewById(R.id.Btn_GuysDialog);
        Edt_message = (EditText) findViewById(R.id.Edt_message);


//..............................................................................................................



        Txt_FullName.setText(App.full_name);

        Txt_NumberOfOnlineGus.setText(App.guys.size() + "");
        Lst_Hall.setDivider(null);


        listMessages = new ArrayList<Msg>();

        lengh = App.sender.size() - 1;


        Collections.reverse(App.text);
        Collections.reverse(App.sender);

        for (int i = 0; i <= lengh; i++) {

            if (App.sender.get(i).equals(App.full_name))
                listMessages.add(new Msg("", "", "", App.text.get(i).toString(), true, "", "", App.full_name));
            else
                listMessages.add(new Msg("", "", "", App.text.get(i).toString(), false, "", "", App.sender.get(i).toString()));
        }


        adapter = new MessagesListAdapter(this, listMessages);
        Lst_Hall.setAdapter(adapter);

        Lst_Hall.setSelection(lengh);


//..............................................................................................................



        Btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Edt_message.getText().toString())) {
                    return;
                }

                App.text.add(Edt_message.getText().toString());
                App.sender.add(App.full_name);


                listMessages.add(new Msg("", "", "", Edt_message.getText().toString(), true, "", "", App.full_name));


                adapter = new MessagesListAdapter(Hall.this, listMessages);
                Lst_Hall.setAdapter(adapter);

                lengh = App.sender.size() - 1;
                Lst_Hall.setSelection(lengh + 1);

                SendMessage();


            }
        });

//..............................................................................................................


        Btn_GuysDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hall.this,DialogOnlineGuys.class));
            }
        });


//..............................................................................................................




        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

            }

        }).on("message", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                Log.e("message Socket",args[0].toString());

                String s = args[0].toString();
                try {
                    SocketMessageToJson(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }

        }).on("guyOn", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                Log.e("guyOn Socket",args[0].toString());

                try {
                    SocketGuyIn(args[0].toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }).on("guyOff", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                Log.e("guyOff Socket",args[0].toString());
                try {
                    SocketGuyOff(args[0].toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

            }

        });
        mSocket.connect();

//..............................................................................................................


        Lst_Hall.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                 Log.e("Scroll Top :: ",""+listIsAtTop());

                if (listIsAtTop()){
                    GetBeforeMessages();
                }



            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


    }

//..............................................................................................................
//..............................................................................................................
//..............................................................................................................
//..............................................................................................................
//..............................................................................................................
//..............................................................................................................
//..............................................................................................................

    private boolean listIsAtTop()   {
        if(Lst_Hall.getChildCount() == 0) return true;
        return Lst_Hall.getChildAt(0).getTop() == 0;


    }



//..............................................................................................................

    public void GetBeforeMessages() {

        //Showing the progress dialog
//        final ProgressDialog loading = ProgressDialog.show(this, "Communicating with the server", "Please Wait", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Server_Address + "messages/"+messages_group,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
//                        loading.dismiss();
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
//                        loading.dismiss();

                        //Showing toast
//                        Toast.makeText(GetName.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new Hashtable<String, String>();




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

        if (status == 2) {

            Snackbar.make(Edt_message, "problem with Server Comminucation", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();

            return;

        }


        if (status == 4) {


            return;

        }


        JSONArray jArray_data = jsonObject.getJSONArray("data");




        for (int i = 0; i < jArray_data.length(); i++) {
            try {


                JSONObject json_data = jArray_data.getJSONObject(i);




//                Toast.makeText(this, json_data.getString("sender")+" :::: "+json_data.getString("text"), Toast.LENGTH_SHORT).show();

                App.sender.add(0, json_data.getString("sender"));
                App.text.add(0, json_data.getString("text"));









            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




            int sizelist = listMessages.size();
            listMessages.clear();


            lengh = App.sender.size() - 1;





            for (int s = 0; s <= lengh; s++) {

                if (App.sender.get(s).equals(App.full_name))
                    listMessages.add(0,new Msg("", "", "", App.text.get(s).toString(), true, "", "", App.full_name));
                else
                    listMessages.add(0,new Msg("", "", "", App.text.get(s).toString(), false, "", "", App.sender.get(s).toString()));
            }

        Collections.reverse(listMessages);


            adapter = new MessagesListAdapter(this, listMessages);
            Lst_Hall.setAdapter(adapter);

//            for(int s=0;s<App.sender.size();s++){
//
//                Log.e("Messages",App.sender.get(s)+" ::: "+App.text.get(s));
//            }





        Lst_Hall.invalidateViews();

//        Lst_Hall.setSelection(listMessages.size()-sizelist);
        Lst_Hall.setSelection(17);











            messages_group++;


        }










//..............................................................................................................

    public void SocketMessageToJson(String response) throws JSONException {



        JSONObject jsonObject = new JSONObject(response);

        status = jsonObject.getInt("status");



        JSONObject Jobject_data = jsonObject.getJSONObject("data");

         sender = Jobject_data.getString("sender");
         text = Jobject_data.getString("text");

        Log.e("Socket sender : ",sender);
        Log.e("Socket text : ",text);


        if(!sender.equals(App.full_name)) {


            App.text.add(text);
            App.sender.add(sender);

            runOnUiThread(new Runnable() {
                public void run() {


                    listMessages.add(new Msg("", "", "", text, false, "", "", sender));


                    adapter = new MessagesListAdapter(Hall.this, listMessages);
                    Lst_Hall.setAdapter(adapter);

                    lengh = App.sender.size() - 1;
                    Lst_Hall.setSelection(lengh + 1);

                    App.NotficationSound.start();


                }
            });



        }





    }

    public void SocketGuyOff(String  response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        status = jsonObject.getInt("status");



        JSONObject Jobject_data = jsonObject.getJSONObject("data");

        String fullname = Jobject_data.getString("fullname");

        int lenght = App.guys.size()-1;

        for(int i=0;i<lenght;i++){
            if(App.guys.get(i).equals(fullname)){
                App.guys.remove(i);
            }
        }

        runOnUiThread(new Runnable() {
            public void run() {
                Txt_NumberOfOnlineGus.setText(""+App.guys.size());

            }
        });






    }

    public void SocketGuyIn(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        status = jsonObject.getInt("status");



        JSONObject Jobject_data = jsonObject.getJSONObject("data");

        String fullname = Jobject_data.getString("fullname");

        App.guys.add(fullname);

        runOnUiThread(new Runnable() {
            public void run() {
                Txt_NumberOfOnlineGus.setText(""+App.guys.size());

            }
        });


    }


    public void SendMessage() {


//        final ProgressDialog loading = ProgressDialog.show(this, "Communicating with the server", "Please Wait", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Address + "messages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String AnswerRequest) {
                        //Disimissing the progresas dialog
//                        loading.dismiss();
                        Edt_message.setText("");
                        //Showing toast message of the response


                        Log.e("Gettt", AnswerRequest);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
//                        loading.dismiss();


                        //Showing toast
//                        Toast.makeText(GetName.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new Hashtable<String, String>();


                params.put("text", Edt_message.getText().toString());
                params.put("sender", App.full_name);


                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);


    }
//..............................................................................................................

    public void Logout() {


        Log.e("Logout","Run");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Address + "logout",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String AnswerRequest) {
                        //Disimissing the progresas dialog


                        //Showing toast message of the response


                        Log.e("Gettt", AnswerRequest);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog


                        //Showing toast
//                        Toast.makeText(GetName.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new Hashtable<String, String>();


                params.put("fullname", App.full_name);

                Log.e("Logout","Send name");


                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);


    }

//..............................................................................................................

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Onrestart","Run");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server_Address + "login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog

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


                        //Showing toast
//                        Toast.makeText(GetName.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new Hashtable<String, String>();

                params.put("fullname",App.full_name);


                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }


//.............................................................................................................



    @Override
    protected void onPause() {
        super.onPause();

    }
//..............................................................................................................

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Onstop","Run");
        Logout();
    }


//..............................................................................................................

    @Override
    protected void onDestroy() {
        super.onDestroy();


        Logout();
        mSocket.disconnect();

    }


//..............................................................................................................


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Logout();

                        mSocket.disconnect();

                        App.guys.clear();
                        App.sender.clear();
                        App.text.clear();
                        finish();
//                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setNeutralButton("Login with another name",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Logout();

                        mSocket.disconnect();

                        App.guys.clear();
                        App.sender.clear();
                        App.text.clear();

                        SharedPreferences shared = getSharedPreferences("Prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("FullName","");
                        editor.apply();

                        startActivity(new Intent(Hall.this,GetName.class));

                        finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}
