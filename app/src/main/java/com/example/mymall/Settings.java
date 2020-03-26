package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymall.Prevalant.Prevalant;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class Settings extends AppCompatActivity {
    private ImageView profileimage;
    private EditText fullnameedit,addressedit,phoneedit;
    private TextView profilechangetextbtn,closetxtbtn,savetxtbtn;
    public String email;
    private Uri imageUri;
    private String myUrl="";
    private StorageReference storageProfilePictureRef;
    private String checker ="";
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras;
        extras=getIntent().getExtras();
        setContentView(R.layout.activity_settings);
        profileimage=(ImageView)findViewById(R.id.settings_profile_image);
        fullnameedit=(EditText)findViewById(R.id.settings_full_name);
        addressedit=(EditText)findViewById(R.id.settings_ad);
        phoneedit=(EditText)findViewById(R.id.settings_phone_number);
        profilechangetextbtn=(TextView)findViewById(R.id.profile_image_change);
        closetxtbtn=(TextView)findViewById(R.id.close_Settings);
        savetxtbtn=(TextView)findViewById(R.id.update_settings);
        email = extras.getString("email");
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        userInfoDisplay(profileimage,fullnameedit,addressedit,phoneedit,profilechangetextbtn,closetxtbtn,savetxtbtn);
        closetxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        savetxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }
                else{
                    updateonlyuserinfo();
                }
            }
        });
        profilechangetextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(Settings.this);
            }
        });
    }

    private void updateonlyuserinfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> Usermap=new HashMap<>();
        Usermap.put("address",addressedit.getText().toString());
        Usermap.put("email",email);
        Usermap.put("full_name",fullnameedit.getText().toString());
        Usermap.put("ph_no",phoneedit.getText().toString());

        //    String CSV = email.getText().toString();

        String[] values = email.split("@");
        ref.child(values[0]).updateChildren(Usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Settings.this,"User updates",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Settings.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            //    startActivity(new Intent(Settings.this,MainActivity.class));
             //   finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri= result.getUri();
            profileimage.setImageURI(imageUri);

        }else{
            Toast.makeText(Settings.this,"error",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.this,Settings.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullnameedit.getText().toString()))
        {
            Toast.makeText(Settings.this,"error name",Toast.LENGTH_SHORT).show();
        }else  if(TextUtils.isEmpty(addressedit.getText().toString()))
        {
            Toast.makeText(Settings.this,"error name",Toast.LENGTH_SHORT).show();
        }else  if(TextUtils.isEmpty(phoneedit.getText().toString()))
        {
            Toast.makeText(Settings.this,"error name",Toast.LENGTH_SHORT).show();
        }else  if(checker.equals("clicked")){
            uploadImage();
        }


    }

    private void uploadImage() {
        if(imageUri!=null){
            Toast.makeText(Settings.this,email+"is",Toast.LENGTH_LONG).show();
            final StorageReference fileRef = storageProfilePictureRef.child(email+".jpg");
            uploadTask =fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri dowloadUrl=task.getResult();
                        myUrl=dowloadUrl.toString();
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> Usermap=new HashMap<>();
                        Usermap.put("address",addressedit.getText().toString());
                        Usermap.put("email",email);
                        Usermap.put("full_name",fullnameedit.getText().toString());
                        Usermap.put("ph_no",phoneedit.getText().toString());
                        Usermap.put("image",myUrl);
                    //    String CSV = email.getText().toString();

                        String[] values = email.split("@");
                        ref.child(values[0]).updateChildren(Usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Settings.this,"User updates",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(Settings.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                }
                              //  startActivity(new Intent(Settings.this,MainActivity.class).putExtra("email",email));

                              //  finish();
                            }
                        });
                    }else{
                        Toast.makeText(Settings.this,"error updating",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(Settings.this,"image not selected",Toast.LENGTH_LONG).show();
        }
    }

    private void userInfoDisplay(final ImageView profileimage, final EditText fullnameedit, final EditText addressedit, final EditText phoneedit, TextView profilechangetextbtn, TextView closetxtbtn, TextView savetxtbtn) {

        final String[] values = email.split("@");
        DatabaseReference Userref = FirebaseDatabase.getInstance().getReference().child("Users").child(values[0]);
        Userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists())
                    {
                        String image=dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("full_name").getValue().toString();
                        String email=dataSnapshot.child("email").getValue().toString();
                        String address=dataSnapshot.child("address").getValue().toString();
                        String phoneno=dataSnapshot.child("ph_no").getValue().toString();
                        Picasso.get().load(image).into(profileimage);
                        fullnameedit.setText(name);
                        addressedit.setText(address);
                        phoneedit.setText(phoneno);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
