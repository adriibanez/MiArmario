package com.adrianibanez.nosequeponerme.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adrianibanez.nosequeponerme.Activities.LoadingDialog;
import com.adrianibanez.nosequeponerme.Adaptadores.AdCamisetas;
import com.adrianibanez.nosequeponerme.Adaptadores.AdPantalones;
import com.adrianibanez.nosequeponerme.Adaptadores.AdCalzado;
import com.adrianibanez.nosequeponerme.Pojos.Calzado;
import com.adrianibanez.nosequeponerme.Pojos.Camiseta;
import com.adrianibanez.nosequeponerme.Pojos.Pantalon;
import com.adrianibanez.nosequeponerme.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    View v;

    private FirebaseAuth mAuth;
    DocumentReference userRef;
    private FirebaseFirestore db;

    private static final int COD_SOLICITADO = 1;
    private TextView txtGeneral;
    private Button btnCompartir;
    LoadingDialog cargando;

    AdCamisetas adCamisetas;
    AdPantalones adPantalones;
    AdCalzado adCalzado;

    private CardView cardCamisetas;

    int numSCam;
    int numSPan;
    int numSCal;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        cargando = new LoadingDialog(getActivity());
        cargando.startLoadingDialog();

        v = inflater.inflate(R.layout.home, container, false);

        Window window = getActivity().getWindow();
        window.setStatusBarColor(Color.parseColor("#6774BC"));

        cardCamisetas = v.findViewById(R.id.cardCamisetas);
        cardCamisetas.setVisibility(View.GONE);
        btnCompartir = v.findViewById(R.id.btnCompartir);
        txtGeneral = v.findViewById(R.id.txtGeneral);

        cardCamisetas.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

        rellenaRecyclerCamisetas();
        rellenaRecyclerPantalones();
        rellenaRecyclerCalzado();

        setNombreUsuario();

        verificarPermisos();

        numSCam = RecyclerView.NO_POSITION;
        numSPan = 0;
        numSCal = 0;

        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Esto NO Funciona", Toast.LENGTH_SHORT).show();
            }
        });

        inicializarEventosAdaptadores();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adCamisetas.startListening();
        adPantalones.startListening();
        adCalzado.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adCamisetas.stopListening();
        adPantalones.stopListening();
        adCalzado.stopListening();

    }

    private void rellenaRecyclerCamisetas() {

        Query q = db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("camisetas").orderBy("nombre");

        FirestoreRecyclerOptions<Camiseta> options = new FirestoreRecyclerOptions.Builder<Camiseta>()
                .setQuery(q, Camiseta.class)
                .build();

        adCamisetas = new AdCamisetas(options);

        RecyclerView recyclerCamisetas = v.findViewById(R.id.rCamisetas);
        recyclerCamisetas.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCamisetas.setAdapter(adCamisetas);

        Toast.makeText(getActivity(), "ITEMS: " + adCamisetas.getItemCount(), Toast.LENGTH_SHORT).show();

        if(adCamisetas.getItemCount() == 0){
            cardCamisetas.setVisibility(View.VISIBLE);
        }
        else {
            cardCamisetas.setVisibility(View.GONE);}
        /*
        Query q1 = userRef.collection("camisetas");

        List<String> documentList = new ArrayList<>();
        userRef.collection("camisetas")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    documentList.add(document.getId());
                    cargando.dismissDialog();
                }

                for (String doc : documentList) {
                    userRef.collection("camisetas")
                            .document(doc)
                            .get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful() && task2.getResult() != null) {

                            String id = task2.getResult().getString("id");
                            String img = task2.getResult().getString("img");
                            String nombre = task2.getResult().getString("nombre");
                            String color = task2.getResult().getString("color");
                            String talla = task2.getResult().getString("talla");
                            String estilo = task2.getResult().getString("estilo");

                            //listaImgsPs.add(img);
                            //listaNomImgsPs.add(nombre);
                            Camiseta camiseta = new Camiseta(img, nombre, color, talla, estilo);
                            System.out.println("CAMISETA --> " + camiseta.toString());
                            //listaCamisetas.add(camiseta);
                            //ParteSuperior obPs = new ParteSuperior(color,talla,estilo);


                            cargando.dismissDialog();
                        } else {
                            //alertarErrorCarga();
                            cargando.dismissDialog();
                        }
                    });
                }
            } else {
                //alertar
                cargando.dismissDialog();
            }
            cargando.dismissDialog();
        });

        adapterCamisetas = new AdapterCamisetas(getActivity(), listaCamisetas);
        recyclerCamisetas.setAdapter(adapterCamisetas);
        */
    }

    private void rellenaRecyclerPantalones() {

        Query q = db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("pantalones").orderBy("nombre");

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("AQUI------------", "DOC " + document.getData());
                    }
                }
            }
        });

        FirestoreRecyclerOptions<Pantalon> options = new FirestoreRecyclerOptions.Builder<Pantalon>()
                .setQuery(q, Pantalon.class)
                .build();

        adPantalones = new AdPantalones(options);

        RecyclerView rPantalones = v.findViewById(R.id.rPantalones);
        rPantalones.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rPantalones.setAdapter(adPantalones);


    }

    private void rellenaRecyclerCalzado() {

        Query q = db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("calzado").orderBy("nombre");

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("AQUI------------", "DOC " + document.getData());
                    }
                }
            }
        });

        FirestoreRecyclerOptions<Calzado> options = new FirestoreRecyclerOptions.Builder<Calzado>()
                .setQuery(q, Calzado.class)
                .build();

        adCalzado = new AdCalzado(options);

        RecyclerView rCalzado = v.findViewById(R.id.rCalzado);
        rCalzado.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rCalzado.setAdapter(adCalzado);
    }

    private void setNombreUsuario() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String name = task.getResult().getString("name");
                txtGeneral.setText("BIENVENIDO A TU ARMARIO, " + name);
            } else {
                txtGeneral.setText("BIENVENIDO A TU ARMARIO, USUARIO");
            }
            cargando.dismissDialog();
        });
    }

    private void verificarPermisos() {
        String[] permisos = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getActivity(),
                permisos[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                permisos[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                permisos[2]) == PackageManager.PERMISSION_GRANTED) {

            Log.i("PERMISOS" ,"Permisos Verificados");
        } else {
            ActivityCompat.requestPermissions(getActivity(), permisos, COD_SOLICITADO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        verificarPermisos();
    }

    private void inicializarEventosAdaptadores()
    {
        //EVENTOS ONCLICK Y ONLONGCLICK ADAPTADOR CAMISETAS
        adCamisetas.setOnItemClickListener(new AdCamisetas.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documento, int posicion) {
                Camiseta camiseta = documento.toObject(Camiseta.class);

                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.mostrar_img_recycler);
                dialog.show();


                ImageView imgCambiar = dialog.findViewById(R.id.imgCambiar);
                TextView edtNombreCambiar = dialog.findViewById(R.id.edtNombreCambiar);
                EditText edtColorCambiar = dialog.findViewById(R.id.edtColorCambiar);
                EditText edtTallaCambiar = dialog.findViewById(R.id.edtTallaCambiar);
                EditText edtEstiloCambiar = dialog.findViewById(R.id.edtEstiloCambiar);
                Button btnGuardarCambios = dialog.findViewById(R.id.btnGuardarCambios);
                Button btnEliminar = dialog.findViewById(R.id.btnEliminar);

                Glide.with(imgCambiar.getContext()).asBitmap().load(camiseta.getImagen()).dontAnimate().into(imgCambiar);
                edtNombreCambiar.setText(camiseta.getNombre());
                edtColorCambiar.setText(camiseta.getColor());
                edtTallaCambiar.setText(camiseta.getTalla());
                edtEstiloCambiar.setText(camiseta.getEstilo());

                btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = edtNombreCambiar.getText().toString().replace(" ", "_").trim();
                        String nombre = edtNombreCambiar.getText().toString();
                        String color = edtColorCambiar.getText().toString();
                        String talla = edtTallaCambiar.getText().toString();
                        String estilo = edtEstiloCambiar.getText().toString();

                        DocumentReference docRefDelete = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("camisetas")
                                .document(camiseta.getNombre().replace(" ", "_").trim());

                        Map<String,Object> ropa = new HashMap<>();
                        ropa.put("id",id);
                        ropa.put("imagen",camiseta.getImagen());
                        ropa.put("nombre",nombre);
                        ropa.put("color",color);
                        ropa.put("talla",talla);
                        ropa.put("estilo",estilo);


                        //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                        Map<String,Object> borrarCampos = new HashMap<>();
                        borrarCampos.put("id", FieldValue.delete());
                        borrarCampos.put("nombre",FieldValue.delete());
                        borrarCampos.put("color",FieldValue.delete());
                        borrarCampos.put("talla",FieldValue.delete());
                        borrarCampos.put("estilo",FieldValue.delete());

                        docRefDelete.update(borrarCampos).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        //BORRO EL DOCUMENTO
                        docRefDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                        //AÑADO EL NUEVO DOCUMENTO CON LOS NUEVOS DATOS
                        DocumentReference docRefCreate = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("camisetas")
                                .document(id);

                        docRefCreate.set(ropa).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //informarUpdateCorrecto();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //informarUpdateInorrecto();
                            }
                        });
                        dialog.dismiss();
                        adCamisetas.startListening();
                    }
                });

                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentReference docRefDelete = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("camisetas")
                                .document(camiseta.getNombre().replace(" ", "_").trim());

                        //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                        Map<String,Object> borrarCampos = new HashMap<>();
                        borrarCampos.put("id", FieldValue.delete());
                        borrarCampos.put("imagen",FieldValue.delete());
                        borrarCampos.put("nombre",FieldValue.delete());
                        borrarCampos.put("color",FieldValue.delete());
                        borrarCampos.put("talla",FieldValue.delete());
                        borrarCampos.put("estilo",FieldValue.delete());

                        docRefDelete.update(borrarCampos).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        //BORRO EL DOCUMENTO
                        docRefDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        Toast.makeText(getActivity(), "Se ha eliminado " + edtNombreCambiar.getText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(position == RecyclerView.NO_POSITION) return;

                numSCam = position;
                if(numSCam == 0){
                    view.setBackgroundColor(getResources().getColor(R.color.naranja));
                    numSCam = 1;
                }
                else{
                    view.setBackgroundColor(getResources().getColor(R.color.fondo));
                    numSCam = 0;
                }

                Toast.makeText(getActivity()," Nums: " + numSCam, Toast.LENGTH_SHORT).show();
            }
        });

        //EVENTOS ONCLICK Y ONLONGCLICK ADAPTADOR PANTALONES
        adPantalones.setOnItemClickListener(new AdPantalones.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documento, int posicion) {
                Pantalon pantalon = documento.toObject(Pantalon.class);

                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.mostrar_img_recycler);
                dialog.show();


                ImageView imgCambiar = dialog.findViewById(R.id.imgCambiar);
                TextView edtNombreCambiar = dialog.findViewById(R.id.edtNombreCambiar);
                EditText edtColorCambiar = dialog.findViewById(R.id.edtColorCambiar);
                EditText edtTallaCambiar = dialog.findViewById(R.id.edtTallaCambiar);
                EditText edtEstiloCambiar = dialog.findViewById(R.id.edtEstiloCambiar);
                Button btnGuardarCambios = dialog.findViewById(R.id.btnGuardarCambios);
                Button btnEliminar = dialog.findViewById(R.id.btnEliminar);

                Glide.with(imgCambiar.getContext()).asBitmap().load(pantalon.getImagen()).dontAnimate().into(imgCambiar);
                edtNombreCambiar.setText(pantalon.getNombre());
                edtColorCambiar.setText(pantalon.getColor());
                edtTallaCambiar.setText(pantalon.getTalla());
                edtEstiloCambiar.setText(pantalon.getEstilo());

                btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = edtNombreCambiar.getText().toString().replace(" ", "_").trim();
                        String nombre = edtNombreCambiar.getText().toString();
                        String color = edtColorCambiar.getText().toString();
                        String talla = edtTallaCambiar.getText().toString();
                        String estilo = edtEstiloCambiar.getText().toString();

                        DocumentReference docRefDelete = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("pantalones")
                                .document(pantalon.getNombre().replace(" ", "_").trim());

                        Map<String,Object> ropa = new HashMap<>();
                        ropa.put("id",id);
                        ropa.put("imagen",pantalon.getImagen());
                        ropa.put("nombre",nombre);
                        ropa.put("color",color);
                        ropa.put("talla",talla);
                        ropa.put("estilo",estilo);


                        //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                        Map<String,Object> borrarCampos = new HashMap<>();
                        borrarCampos.put("id", FieldValue.delete());
                        borrarCampos.put("nombre",FieldValue.delete());
                        borrarCampos.put("color",FieldValue.delete());
                        borrarCampos.put("talla",FieldValue.delete());
                        borrarCampos.put("estilo",FieldValue.delete());

                        docRefDelete.update(borrarCampos).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        //BORRO EL DOCUMENTO
                        docRefDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                        //AÑADO EL NUEVO DOCUMENTO CON LOS NUEVOS DATOS
                        DocumentReference docRefCreate = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("pantalones")
                                .document(id);

                        docRefCreate.set(ropa).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //informarUpdateCorrecto();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //informarUpdateInorrecto();
                            }
                        });
                        dialog.dismiss();
                        adPantalones.startListening();
                    }
                });

                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentReference docRefDelete = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("pantalones")
                                .document(pantalon.getNombre().replace(" ", "_").trim());

                        //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                        Map<String,Object> borrarCampos = new HashMap<>();
                        borrarCampos.put("id", FieldValue.delete());
                        borrarCampos.put("imagen",FieldValue.delete());
                        borrarCampos.put("nombre",FieldValue.delete());
                        borrarCampos.put("color",FieldValue.delete());
                        borrarCampos.put("talla",FieldValue.delete());
                        borrarCampos.put("estilo",FieldValue.delete());

                        docRefDelete.update(borrarCampos).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        //BORRO EL DOCUMENTO
                        docRefDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        Toast.makeText(getActivity(), "Se ha eliminado " + edtNombreCambiar.getText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(position == RecyclerView.NO_POSITION) return;

                numSCam = position;
                if(numSCam == 0){
                    view.setBackgroundColor(getResources().getColor(R.color.naranja));
                    numSCam = 1;
                }
                else{
                    view.setBackgroundColor(getResources().getColor(R.color.fondo));
                    numSCam = 0;
                }

                Toast.makeText(getActivity()," Nums: " + numSCam, Toast.LENGTH_SHORT).show();
            }
        });

        //EVENTOS ONCLICK Y ONLONGCLICK DEL ADAPTADOR CALZADO
        adCalzado.setOnItemClickListener(new AdCalzado.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documento, int posicion) {
                Calzado calzado = documento.toObject(Calzado.class);

                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.mostrar_img_recycler);
                dialog.show();


                ImageView imgCambiar = dialog.findViewById(R.id.imgCambiar);
                TextView edtNombreCambiar = dialog.findViewById(R.id.edtNombreCambiar);
                EditText edtColorCambiar = dialog.findViewById(R.id.edtColorCambiar);
                EditText edtTallaCambiar = dialog.findViewById(R.id.edtTallaCambiar);
                EditText edtEstiloCambiar = dialog.findViewById(R.id.edtEstiloCambiar);
                Button btnGuardarCambios = dialog.findViewById(R.id.btnGuardarCambios);
                Button btnEliminar = dialog.findViewById(R.id.btnEliminar);

                Glide.with(imgCambiar.getContext()).asBitmap().load(calzado.getImagen()).dontAnimate().into(imgCambiar);
                edtNombreCambiar.setText(calzado.getNombre());
                edtColorCambiar.setText(calzado.getColor());
                edtTallaCambiar.setText(calzado.getTalla());
                edtEstiloCambiar.setText(calzado.getEstilo());

                btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = edtNombreCambiar.getText().toString().replace(" ", "_").trim();
                        String nombre = edtNombreCambiar.getText().toString();
                        String color = edtColorCambiar.getText().toString();
                        String talla = edtTallaCambiar.getText().toString();
                        String estilo = edtEstiloCambiar.getText().toString();

                        DocumentReference docRefDelete = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("calzado")
                                .document(calzado.getNombre().replace(" ", "_").trim());

                        Map<String,Object> ropa = new HashMap<>();
                        ropa.put("id",id);
                        ropa.put("imagen",calzado.getImagen());
                        ropa.put("nombre",nombre);
                        ropa.put("color",color);
                        ropa.put("talla",talla);
                        ropa.put("estilo",estilo);


                        //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                        Map<String,Object> borrarCampos = new HashMap();
                        borrarCampos.put("id", FieldValue.delete());
                        borrarCampos.put("nombre",FieldValue.delete());
                        borrarCampos.put("color",FieldValue.delete());
                        borrarCampos.put("talla",FieldValue.delete());
                        borrarCampos.put("estilo",FieldValue.delete());

                        docRefDelete.update(borrarCampos).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        //BORRO EL DOCUMENTO
                        docRefDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                        //AÑADO EL NUEVO DOCUMENTO CON LOS NUEVOS DATOS
                        DocumentReference docRefCreate = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("calzado")
                                .document(id);

                        docRefCreate.set(ropa).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //informarUpdateCorrecto();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //informarUpdateInorrecto();
                            }
                        });
                        dialog.dismiss();
                        adCalzado.startListening();
                    }
                });

                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentReference docRefDelete = db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("calzado")
                                .document(calzado.getNombre().replace(" ", "_").trim());

                        //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                        Map<String,Object> borrarCampos = new HashMap<>();
                        borrarCampos.put("id", FieldValue.delete());
                        borrarCampos.put("imagen",FieldValue.delete());
                        borrarCampos.put("nombre",FieldValue.delete());
                        borrarCampos.put("color",FieldValue.delete());
                        borrarCampos.put("talla",FieldValue.delete());
                        borrarCampos.put("estilo",FieldValue.delete());

                        docRefDelete.update(borrarCampos).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        //BORRO EL DOCUMENTO
                        docRefDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        Toast.makeText(getActivity(), "Se ha eliminado " + edtNombreCambiar.getText().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(position == RecyclerView.NO_POSITION) return;

                numSCam = position;
                if(numSCam == 0){
                    view.setBackgroundColor(getResources().getColor(R.color.naranja));
                    numSCam = 1;
                }
                else{
                    view.setBackgroundColor(getResources().getColor(R.color.fondo));
                    numSCam = 0;
                }

                Toast.makeText(getActivity()," Nums: " + numSCam, Toast.LENGTH_SHORT).show();
            }
        });


    }

}
