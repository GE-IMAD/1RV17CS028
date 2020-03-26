package com.example.mymall.ViewHolder;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.mymall.itemclicklistener;
import com.example.mymall.R;

//import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productname,productdescription,productprice;
    public ImageView imageview;
    public itemclicklistener listener;
    public  TextView ordername,quantity;
   public ProductViewHolder(View itemView){
       super(itemView);
       imageview=(ImageView) itemView.findViewById(R.id.product_image_main);
       productname=(TextView) itemView.findViewById(R.id.product_name);
       productdescription=(TextView) itemView.findViewById(R.id.product_description);
       productprice=(TextView)itemView.findViewById(R.id.product_price);
       ordername=(TextView)itemView.findViewById(R.id.order_item_name);
       quantity=(TextView)itemView.findViewById(R.id.order_item_quantity);
   }
   public void setItemClickListener(itemclicklistener listener){
       this.listener=listener;

   }
    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
