package com.adrianibanez.nosequeponerme.Activities;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.adrianibanez.nosequeponerme.R;

public class LoadingDialog {
    public Activity activity;
    public AlertDialog dialog;

    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }
    public void startLoadingDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(R.layout.progress_dialog);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog()
    {
        dialog.dismiss();
    }



}
