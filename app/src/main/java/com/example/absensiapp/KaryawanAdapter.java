package com.example.absensiapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.Holder> {

    public interface OnItemClick {
        void onEdit(KaryawanModel karyawan);
        void onDelete(KaryawanModel karyawan);
    }

    ArrayList<KaryawanModel> list;
    OnItemClick listener;

    public KaryawanAdapter(ArrayList<KaryawanModel> list, OnItemClick listener) {
        this.list = list;
        this.listener = listener;
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView tvNama, tvJabatan, tvTelp;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNama);
            tvJabatan = itemView.findViewById(R.id.tvJabatan);
            tvTelp = itemView.findViewById(R.id.tvTelp);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_karyawan, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        KaryawanModel k = list.get(position);

        holder.tvNama.setText(k.getNama());
        holder.tvJabatan.setText(k.getJabatan());
        holder.tvTelp.setText(k.getTelp());

        // ✅ KLIK NAMA = EDIT
        holder.tvNama.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(k);
        });

        // ✅ LONG PRESS ITEM = DELETE
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onDelete(k);
            return true;
        });
        // 🔥 TAMBAHAN: klik seluruh item juga bisa delete (opsional)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // kamu bisa pilih edit atau delete di sini
                listener.onEdit(k);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}