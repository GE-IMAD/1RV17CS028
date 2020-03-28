package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mymall.Model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmFinalActivity extends AppCompatActivity {
    private EditText nameEditText,phoneEdutText,addressEditText,cityEditText;
    private Button confirmOrderbtn;
    private String Totalamount="";
    private String email;
    private String key;
    private String value;
    private String quantity_in_product;
    private int i;
    private int counter;
    private String buffer;
    public String prev;
    HashMap<String,String> productidmap=new HashMap<>();
    public final List<String> lis = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final);
        confirmOrderbtn=(Button)findViewById(R.id.shipment_confirm);
        nameEditText=(EditText)findViewById(R.id.shipment_name);
        phoneEdutText=(EditText)findViewById(R.id.shipment_phone_no);
        addressEditText=(EditText)findViewById(R.id.shipment_address);
        cityEditText=(EditText)findViewById(R.id.shipment_city);
        Totalamount=getIntent().getStringExtra("total Price");
        email=getIntent().getStringExtra("email");
        productidmap=(HashMap<String, String>)getIntent().getSerializableExtra("pids");
        confirmOrderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(ConfirmFinalActivity.this,"Name Empty",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phoneEdutText.getText().toString()))
        {
            Toast.makeText(ConfirmFinalActivity.this,"phone Empty",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(ConfirmFinalActivity.this,"Adress Empty",Toast.LENGTH_LONG).show();
        }
        else{
            confirmorder();
        }



    }

    private void confirmorder() {
        if (reduce_quantity() == 1) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("MM dd, yyyy");
            String savecurrentdate = currentdate.format(calendar.getTime());

            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
            String savecurrenttime = currenttime.format(calendar.getTime());

            String orderrandomkey = savecurrentdate + savecurrenttime;
            final String[] values = email.split("@");
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
            final DatabaseReference ordersref = FirebaseDatabase.getInstance().getReference().child("Orders").child(values[0]);
            HashMap<String, Object> OrdersMap = new HashMap<>();
            OrdersMap.put("Total Amount", Totalamount);
            OrdersMap.put("name", nameEditText.getText().toString());
            OrdersMap.put("Address", addressEditText.getText().toString());
            OrdersMap.put("City", cityEditText.getText().toString());
            OrdersMap.put("ProductMap", productidmap);
            OrdersMap.put("Order_id", orderrandomkey);
            ordersref.child(orderrandomkey).updateChildren(OrdersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(values[0]).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ConfirmFinalActivity.this, "Final Order has been Placed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        Intent intent=new Intent(ConfirmFinalActivity.this,MainActivity.class);
        //  intent.putExtra("pid",products.getPid());
        intent.putExtra("email",email);
        startActivity(intent);

        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (getApplicationContext()).setContentTitle("Listed Product").setContentText("Congragulations! Your order is confirmed ").
                setContentTitle("Placed Order").setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);
    }

    private int reduce_quantity() {
       final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        DatabaseReference product_field_ref;
        for (Map.Entry<String, String> e : productidmap.entrySet()) {
            key=e.getKey();
            value=e.getValue();
            counter=0;
         //   Toast.makeText(ConfirmFinalActivity.this,String.valueOf(counter)+"before",Toast.LENGTH_LONG).show();
            getProductDetails(key,value);
           // Toast.makeText(ConfirmFinalActivity.this,String.valueOf(counter),Toast.LENGTH_LONG).show();


        //    Toast.makeText(ConfirmFinalActivity.this,quantity_in_product,Toast.LENGTH_LONG).show();

                    }
        //counter=0;
        for (Map.Entry<String, String> e : productidmap.entrySet()) {
            key=e.getKey();
            value=e.getValue();
        //   i=Integer.parseInt(lis.get(counter))-Integer.parseInt(value);
          //  quantity_in_product=String.valueOf(i);
           // productRef.child(key).child("quantity").setValue(quantity_in_product);
          //  counter++;


            //    Toast.makeText(ConfirmFinalActivity.this,quantity_in_product,Toast.LENGTH_LONG).show();

        }



      return 1;

    }
    private void getProductDetails(final String productid , final String values) {
       final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    buffer=products.getQuantity();


                    if(!lis.contains(products.getPid())) {
                        Toast.makeText(ConfirmFinalActivity.this,products.getPid(),Toast.LENGTH_LONG).show();
                        i = Integer.parseInt(buffer) - Integer.parseInt(values);
                        quantity_in_product = String.valueOf(i);
                        productRef.child(productid).child("quantity").setValue(quantity_in_product);
                        prev="Ayush";
                        counter++;
                        lis.add(products.getPid());
                    }
                //    if(prev!=products.getPid())
                  //  {
                    //    Toast.makeText(ConfirmFinalActivity.this,"prev"+prev+"current"+products.getPid(),Toast.LENGTH_LONG).show();
                       // Toast.makeText(ConfirmFinalActivity.this,products.getPid()+"in else",Toast.LENGTH_LONG).show();
                  //      counter--;
                      // i = Integer.parseInt(buffer) - Integer.parseInt(values);
                       // quantity_in_product = String.valueOf(i);
                       // productRef.child(productid).child("quantity").setValue(quantity_in_product);
                       // prev=products.getPid();
                        //counter++;
                    //}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
