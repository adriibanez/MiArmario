package com.adrianibanez.nosequeponerme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianibanez.nosequeponerme.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private EditText edtNombre_rg, edtEmail_rg, edtPass_rg;
    private Button btnSignUp;
    private TextView txtIrALogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference almacenImg;
    String userPath;
    Uri downloadUri;

    //private LoadingDialog loadingDialog;

    private static String name;
    private static String email;
    private static String pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        edtNombre_rg = this.findViewById(R.id.edtNombre_rg);
        edtEmail_rg = this.findViewById(R.id.edtEmail_rg);
        edtPass_rg = this.findViewById(R.id.edtPass_rg);
        btnSignUp = this.findViewById(R.id.btnSignUp);
        txtIrALogin = this.findViewById(R.id.txtIrALogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        almacenImg = FirebaseStorage.getInstance().getReference();
        

        edtNombre_rg.setText("");
        edtEmail_rg.setText("");
        edtPass_rg.setText("");

        //loadingDialog = new LoadingDialog(Registro.this);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });

        txtIrALogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irALogin();
            }
        });


    }

    private void registrar() {
        name = edtNombre_rg.getText().toString().trim();
        email = edtEmail_rg.getText().toString().trim();
        pass = edtPass_rg.getText().toString().trim();

        if (!name.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        //loadingDialog.startLoadingDialog();

                        Map<String, Object> user = new HashMap<>();
                        //user.put("userImg", pathDefaultUserImg);
                        user.put("name", name);
                        user.put("email", email);
                        user.put("pass", pass);

                        db.collection("users").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                subirUserImg();
                                Toast.makeText(getApplicationContext(), "Registro completado con éxito", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), BottomNavigation.class);
                                startActivity(i);
                                //loadingDialog.dismissDialog();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertaRegistro();
                            }
                        });
                    } else {
                        alertaRegistro();
                    }
                }
            });
        } else {
            alertaCamposVacios();
        }
    }


    private void subirUserImg()
    {
        Uri imgSubida = Uri.parse("android.resource://com.adrianibanez.nosequeponerme/drawable/user_image.png");
        StorageReference imgPath = almacenImg.child(userPath).child("userImg.png");//ruta en firebase storage

        UploadTask uploadTask = imgPath.putFile(imgSubida);//subo la imagen a la ruta especifica
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //loadingDialog.dismissDialog();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imgPath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUri = task.getResult();

                            Map<String, Object> userImg = new HashMap<>();
                            userImg.put("userImg", downloadUri);

                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .update(userImg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "Ha ocurrido un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void irALogin() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }


    private void alertaRegistro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error al registrar al usuario");
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