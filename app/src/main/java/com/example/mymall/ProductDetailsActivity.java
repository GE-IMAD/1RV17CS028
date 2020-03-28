package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mymall.Model.Products;
import com.example.mymall.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.mymall.R.layout.review_item;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productimage;
    private ElegantNumberButton numberButton;
    private TextView productprice,productname,productdetails,productrating;
    private String productid="";
    private Button add_to_cart;
    private String email;
    private TextView quaantitytext;
    private TextView pricedynamic;
    public String quant;
    private DatabaseReference ratingref;
    public String price;
    private ListView listview;
    private String reviewtext;
    private String reviewarray[];
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
        pricedynamic=(TextView)findViewById(R.id.dynamic_price);
        productrating=(TextView)findViewById(R.id.product_rating);
        listview=(ListView)findViewById(R.id.product_reviews_listview);
        ratingref=FirebaseDatabase.getInstance().getReference().child("Rating");
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
        Intent intent=new Intent(ProductDetailsActivity.this,MainActivity.class);
      //  intent.putExtra("pid",products.getPid());
        intent.putExtra("email",email);
        startActivity(intent);

    }

    private void getProductDetails(final String productid) {
        ratingz();
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
                quaantitytext.setText("Quantity Available :"+products.getQuantity());
                quant=products.getQuantity();
                reviewtext=products.getReview();
                if(reviewtext != null)
                    reviewarray = reviewtext.split("@");
                if(reviewtext != null) {
                    reviewarray = Arrays.copyOfRange(reviewarray, 1, reviewarray.length);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductDetailsActivity.this, review_item,reviewarray);

                    listview.setAdapter(adapter);
                }
                Picasso.get().load(products.getPimg()).into(productimage);

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ratingz() {
        ratingref.child(productid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    {
                        Rating ratings = dataSnapshot.getValue(Rating.class);
                        Toast.makeText(ProductDetailsActivity.this,ratings.getNum()+" value", Toast.LENGTH_SHORT).show();
                        productrating.setText("Average Rating  : "+String.valueOf(Integer.valueOf(ratings.getNum())/Integer.valueOf(ratings.getDen())));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void calprice(View view) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        Toast.makeText(ProductDetailsActivity.this,"clicked",Toast.LENGTH_SHORT).show();
        productRef.child(productid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    price=String.valueOf(Integer.valueOf(products.getPrice())*Integer.valueOf(numberButton.getNumber()));
                    pricedynamic.setText("Price : "+price);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
