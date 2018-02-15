package com.xte.xte;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class DialogOnlineGuys extends AppCompatActivity {

    private TextView Txt_OnlineGus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_online_guys);

        Txt_OnlineGus=(TextView) findViewById(R.id.Txt_OnlineGus);

        int lenght = App.guys.size();

        for(int i=0;i<lenght;i++){

            Txt_OnlineGus.setText(Txt_OnlineGus.getText()+"\n"+App.guys.get(i).toString());

        }



    }
}
