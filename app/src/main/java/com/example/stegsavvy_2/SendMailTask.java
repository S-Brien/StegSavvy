package com.example.stegsavvy_2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class SendMailTask extends AsyncTask {

    private ProgressDialog sDialog;
    private Activity sendMailActivity;

    public SendMailTask(Activity activity){
        sendMailActivity = activity;
    }

    protected void onPreExecute(){
        sDialog = new ProgressDialog(sendMailActivity);
        sDialog.setMessage("Hold on, nearly there..");
        sDialog.setIndeterminate(false);
        sDialog.setCancelable(false);
        sDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args){
        try{
            Log.i("SendMailTask", "Setting your email up..");
            publishProgress("Doing our stuff, hang tight");
            GMail androidEmail = new GMail(args[0].toString(), args[1].toString(),
                    (List) args[2], args[3].toString(), args[4].toString());
            publishProgress("Readying your beautiful information.");
            androidEmail.createEmailMessage();
            publishProgress("Trying to send your information please wait..");
            androidEmail.SendMail();
            publishProgress("Thank you for your info");
            Log.i("SendMailTask", "Thank you for using our service..");
        }
        catch (Exception e){
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    // Loading the Dialog Message and setting it up;
    @Override
    public void onProgressUpdate(Object... values){
        sDialog.setMessage(values[0].toString());
    }

    // Ridding or Dismissing these dialog messages
    @Override
    public void onPostExecute(Object result){
        sDialog.dismiss();
    }
}
