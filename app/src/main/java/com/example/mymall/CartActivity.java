package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymall.Model.Cart;
import com.example.mymall.Prevalant.Prevalant;
import com.example.mymall.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessbtn;
    private TextView totalamount;
    public String email;
    HashMap<String,String> productidmap=new HashMap<>();
    public DatabaseReference cartlistRef;
    private int FinalPrice=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras;
        extras=getIntent().getExtras();
        setContentView(R.layout.activity_cart);
        recyclerView=findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NextProcessbtn=(Button) findViewById(R.id.next_process_button);
        totalamount=(TextView) findViewById(R.id.Total_Price_text);
        cartlistRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        email=extras.getString("email");
        totalamount.setText("Total Price ="+String.valueOf(FinalPrice));
        NextProcessbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalamount.setText("Total Price ="+String.valueOf(FinalPrice));
                Toast.makeText(CartActivity.this,String.valueOf(FinalPrice),Toast.LENGTH_LONG).show();
                Intent intent =new Intent(CartActivity.this,ConfirmFinalActivity.class);
                intent.putExtra("total Price",String.valueOf(FinalPrice));
                intent.putExtra("email",email);
                intent.putExtra("pids",productidmap);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        final String[] values = email.split("@");
        super.onStart();
        totalamount.setText("Total Price ="+String.valueOf(FinalPrice));
        Toast.makeText(CartActivity.this,values[0],Toast.LENGTH_LONG).show();
        FirebaseRecyclerOptions<Cart> options= new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartlistRef.child("User View").child(values[0]).child("Products"),Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder>adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                productidmap.put(cart.getPid(),cart.getQuantity());
                totalamount.setText("Total Price ="+String.valueOf(FinalPrice));
                cartViewHolder.txtProductName.setText("Product Name:  " + cart.getPname());
                cartViewHolder.txtProductquantity.setText("Product Quantity:  "+cart.getQuantity());
                int oneTypeProductPrice=((Integer.valueOf(cart.getPrice())))*Integer.valueOf(cart.getQuantity());
                FinalPrice=FinalPrice+oneTypeProductPrice;
                totalamount.setText("Total Price ="+String.valueOf(FinalPrice));
                cartViewHolder.txtProductprice.setText("Product Price:  "+cart.getPrice());
                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[]=new CharSequence[]{
                          "Edit",
                          "Remove"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0)
                            {
                                Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("pid",cart.getPid());
                                intent.putExtra("email",email);
                                startActivity(intent);
                            }
                            if(i==1)
                            {
                                cartlistRef.child("User View").child(values[0]).child("Products").child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        productidmap.remove(cart.getPid());
                                        Toast.makeText(CartActivity.this,"Item Removed",Toast.LENGTH_LONG).show();
                                    }
                                    }
                                });
                            }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder= new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
