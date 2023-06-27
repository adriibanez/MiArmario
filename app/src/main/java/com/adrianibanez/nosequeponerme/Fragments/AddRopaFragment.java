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
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.adrianibanez.nosequeponerme.Activities.BottomNavigation;
import com.adrianibanez.nosequeponerme.Activities.LoadingDialog;
import com.adrianibanez.nosequeponerme.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddRopaFragment extends Fragment {

    private LoadingDialog loadingDialog;
    private ImageView imgMostrarImg;
    private FloatingActionButton btnAddImg;
    private EditText txtNomImagen,edtTalla;
    private AutoCompleteTextView actColores, actTipoRopa, actEstilo;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference almacenImg;

    BottomSheetDialog bottomDialog;

    private static final int ELEGIR_IMAGEN = 1;
    private static final int HACER_FOTO = 2;

    Bitmap bitmap;

    String userPath;
    String path = "imagenes/" + UUID.randomUUID() + ".png";

    Uri downloadUri;

    String [] colores,tiposRopa,estilos;

    public AddRopaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.addropa, container, false);

        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.parseColor("#6774BC"));

        loadingDialog = new LoadingDialog(getActivity());
        imgMostrarImg = v.findViewById(R.id.imgMostrarImg);
        btnAddImg = v.findViewById(R.id.btnAñadirImg);
        txtNomImagen = v.findViewById(R.id.txtNomImagen);
        actColores = v.findViewById(R.id.actColores);
        actTipoRopa = v.findViewById(R.id.actTiporopa);
        actEstilo = v.findViewById(R.id.actEstilo);
        edtTalla = v.findViewById(R.id.edtTalla);

        mAuth = FirebaseAuth.getInstance();
        userPath = "imagenes/" + mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        almacenImg = FirebaseStorage.getInstance().getReference();


        txtNomImagen.setVisibility(View.GONE);

        cargarSpinners();

        imgMostrarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirOpcionAddRopa();
            }
        });


        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imgMostrarImg.getDrawable().equals(R.drawable.noimage) || txtNomImagen.getText().toString().isEmpty()
                || !comprobarCampos()) {
                    Toast.makeText(getActivity(), "Faltan datos por rellenar", Toast.LENGTH_SHORT).show();
                } else {

                    String id = txtNomImagen.getText().toString().replace(" ", "_").trim();
                    String nombre = txtNomImagen.getText().toString();
                    String color = actColores.getText().toString().toLowerCase();
                    String tipoRopa = actTipoRopa.getText().toString();
                    String talla = edtTalla.getText().toString();
                    String estilo = actEstilo.getText().toString();

                    switch (tipoRopa) {
                        case "Camiseta":
                            final Map<String, Object> camiseta = new HashMap<>();
                            camiseta.put("id", id);
                            camiseta.put("imagen", downloadUri.toString());
                            camiseta.put("nombre", nombre);
                            camiseta.put("color", color);
                            camiseta.put("talla", talla);
                            camiseta.put("estilo", estilo);

                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .collection("camisetas").document(txtNomImagen.getText().toString().replace(" ", "_").trim()).set(camiseta).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getActivity(), "CAMISETA SUBIDA CORRECTAMENTE", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "SUBIDA INCORRECTA", Toast.LENGTH_LONG).show();
                                }
                            });


                            break;


                        case "Pantalón":
                            final Map<String, Object> pantalon = new HashMap<>();
                            pantalon.put("id", id);
                            pantalon.put("imagen", downloadUri.toString());
                            pantalon.put("nombre", nombre);
                            pantalon.put("color", color);
                            pantalon.put("talla", talla);
                            pantalon.put("estilo", estilo);

                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .collection("pantalones").document(txtNomImagen.getText().toString().replace(" ", "_").trim()).set(pantalon).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getActivity(), "PANTALÓN SUBIDO CORRECTAMENTE", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "SUBIDA INCORRECTA", Toast.LENGTH_LONG).show();
                                }
                            });
                            break;


                        case "Calzado":

                            final Map<String, Object> zapato = new HashMap<>();
                            zapato.put("id", id);
                            zapato.put("imagen", downloadUri.toString());
                            zapato.put("nombre", nombre);
                            zapato.put("color", color);
                            zapato.put("talla", talla);
                            zapato.put("estilo", estilo);

                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .collection("calzado").document(txtNomImagen.getText().toString().replace(" ", "_").trim()).set(zapato).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getActivity(), "CALZADO SUBIDO CORRECTAMENTE", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "SUBIDA INCORRECTA", Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                    }
                }
            }
        });


        return v;
    }

    private void elegirOpcionAddRopa()
    {
        bottomDialog = new BottomSheetDialog(getActivity());

        View view = getLayoutInflater().inflate(R.layout.addropa_opciones,null);

        bottomDialog.setContentView(view);
        bottomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomDialog.show();

        Button btnElegirGaleria = view.findViewById(R.id.btnElegirGaleria);
        Button btnHacerFoto = view.findViewById(R.id.btnHacerFoto);

        btnElegirGaleria.setOnClickListener(v3 -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(i, ELEGIR_IMAGEN);
            bottomDialog.dismiss();
        });

        btnHacerFoto.setOnClickListener(v3 -> {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(i, HACER_FOTO);
                bottomDialog.dismiss();
            }
        });


    }

    private void subirImagenYObtenerUrl(Intent data) {

        loadingDialog.startLoadingDialog();

        Uri imgSubida = data.getData();//foto de la galeria
        StorageReference imgPath = almacenImg.child(userPath).child(UUID.randomUUID() + ".png");//ruta en firebase storage

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
                        .into(imgMostrarImg);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void subirImagenYObtenerUrlDeCamara(Intent data) {

        loadingDialog.startLoadingDialog();

        bitmap = (Bitmap) data.getExtras().get("data");

        Uri imgSubida = getImageUri(getActivity(), bitmap);

        StorageReference imgPath = almacenImg.child(userPath).child(UUID.randomUUID() + ".png");//ruta en firebase storage

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
                        .into(imgMostrarImg);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

                btnAddImg.setVisibility(View.VISIBLE);
                txtNomImagen.setVisibility(View.VISIBLE);

            } else if (requestCode == HACER_FOTO && resultCode == Activity.RESULT_OK) {

                subirImagenYObtenerUrlDeCamara(data);

                btnAddImg.setVisibility(View.VISIBLE);
                txtNomImagen.setVisibility(View.VISIBLE);
            }
        }

    }

    private void cargarSpinners() {

        colores = new String[]{"Rojo", "Verde", "Azul", "Amarillo", "Negro", "Blanco", "Marrón","Gris","Morado","Naranja"};
        tiposRopa = new String[]{"Camiseta", "Pantalón", "Calzado"};
        estilos = new String[]{"Sport", "Elegante","Hipster","Minimalista","Clásico","Urban"};

        ArrayAdapter<String> adapterSpColores = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, colores);
        actColores.setAdapter(adapterSpColores);

        ArrayAdapter<String> adapterSPTipoRopa = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tiposRopa);
        actTipoRopa.setAdapter(adapterSPTipoRopa);

        ArrayAdapter<String> adapterSpEstilo = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, estilos);
        actEstilo.setAdapter(adapterSpEstilo);

    }

    private boolean comprobarCampos()
    {
        ArrayList<String> listaTallas = new ArrayList<>();
        for (int i = 0;i<=50;i++){
            listaTallas.add(String.valueOf(i));
            listaTallas.add(String.valueOf(i + 0.5));
        }
        listaTallas.add("XS");
        listaTallas.add("xs");
        listaTallas.add("S");
        listaTallas.add("s");
        listaTallas.add("M");
        listaTallas.add("m");
        listaTallas.add("L");
        listaTallas.add("l");
        listaTallas.add("XL");
        listaTallas.add("xl");
        listaTallas.add("2XL");
        listaTallas.add("2xl");
        listaTallas.add("3XL");
        listaTallas.add("3xl");
        listaTallas.add("4XL");
        listaTallas.add("4xl");


        boolean estaEnTallas = listaTallas.contains(edtTalla.getText().toString());

        boolean estaEnColores = Arrays.asList(colores).contains(actColores.getText().toString());

        boolean estaEnTipoRopa = Arrays.asList(tiposRopa).contains(actTipoRopa.getText().toString());

        boolean estaEnEstilos = Arrays.asList(estilos).contains(actEstilo.getText().toString());

        return estaEnColores && estaEnTipoRopa && estaEnEstilos && estaEnTallas;
    }



}