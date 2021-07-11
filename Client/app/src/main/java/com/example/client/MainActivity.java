package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.sql.ClientInfoStatus;

public class MainActivity extends AppCompatActivity {

    private ImageView screen;
    private static Network client ;
    private static boolean connection = false;

    private float px, py;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen = (ImageView) findViewById(R.id.touchPad);
        Button left = (Button) findViewById(R.id.L);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.Send("Left\n");
            }
        });
        Button right = (Button) findViewById(R.id.R);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.Send("Right\n");
            }
        });

        if(!connection)
        {
            client =  new Network(screen, this);
            connection = true;
        }

        screen.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {

                float x = e.getX();
                float y = e.getY();
                switch (e.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        float dx = x-px;
                        float dy = y-py;
                        String message = dx +"&&"+ dy+"\n";
                        Log.d("Usum", "input recived");
                        client.Send(message);
                        break;
                }
                px = x;
                py = y;

                return true;
            }
        });



    }



}