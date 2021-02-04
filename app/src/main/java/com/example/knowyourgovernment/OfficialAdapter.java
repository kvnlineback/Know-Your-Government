package com.example.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;


public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private ArrayList<Official> list;
    private MainActivity mainActivity;

    OfficialAdapter(ArrayList<Official> list, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.list = list;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_row_item, parent, false);
        itemView.setOnClickListener(mainActivity);
        return new OfficialViewHolder(itemView);
    }


    @Override

    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = list.get(position);
        holder.name.setText(official.getName());
        holder.title.setText(official.getTitle());
        if (!official.getParty().contains("Dem") && !official.getParty().contains("Rep") && !official.getParty().contains("Non"))
            holder.party.setText("(Nonpartisan)");
        else
            holder.party.setText(String.format(Locale.getDefault(), "(%s)", official.getParty()));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
