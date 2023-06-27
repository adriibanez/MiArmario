package com.adrianibanez.nosequeponerme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adrianibanez.nosequeponerme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {


    private EditText edtEmail_lg, edtPass_lg;
    private Button btnLogIn;
    private TextView txtIrARegistro;

    private FirebaseAuth auth;

    //private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edtEmail_lg = this.findViewById(R.id.edtEmail_lg);
        edtPass_lg = this.findViewById(R.id.edtPass_lg);
        btnLogIn = this.findViewById(R.id.btnLogIn);
        txtIrARegistro = this.findViewById(R.id.btnIrARegistro);

        auth = FirebaseAuth.getInstance();

        //Si se consigue recuperar la instancia auth (la aplicacion guarda
        // internamente al usuario incluso despues de reiniciar la app)
        //se reloguea al usuario
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(this, BottomNavigation.class);
            startActivity(i);
            finish();
        }

        edtEmail_lg.setText("");
        edtPass_lg.setText("");

        //loadingDialog = new LoadingDialog(Login.this);


        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });

        txtIrARegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irARegistro();
            }
        });


    }


    private void iniciarSesion() {
        String email = edtEmail_lg.getText().toString().trim();
        String pass = edtPass_lg.getText().toString().trim();

        if (!email.isEmpty() && !pass.isEmpty()) {
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(getApplicationContext(), BottomNavigation.class);
                        startActivity(i);
                        //loadingDialog.startLoadingDialog();
                        Toast.makeText(getApplicationContext(), "Accediste como " + email, Toast.LENGTH_SHORT).show();
                        //loadingDialog.dismissDialog();
                        finish();
                    } else {
                        alertaInicioSesion();
                    }
                }
            });
        } else {
            alertaCamposVacios();
        }
    }

    private void irARegistro() {
        Intent i = new Intent(this, Registro.class);
        startActivity(i);
    }


    private void alertaInicioSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error al iniciar sesión");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void alertaCamposVacios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Email y/o contraseña vacío/s");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
