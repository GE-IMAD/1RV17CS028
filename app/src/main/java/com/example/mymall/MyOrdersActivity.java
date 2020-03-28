package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mymall.Model.Cart;
import com.example.mymall.Model.Order;
import com.example.mymall.ViewHolder.CartViewHolder;
import com.example.mymall.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyOrdersActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public DatabaseReference orderlistRef;
    public String email;
    public String values[];
    public List<String> list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        Bundle extras;
        extras=getIntent().getExtras();
        recyclerView=findViewById(R.id.order_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        email=extras.getString("email");
        values = email.split("@");
        orderlistRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(values[0]);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Order> options= new FirebaseRecyclerOptions.Builder<Order>().setQuery(orderlistRef,Order.class).build();
        FirebaseRecyclerAdapter<Order, OrderViewHolder>adapter=new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder orderViewHolder, int i, @NonNull final Order order) {
            //    Toast.makeText(MyOrdersActivity.this,order.getProductMap(),Toast.LENGTH_LONG);
                orderViewHolder.orderidtext.setText("Order ID : " + order.getOrder_id());
                orderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Iterator it = items.entrySet().iterator();
                        int i=0;
                        for (Map.Entry<String, String> e : order.getProductMap().entrySet()) {
                            //to get key
                            e.getKey();
                            //  Toast.makeText(OrderItems.this,e.getValue(),Toast.LENGTH_LONG).show();
                            //and to get value
                            e.getValue();
                            list.add(e.getKey() + " : " + e.getValue());
                           // Toast.makeText(OrderItems.this,itemarray[i],Toast.LENGTH_LONG).show();
                            i++;
                        }

                        Intent intent=new Intent(MyOrdersActivity.this,OrderItems.class);
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST",(Serializable)list);
                        intent.putExtra("BUNDLE",args);
                        intent.putExtra("email",email);
                        intent.putExtra("productmap",order.getProductMap());
                        intent.putExtra("orderid",order.getOrder_id());
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items,parent,false);
                OrderViewHolder holder= new OrderViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
