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

    // ================= INTERFACE ADMIN =================
    public interface OnAction {
        void onApprove(CutiModel c);
        void onReject(CutiModel c);
    }

    List<CutiModel> list;
    OnAction listener;
    boolean isAdmin;

    // ================= CONSTRUCTOR =================
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

        // ================= SET DATA =================
        holder.tvNama.setText(data.getNama());

        holder.tvTanggal.setText(
                data.getMulai() + " - " + data.getSelesai()
        );

        holder.tvAlasan.setText(data.getAlasan());

        String status = data.getStatus() != null
                ? data.getStatus().toLowerCase()
                : "pending";

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

        // ================= MODE ADMIN =================
        if (isAdmin) {

            // tombol hanya tampil jika pending
            if (status.equals("pending")) {

                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);

            } else {

                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            }

            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApprove(data);
                }
            });

            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReject(data);
                }
            });

        }

        // ================= MODE USER =================
        else {

            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= VIEWHOLDER =================
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