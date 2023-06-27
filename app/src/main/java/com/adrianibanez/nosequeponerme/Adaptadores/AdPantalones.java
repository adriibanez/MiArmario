package com.adrianibanez.nosequeponerme.Adaptadores;

import android.app.Dialog;
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
import com.adrianibanez.nosequeponerme.Pojos.Camiseta;
import com.adrianibanez.nosequeponerme.Pojos.Pantalon;
import com.adrianibanez.nosequeponerme.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AdPantalones extends FirestoreRecyclerAdapter<Pantalon,AdPantalones.PantalonHolder> {
    private OnItemClickListener listener;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    PantalonHolder holder;
    Pantalon pantalon;

    public AdPantalones(@NonNull FirestoreRecyclerOptions<Pantalon> options) {
        super(options);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull PantalonHolder holder, int position, @NonNull Pantalon pantalon) {
        this.pantalon = pantalon;
        this.holder = holder;
        Glide.with(holder.itemImg.getContext())
                .asBitmap()
                .load(pantalon.getImagen())
                .dontAnimate()//MUY IMPORTANTE
                .into(holder.itemImg);
        System.out.println("IMAGEN" + pantalon.getImagen());
        holder.itemNomimg.setText(pantalon.getNombre());
        holder.itemColimg.setText(pantalon.getColor());
        holder.itemTallaimg.setText(pantalon.getTalla());
        holder.itemEstiloimg.setText(pantalon.getEstilo());
    }


    @NonNull
    @Override
    public PantalonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemps, parent, false);
        return new PantalonHolder(view);
    }

    @Override
    public void onDataChanged() {
        if(getItemCount() == 0)
        {
        }
    }

    public class PantalonHolder extends RecyclerView.ViewHolder {

        ImageView itemImg;
        TextView itemNomimg;
        TextView itemColimg;
        TextView itemTallaimg;
        TextView itemEstiloimg;

        public PantalonHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.itemImg);
            itemNomimg = itemView.findViewById(R.id.itemNomimg);
            itemColimg = itemView.findViewById(R.id.itemColimg);
            itemTallaimg = itemView.findViewById(R.id.itemTallaimg);
            itemEstiloimg = itemView.findViewById(R.id.itemEstiloimg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int posicion = getBindingAdapterPosition();
                    //De esta manera se evita que se clique en el item que se está eliminando
                    if(posicion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(posicion),posicion);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(v, getAdapterPosition());
                    return true;
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documento,int posicion);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }



}
