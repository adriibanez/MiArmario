package com.adrianibanez.nosequeponerme.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adrianibanez.nosequeponerme.Fragments.HomeFragment;
import com.adrianibanez.nosequeponerme.Pojos.Calzado;
import com.adrianibanez.nosequeponerme.Pojos.Camiseta;
import com.adrianibanez.nosequeponerme.Pojos.Pantalon;
import com.adrianibanez.nosequeponerme.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AdapterCamisetas extends RecyclerView.Adapter<AdapterCamisetas.ViewHolder> {

    LayoutInflater inflador;
    Context context;
    ArrayList<Camiseta> listaCamisetas;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public AdapterCamisetas(Context context, ArrayList<Camiseta> listaCamisetas/*ArrayList<String> listaImgs, ArrayList<String> listaNomImgs, ParteSuperior obPs*/) {
        inflador = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.listaCamisetas = listaCamisetas;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflador.inflate(R.layout.itemps, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(holder.itemImg).asBitmap().load(listaCamisetas.get(position).getImagen()).into(holder.itemImg);
        holder.itemNomimg.setText(listaCamisetas.get(position).getNombre());
        holder.itemColimg.setText(listaCamisetas.get(position).getColor());
        holder.itemTallaimg.setText(listaCamisetas.get(position).getTalla());
        holder.itemEstiloimg.setText(listaCamisetas.get(position).getEstilo());

        holder.itemImg.setOnClickListener(view -> mostrarImg(position));
        //holder.img.setOnLongClickListener(view -> false);
        //holder.itemView.setBackgroundColor(color);

    }

    private void mostrarImg(int position)
    {
        Dialog dialog = new Dialog(context);
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

        Glide.with(imgCambiar).asBitmap().load(listaCamisetas.get(position).getImagen()).into(imgCambiar);
        edtNombreCambiar.setText(listaCamisetas.get(position).getNombre());
        edtColorCambiar.setText(listaCamisetas.get(position).getColor());
        edtTallaCambiar.setText(listaCamisetas.get(position).getTalla());
        edtEstiloCambiar.setText(listaCamisetas.get(position).getEstilo());


        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = edtNombreCambiar.getText().toString().replace(" ", "_").trim();
                String img = listaCamisetas.get(position).getImagen();
                String nombre = edtNombreCambiar.getText().toString();
                String color = edtColorCambiar.getText().toString();
                String talla = edtTallaCambiar.getText().toString();
                String estilo = edtEstiloCambiar.getText().toString();

                DocumentReference docRefDelete = db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("camisetas")
                        .document(listaCamisetas.get(position).getNombre().replace(" ", "_").trim());

                Map<String,Object> ropa = new HashMap<>();
                ropa.put("id",id);
                ropa.put("img",img);
                ropa.put("nombre",nombre);
                ropa.put("color",color);
                ropa.put("talla",talla);
                ropa.put("estilo",estilo);


                //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                Map<String,Object> borrarCampos = new HashMap();
                borrarCampos.put("id", FieldValue.delete());
                borrarCampos.put("img",FieldValue.delete());
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

                //listaObPs.remove(position);
                notifyItemChanged(position);
                notifyItemRangeChanged(position,getItemCount());

                dialog.dismiss();



            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference docRefDelete = db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("camisetas")
                        .document(listaCamisetas.get(position).getNombre().replace(" ", "_").trim());

                //BORRO TODOS LOS CAMPOS DEL DOCUMENTO
                Map<String,Object> borrarCampos = new HashMap();
                borrarCampos.put("id", FieldValue.delete());
                borrarCampos.put("img",FieldValue.delete());
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

                Toast.makeText(context, "Se ha eliminado " + edtNombreCambiar.getText().toString(), Toast.LENGTH_SHORT).show();
                listaCamisetas.remove(position);
                notifyItemRemoved(position);
                //notifyItemChanged(position);
                //notifyItemRangeChanged(position,getItemCount());
                dialog.dismiss();
            }
        });


    }

    private void seleccionarImg() {
    }

    @Override
    public int getItemCount() {
        return listaCamisetas.size();
    }

    /*

    public void eliminarItem(int posicion) {
        listaPartesSuperiores.remove(posicion);
        //notifyItemRemoved(posicion);
    }
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImg;
        TextView itemNomimg;
        TextView itemColimg;
        TextView itemTallaimg;
        TextView itemEstiloimg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.itemImg);
            itemNomimg = itemView.findViewById(R.id.itemNomimg);
            itemColimg = itemView.findViewById(R.id.itemColimg);
            itemTallaimg = itemView.findViewById(R.id.itemTallaimg);
            itemEstiloimg = itemView.findViewById(R.id.itemEstiloimg);
        }
    }


}