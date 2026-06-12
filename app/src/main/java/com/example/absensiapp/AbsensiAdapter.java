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

        holder.tvJam.setText(
                data.getJamMasuk()
                        + " - "
                        + data.getJamKeluar()
        );

        holder.btnEdit.setOnClickListener(v -> {
            listener.onEdit(data);
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onDelete(data);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNama,
                tvTanggal,
                tvJam;

        ImageView btnEdit,
                btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNama);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJam = itemView.findViewById(R.id.tvJam);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}