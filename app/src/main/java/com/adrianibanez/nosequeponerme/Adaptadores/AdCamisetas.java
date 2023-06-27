package com.adrianibanez.nosequeponerme.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.adrianibanez.nosequeponerme.Pojos.Camiseta;
import com.adrianibanez.nosequeponerme.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdCamisetas extends FirestoreRecyclerAdapter<Camiseta,AdCamisetas.CamisetaHolder> {
    private OnItemClickListener listener;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    CamisetaHolder holder;
    Camiseta camiseta;

    public AdCamisetas(@NonNull FirestoreRecyclerOptions<Camiseta> options) {
        super(options);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull CamisetaHolder holder, int position, @NonNull Camiseta camiseta) {
        this.camiseta = camiseta;
        this.holder = holder;
        Glide.with(holder.itemImg.getContext())
                .asBitmap()
                .load(camiseta.getImagen())
                .dontAnimate()//MUY IMPORTANTE, sin esta línea(la cual quita las animaciones al cargar la imagen en el ImageView)
                                //la imagen no se carga con FirestoreRecyclerAdapter
                .into(holder.itemImg);
        holder.itemNomimg.setText(camiseta.getNombre());
        holder.itemColimg.setText(camiseta.getColor());
        holder.itemTallaimg.setText(camiseta.getTalla());
        holder.itemEstiloimg.setText(camiseta.getEstilo());

    }


    @NonNull
    @Override
    public CamisetaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemps, parent, false);
        return new CamisetaHolder(view);
    }



    @Override
    public void onDataChanged() {
        if(getItemCount() == 0){

        }
    }

    public class CamisetaHolder extends RecyclerView.ViewHolder {

        ImageView itemImg;
        TextView itemNomimg;
        TextView itemColimg;
        TextView itemTallaimg;
        TextView itemEstiloimg;

        public CamisetaHolder(@NonNull View itemView) {
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
                    listener.onItemLongClick(v, getBindingAdapterPosition());
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
