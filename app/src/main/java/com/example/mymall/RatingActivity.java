package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mymall.Model.Products;
import com.example.mymall.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RatingActivity extends AppCompatActivity {

    private String values[];
    private String email;
    private String orderid,productid,quantity;
    HashMap<String,String> productidmap=new HashMap<>();
    private EditText ratingvalue,revieweditext;
    private Button submit;
    private DatabaseReference ProductRef;
    private DatabaseReference rootref;
    private String reviewlocal;
    int counter,num,den,counter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Bundle extras;
        extras=getIntent().getExtras();
        Bundle args = getIntent().getBundleExtra("BUNDLE");
     //   ArrayList<Object> listitems = (ArrayList<Object>) args.getSerializable("ARRAYLIST");
        email=extras.getString("email");
        orderid=extras.getString("orderid");
        productid=extras.getString("productid");
        quantity=extras.getString("quantity");
        values = email.split("@");
        productidmap=(HashMap<String, String>)getIntent().getSerializableExtra("productmap");
        ratingvalue=(EditText)findViewById(R.id.order_item_rating);
        revieweditext=(EditText)findViewById(R.id.order_item_review);
        submit=(Button)findViewById(R.id.rating_submit);
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Rating").child(productid);
        rootref=FirebaseDatabase.getInstance().getReference().child("Products").child(productid);
        counter=0;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter=0;
                ProductRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            if(counter!=1){

                            Rating rating = dataSnapshot.getValue(Rating.class);
                            num=Integer.valueOf(rating.getNum())+Integer.valueOf(ratingvalue.getText().toString())*Integer.valueOf(quantity);
                            den=Integer.valueOf(quantity)+Integer.valueOf(rating.getDen());
                            HashMap<String, Object> ratingmap = new HashMap<>();
                            ratingmap.put("num", String.valueOf(num));
                            ratingmap.put("den", String.valueOf(den));
                            ProductRef.updateChildren(ratingmap);
                            num=num/den;
                            rootref.child("rating").setValue(String.valueOf(num));
                            counter = 1;}

                        }
                        else{
                            if(counter!=1) {
                                HashMap<String, Object> ratingmap = new HashMap<>();
                                num=Integer.valueOf(ratingvalue.getText().toString())*Integer.valueOf(quantity);
                                ratingmap.put("num", String.valueOf(num));
                                ratingmap.put("den", quantity);
                                ProductRef.updateChildren(ratingmap);
                                rootref.child("rating").setValue(String.valueOf(num/Integer.valueOf(quantity)));
                                counter = 1;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                HashMap<String,Object> ratingmap=new HashMap<>();
                ratingmap.put("ratingvalue",ratingvalue.getText().toString());
                ratingmap.put("quantity",quantity);
            //    ProductRef.updateChildren(ratingmap);
                counter1 =0;
                rootref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            if(counter1!=1) {
                                Products product = dataSnapshot.getValue(Products.class);
                                rootref.child("review").setValue(product.getReview() + "@" + revieweditext.getText().toString());
                                counter1 = 1;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                Intent intent=new Intent(RatingActivity.this,MainActivity.class);
                //  intent.putExtra("pid",products.getPid());
                intent.putExtra("email",email);
                startActivity(intent);

            }

        });
    }
}
