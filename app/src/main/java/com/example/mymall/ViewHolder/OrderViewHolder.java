package com.example.mymall.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymall.R;
import com.example.mymall.itemclicklistener;

public class OrderViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView orderidtext;
    private itemclicklistener itemClickListener;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderidtext=itemView.findViewById(R.id.order_id);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
