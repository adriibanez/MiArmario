package com.adrianibanez.nosequeponerme.Fragments;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianibanez.nosequeponerme.Activities.LoadingDialog;
import com.adrianibanez.nosequeponerme.Activities.Login;
import com.adrianibanez.nosequeponerme.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class PerfilFragment extends Fragment {

    private static final int ELEGIR_IMAGEN = 1;
    private static final int HACER_FOTO = 2;

    private LoadingDialog loadingDialog;
    private ImageView imgUser;
    private TextView txtEmail,txtEmail2,txtPass,txtNombre,txtNombre2,txtCamiCount,txtPantCount,txtCalzCount;
    private Button btnLogOut,btnUpdate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    LoadingDialog cargando;


    private StorageReference almacenImg;
    String userPath;
    Uri downloadUri;
    Bitmap bitmap;

    BottomSheetDialog bottomDialog;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cargando = new LoadingDialog(getActivity());
        cargando.startLoadingDialog();

        View v = inflater.inflate(R.layout.perfil_usuario, container, false);

        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.parseColor("#6774BC"));

        LoadingDialog loadingDialog2 = new LoadingDialog(getActivity());
        loadingDialog2.startLoadingDialog();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        almacenImg = FirebaseStorage.getInstance().getReference();

        loadingDialog = new LoadingDialog(getActivity());
        imgUser = v.findViewById(R.id.imgUser);
        txtNombre = v.findViewById(R.id.txtNombre);
        txtNombre2 = v.findViewById(R.id.txtNombre2);
        txtEmail = v.findViewById(R.id.txtEmail);
        txtEmail2 = v.findViewById(R.id.txtEmail2);
        txtPass = v.findViewById(R.id.txtPass);
        txtCamiCount = v.findViewById(R.id.txtCamiCount);
        txtPantCount = v.findViewById(R.id.txtPantCount);
        txtCalzCount = v.findViewById(R.id.txtCalzCount);
        btnLogOut = v.findViewById(R.id.btnLogOut);
        btnUpdate = v.findViewById(R.id.btnUpdate);
        userPath = "imagenes/" + mAuth.getCurrentUser().getUid();

        contarCamisetas();
        contarPantalones();
        contarCalzado();

        txtNombre.setText("");
        txtNombre2.setText("");
        txtEmail.setText("");
        txtEmail2.setText("");
        txtPass.setText("");

        cargarInfoLogin();

        loadingDialog2.dismissDialog();

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarUserImg();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFields();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                Intent i = new Intent(getActivity(), Login.class);
                startActivity(i);
            }
        });

        return v;

    }

    private void cargarInfoLogin()
    {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                String userImg = task.getResult().getString("userImg");
                String name = task.getResult().getString("name");
                String email = task.getResult().getString("email");
                String pass = task.getResult().getString("pass");
                System.out.println(name+email+pass+userImg);



                txtNombre.setText(name);
                txtNombre2.setText(name);
                txtEmail.setText(email);
                txtEmail2.setText(email);
                txtPass.setText(pass);
                cargando.dismissDialog();
            }else{
                alertarErrorCarga();
            }
        });
        //cuentaRopa();
    }

    /*
    private void cuentaRopa()
    {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("partes_superiores").get().then(snap => {
                size = snap.size // will return the collection size
        });


    }
*/

    private void editarUserImg(){

        bottomDialog = new BottomSheetDialog(getActivity());

        View view = getLayoutInflater().inflate(R.layout.mostrar_img_perfil,null);

        bottomDialog.setContentView(view);
        bottomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomDialog.show();

        Button btnGaleria = view.findViewById(R.id.btnGaleria);
        Button btnFoto = view.findViewById(R.id.btnFoto);
        Button btnEliminar = view.findViewById(R.id.btnEliminarImgPerfil);

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, ELEGIR_IMAGEN);
            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, HACER_FOTO);
                }
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Map<String, Object> userImg = new HashMap<>();
                userImg.put("userImg", FieldValue.delete());

                db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .update(userImg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Imagen eliminada con éxito", Toast.LENGTH_SHORT).show();
                        Glide.with(getActivity())
                                .load(R.drawable.user_image)
                                .fitCenter()
                                .centerCrop()
                                .into(imgUser);
                        bottomDialog.dismiss();
                    }
                });
            }
        });


    }

    private void updateFields()
    {

        Map<String,Object> user = new HashMap<>();
        user.put("name",txtNombre2.getText().toString());
        user.put("email",txtEmail2.getText().toString());

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                informarUpdateCorrecto();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                informarUpdateInorrecto();
            }
        });

        cargarInfoLogin();

    }




    private void cambiarUserImg(){
        final Map<String, Object> userImg = new HashMap<>();
        userImg.put("userImg", downloadUri.toString());

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .update(userImg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(getActivity(), "FOTO DE PERFIL ACTUALIZADA CORRECTAMENTE", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "SUBIDA INCORRECTA", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void subirImagenYObtenerUrl(Intent data) {

        loadingDialog.startLoadingDialog();
        Uri imgSubida = data.getData();//foto de la galeria
        StorageReference imgPath = almacenImg.child(userPath).child("/userImg.png");//ruta en firebase storage

        UploadTask uploadTask = imgPath.putFile(imgSubida);//subo la imagen a la ruta especifica
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Ha ocurrido un error al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                loadingDialog.dismissDialog();
                Glide.with(getActivity())
                        .load(imgSubida)
                        .fitCenter()
                        .centerCrop()
                        .into(imgUser);
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
                            cambiarUserImg();
                            bottomDialog.dismiss();
                            cargando.dismissDialog();

                        } else {
                            Toast.makeText(getActivity(), "Ha ocurrido un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "UserImg", null);
        return Uri.parse(path);
    }

    private void subirImagenYObtenerUrlDeCamara(Intent data) {

        loadingDialog.startLoadingDialog();

        bitmap = (Bitmap) data.getExtras().get("data");

        Uri imgSubida = getImageUri(getActivity(), bitmap);

        StorageReference imgPath = almacenImg.child(userPath).child("/userImg.png");//ruta en firebase storage

        UploadTask uploadTask = imgPath.putFile(imgSubida);//subo la imagen a la ruta especifica
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Ha ocurrido un error al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                loadingDialog.dismissDialog();
                Glide.with(getActivity())
                        .load(imgSubida)
                        .fitCenter()
                        .centerCrop()
                        .into(imgUser);
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
                            cambiarUserImg();
                            bottomDialog.dismiss();
                            cargando.dismissDialog();
                        } else {
                            Toast.makeText(getActivity(), "Ha ocurrido un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {

            if (requestCode == ELEGIR_IMAGEN && resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    Toast.makeText(getActivity(), "No se ha podido cargar la imagen seleccionada", Toast.LENGTH_SHORT).show();
                    return;
                }
                subirImagenYObtenerUrl(data);


            } else if (requestCode == HACER_FOTO && resultCode == Activity.RESULT_OK) {

                subirImagenYObtenerUrlDeCamara(data);

            }
        }

    }

    private void contarCamisetas()
    {
        db.collection("users").document(mAuth.getUid()).collection("camisetas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            txtCamiCount.setText(String.valueOf(count));
                        } else {

                        }
                    }
                });

    }
    private void contarPantalones()
    {
        db.collection("users").document(mAuth.getUid()).collection("pantalones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            txtPantCount.setText(String.valueOf(count));
                        } else {

                        }
                    }
                });

    }
    private void contarCalzado()
    {
        db.collection("users").document(mAuth.getUid()).collection("calzado")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            txtCalzCount.setText(String.valueOf(count));
                        } else {

                        }
                    }
                });

    }

    private void alertarErrorCarga()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error al cargar los datos");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void informarUpdateCorrecto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Información");
        builder.setMessage("Perfil actualizado correctamente");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void informarUpdateInorrecto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Información");
        builder.setMessage("No se ha podido actualizar el perfil");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}