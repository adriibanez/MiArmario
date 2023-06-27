package com.adrianibanez.nosequeponerme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianibanez.nosequeponerme.Activities.LoadingDialog;
import com.adrianibanez.nosequeponerme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NuevaCombinacion extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DocumentReference userRef;
    private FirebaseFirestore db;

    private TextView txtComb;
    private ImageButton btnFavorito;
    private LoadingDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_combinacion);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#6774BC"));

        cargando = new LoadingDialog(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

        cargando.startLoadingDialog();

        setNombreUsuario();

        txtComb = this.findViewById(R.id.txtComb);
        btnFavorito = this.findViewById(R.id.btnFavorito);
        btnFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarQuitarCombinacion();
            }
        });
    }

    private void agregarQuitarCombinacion()
    {
        Bitmap imageBtm = ((BitmapDrawable)btnFavorito.getDrawable()).getBitmap();

        Drawable fav = ResourcesCompat.getDrawable(getResources(), R.drawable.favorito, null);

        Bitmap favBtm = ((BitmapDrawable) fav).getBitmap();

        Drawable noFav = ResourcesCompat.getDrawable(getResources(), R.drawable.no_favorito, null);
        Bitmap noFavBitm = ((BitmapDrawable) noFav).getBitmap();

        if (imageBtm.sameAs(noFavBitm)){
            btnFavorito.setImageResource(R.drawable.favorito);
            addFav();
            Toast.makeText(getApplicationContext(), "TE GUSTA ESTA COMBINACIÓN", Toast.LENGTH_SHORT).show();
        }
        else if (imageBtm.sameAs(favBtm)){
            btnFavorito.setImageResource(R.drawable.no_favorito);
            quitarFav();
            Toast.makeText(getApplicationContext(), "YA NO TE GUSTA", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNombreUsuario() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String name = task.getResult().getString("name");
                txtComb.setText("BESTA ES LA COMBINACIÓN DE " + name);
            } else {
                txtComb.setText("ESTA ES LA COMBINACIÓN DE USUARIO " + mAuth.getCurrentUser().getUid());
            }
        });
    }
    private void addFav(){

    }

    private void quitarFav(){

    }

    private void volver()
    {
        //Intent i = new Intent(this,)
    }
}