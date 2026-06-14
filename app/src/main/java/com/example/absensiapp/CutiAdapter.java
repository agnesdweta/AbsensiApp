package com.example.absensiapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CutiAdapter extends RecyclerView.Adapter<CutiAdapter.ViewHolder> {

    public interface OnAction {
        void onApprove(CutiModel c);
        void onReject(CutiModel c);
    }

    List<CutiModel> list;
    OnAction listener;
    boolean isAdmin;

    public CutiAdapter(List<CutiModel> list, OnAction listener, boolean isAdmin) {
        this.list = list;
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cuti, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CutiModel data = list.get(position);

        // ================= SAFETY NULL =================
        String nama = data.getNama() != null ? data.getNama() : "-";
        String mulai = data.getMulai() != null ? data.getMulai() : "-";
        String selesai = data.getSelesai() != null ? data.getSelesai() : "-";
        String alasan = data.getAlasan() != null ? data.getAlasan() : "-";
        String statusRaw = data.getStatus();

        if (statusRaw == null || statusRaw.isEmpty()) {
            statusRaw = "pending";
        }

        String status = statusRaw.toLowerCase();

        // ================= SET TEXT =================
        holder.tvNama.setText(nama);
        holder.tvTanggal.setText(mulai + " - " + selesai);
        holder.tvAlasan.setText(alasan);
        holder.tvStatus.setText(status.toUpperCase());

        // ================= WARNA STATUS =================
        switch (status) {

            case "pending":
                holder.tvStatus.setTextColor(Color.parseColor("#F59E0B"));
                break;

            case "disetujui":
                holder.tvStatus.setTextColor(Color.parseColor("#10B981"));
                break;

            case "ditolak":
                holder.tvStatus.setTextColor(Color.parseColor("#EF4444"));
                break;

            default:
                holder.tvStatus.setTextColor(Color.GRAY);
                break;
        }

        // ================= ADMIN MODE =================
        if (isAdmin) {

            if (status.equals("pending")) {

                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);

            } else {

                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            }

            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(data);
            });

            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(data);
            });

        } else {

            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNama, tvTanggal, tvAlasan, tvStatus;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNama);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvAlasan = itemView.findViewById(R.id.tvAlasan);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}