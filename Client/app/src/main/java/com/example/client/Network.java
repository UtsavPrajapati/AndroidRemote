package com.example.client;


import java.io.*;
import java.net.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import android.app.Activity;
import android.content.Context;

import android.os.AsyncTask;

public class Network {

    private String ip = "192.168.18.3";
    private int port = 6666;
    private static Socket client;
    public static ObjectInputStream reader;
    private static DataOutputStream writer;
    private Context context;
    private static ImageView imageView;



    public static boolean connected = false;
    public static boolean error = false;
    Network(ImageView s, Context con)
    {
        context = con;
        imageView = s;
        Log.d("Usu","initialized");
        new Connect().execute();


    }

    private class Connect extends AsyncTask<Void,Void,Void>
    {
        Connect()
        {
            super();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                InetAddress idd = InetAddress.getByName(ip);
                Log.d("Usu","waiting for connection");
                client = new Socket(idd,port);
                Log.d("Usu","connected");
                connected = true;

                reader = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
                writer = new DataOutputStream(client.getOutputStream());
                updateImg();
            }catch(Exception e){
                error = true;
            }
            return null;
        }
    }

    private class SendCoords extends AsyncTask<Void,Void,Void>
    {
        String message;
        SendCoords(String a)
        {
            super();
            Log.d("Usum", "caleed");
            message = a;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Log.d("Usum", "sending output");
                writer.flush();
                writer.writeBytes(message);
                Log.d("Usum", "sent output");
                //updateImg();
            }catch(Exception e)
            {
                error = true;
            }
            return null;
        }
    }


    private class UpdateImage extends AsyncTask<Void,Void,Void>
    {


        UpdateImage()
        {
            super();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            while(!error) {
                try {

                    Log.d("Usut", "reading image");
                    byte[] byteImage = (byte[]) reader.readObject();
                    Log.d("Usut", "image received");
                    Bitmap img = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

                    bla(img);
                } catch (Exception e) {
                    Log.d("Usut", "error");
                    error = true;
                }
            }

            return null;
        }

    }

    /*class ImageUpdater extends Thread{
         public void run()
         {
             while(true)
             {
                 try {

                     Log.d("Usut", "reading image");
                     byte[] byteImage = (byte[]) reader.readObject();
                     Log.d("Usut", "image received");
                     Bitmap img = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

                     bla(img);

                 } catch (Exception e) {
                     Log.d("Usut", "error");
                     break;
                 }
             }
        }
    }*/

    public void updateImg()
    {
        Log.d("Usut", "image thread called");
        new UpdateImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //new ImageUpdater().start();
    }
    public void Send(String s)
    {
        new SendCoords(s).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void bla(Bitmap i){
        final Bitmap img = i;
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                imageView.setImageBitmap(img);

            }
        });
    }
}