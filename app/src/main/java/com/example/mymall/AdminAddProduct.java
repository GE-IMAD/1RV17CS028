package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProduct extends AppCompatActivity {

    private EditText productname,productprice,productdescription,productquantity;
    private Button post;
    private ImageView productimage;
    private static final int Gallerypick=1;
    private Uri Imageuri;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;
    private FirebaseAuth firebaseAuth;
    private String email;
    private String values[];
    private String productName,productPrice,productDescription,savecurrentdate,savecurrenttime,productrandomkey,downloadimageurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);
        productname=(EditText)findViewById(R.id.admin_product_name);
        productprice=(EditText)findViewById(R.id.admin_product_price);
        productdescription=(EditText)findViewById(R.id.admin_product_Description);
        productquantity=(EditText)findViewById(R.id.admin_product_quantity);
        post=(Button) findViewById(R.id.admin_post_button);
        productimage=(ImageView) findViewById(R.id.productimage);
        email=getIntent().getStringExtra("email");
        values = email.split("@");
        firebaseAuth=FirebaseAuth.getInstance();
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ProductImageRef= FirebaseStorage.getInstance().getReference().child("Product_Images");
        productimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Opengallery();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateproductdata();
            }
        });
    }
    private void Opengallery(){
        Intent galleryintent=new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        Toast.makeText(AdminAddProduct.this,"in",Toast.LENGTH_SHORT).show();
        startActivityForResult(galleryintent,Gallerypick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(AdminAddProduct.this,"in if",Toast.LENGTH_LONG).show();
        if(requestCode==Gallerypick && resultCode== RESULT_OK && data!=null){
            Toast.makeText(AdminAddProduct.this,"in if",Toast.LENGTH_SHORT).show();
            Imageuri=data.getData();
            productimage.setImageURI(Imageuri);
        }
    }
    private void validateproductdata(){
        productDescription=productdescription.getText().toString();
        productPrice=productprice.getText().toString();
        productName=productname.getText().toString();
        if(Imageuri == null)
        {
            Toast.makeText(this,"image not selected",Toast.LENGTH_SHORT).show();
        }
        else{
            StoreProductInformation();
        }
    }
    private void StoreProductInformation()
    {
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentdate= new SimpleDateFormat("MM dd, yyyy");
        savecurrentdate=currentdate.format(calendar.getTime());

        SimpleDateFormat currenttime= new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime=currenttime.format(calendar.getTime());

        productrandomkey=savecurrentdate+savecurrenttime;

        final StorageReference filepath=ProductImageRef.child(Imageuri.getLastPathSegment() + productrandomkey +".jpg");
        final UploadTask uploadTask=filepath.putFile(Imageuri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AdminAddProduct.this,message,Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadimageurl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        downloadimageurl = task.getResult().toString();
                        Toast.makeText(AdminAddProduct.this,"getting productimage url",Toast.LENGTH_SHORT).show();
                        saveproductinfotodatabase();
                    }
                    }
                });
            }
        });
        Intent intent=new Intent(AdminAddProduct.this,MainActivity.class);
        //  intent.putExtra("pid",products.getPid());
        intent.putExtra("email",email);
        startActivity(intent);

    }
    private void saveproductinfotodatabase(){
        HashMap<String,Object> productmap=new HashMap<>();
        productmap.put("pid",productrandomkey);
        productmap.put("date",savecurrentdate);
        productmap.put("time",savecurrenttime);
        productmap.put("description",productDescription);
        productmap.put("Name",productName);
        productmap.put("price",productPrice);
        productmap.put("pimg",downloadimageurl);
        productmap.put("SoldBy",values[0]);
        productmap.put("quantity",productquantity.getText().toString());
        ProductRef.child(productrandomkey).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminAddProduct.this,"product added",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AdminAddProduct.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (getApplicationContext()).setContentTitle("Listed Product").setContentText("Congragulations! You have listed"+productName).
                setContentTitle("Listed Product").setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);
    }
}
