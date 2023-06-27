package com.adrianibanez.nosequeponerme.Permisos;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

public class FragmentPermissionHelper {
    public void startPermissionRequest(FragmentActivity fr,FragmentPermissionInterface fs,String manifest){
        ActivityResultLauncher<String> requestPermissionLauncher =
                fr.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if(isGranted)
                    {

                    }
                    else{

                    }
        });
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

}
