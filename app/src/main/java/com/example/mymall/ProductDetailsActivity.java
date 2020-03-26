package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mymall.Model.Products;
import com.example.mymall.Prevalant.Prevalant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productimage;
    private ElegantNumberButton numberButton;
    private TextView productprice,productname,productdetails;
    private String productid="";
    private Button add_to_cart;
    private String email;
    private TextView quaantitytext;
    public String quant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        numberButton=(ElegantNumberButton) findViewById(R.id.number_btn);
        productimage=(ImageView) findViewById(R.id.product_image_details);
        productname=(TextView) findViewById(R.id.product_name_details);
        productprice=(TextView) findViewById(R.id.product_price_details);
        productdetails=(TextView) findViewById(R.id.product_description_details);
        add_to_cart=(Button)findViewById(R.id.pd_add_to_cart_button);
        quaantitytext=(TextView)findViewById(R.id.quantity_available);
        productid=getIntent().getStringExtra("pid");
        email=getIntent().getStringExtra("email");
        getProductDetails(productid);
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Integer.parseInt(quant)>Integer.parseInt(numberButton.getNumber()))
                    adding_to_cart_list();
                else
                    Toast.makeText(ProductDetailsActivity.this,"Invalid Quantityt",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void adding_to_cart_list() {
        Toast.makeText(ProductDetailsActivity.this,"Product"+email,Toast.LENGTH_LONG).show();
        final String[] values = email.split("@");
        String saveCurrenttime,saveCurrentDate;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");

        saveCurrentDate=currentDate.format(callForDate.getTime());

        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss a");

        saveCurrenttime=currentDate.format(callForDate.getTime());

        final DatabaseReference cartListref = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap=new HashMap<>();
        cartMap.put("pid",productid);
        cartMap.put("pname",productname.getText().toString());
        cartMap.put("price",productprice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrenttime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");
        cartListref.child("User View").child(values[0]).child("Products").child(productid).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    cartListref.child("Admin View").child(values[0]).child("Products").child(productid).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ProductDetailsActivity.this,"Added to cart",Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                    }
            }
        });

    }

    private void getProductDetails(String productid) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                Products products=dataSnapshot.getValue(Products.class);
                productname.setText(products.getName());
                productprice.setText(products.getPrice());
                productdetails.setText(products.getDescription());
                quaantitytext.setText(products.getQuantity());
                quant=products.getQuantity();
                Picasso.get().load(products.getPimg()).into(productimage);

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
