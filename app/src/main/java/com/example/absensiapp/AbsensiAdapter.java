package com.example.absensiapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AbsensiAdapter
        extends RecyclerView.Adapter<AbsensiAdapter.ViewHolder> {

    public interface OnAction {
        void onEdit(AbsensiModel a);
        void onDelete(AbsensiModel a);
    }

    List<AbsensiModel> list;
    OnAction listener;

    public AbsensiAdapter(List<AbsensiModel> list,
                          OnAction listener) {

        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_absensi,
                        parent,
                        false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        AbsensiModel data = list.get(position);

        holder.tvNama.setText(data.getNama());

        holder.tvTanggal.setText(
                "Tanggal : " + data.getTanggal()
        );

        String jamMasuk = data.getJamMasuk();
        String jamKeluar = data.getJamKeluar();

        if (jamMasuk == null ||
                jamMasuk.equals("null")) {
            jamMasuk = "-";
        }

        if (jamKeluar == null ||
                jamKeluar.equals("null") ||
                jamKeluar.isEmpty()) {
            jamKeluar = "-";
        }

        holder.tvJam.setText(
                "Jam : " + jamMasuk + " - " + jamKeluar
        );

        holder.tvStatus.setText(
                "Status : " + data.getStatus()
        );

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(data);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNama;
        TextView tvTanggal;
        TextView tvJam;
        TextView tvStatus;

        ImageView btnEdit;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNama);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJam = itemView.findViewById(R.id.tvJam);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}