package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView name;
    TextView party;


    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        name = itemView.findViewById(R.id.name);
        party = itemView.findViewById(R.id.party);

    }
}
