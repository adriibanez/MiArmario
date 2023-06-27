package com.adrianibanez.nosequeponerme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.adrianibanez.nosequeponerme.Fragments.AddRopaFragment;
import com.adrianibanez.nosequeponerme.Fragments.HomeFragment;
import com.adrianibanez.nosequeponerme.Fragments.PerfilFragment;
import com.adrianibanez.nosequeponerme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class BottomNavigation extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_navigation);

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("nueva_combinacion");

        bottomNavigationView = this.findViewById(R.id.bttom_navigation);
        //Se inicia con el primer fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new HomeFragment()).commit();
        //Se selecciona el primer item del bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.nav_addFoto:
                        fragment = new AddRopaFragment();
                        break;

                    case R.id.nav_perfil:
                        fragment = new PerfilFragment();
                        break;
                }
                //Permite usar el contenedor principal(bottom navigation)
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();

                return true;
            }
        });


    }
}