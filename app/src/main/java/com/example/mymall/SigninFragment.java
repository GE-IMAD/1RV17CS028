package com.example.mymall;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymall.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {

    private EditText email;
    private EditText password;
    private ImageButton closebtn;
    private Button signinBtn;
    private Button admin;
    private FirebaseAuth firebaseauth;
    private  String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private TextView forgotpassword;
    public SigninFragment() {
        // Required empty public constructor
    }

    private TextView donthaveanaccount;
    private FrameLayout parentframelayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_signin, container, false);
        donthaveanaccount=view.findViewById(R.id.haveanaccount);
        parentframelayout=getActivity().findViewById(R.id.registerframelayout);
        email=view.findViewById(R.id.sign_in_email);
        password=view.findViewById(R.id.sign_in_password);
     //   closebtn=view.findViewById(R.id.sign_in_close_btn);
        signinBtn=view.findViewById(R.id.sign_in_btn);
        firebaseauth=FirebaseAuth.getInstance();
        forgotpassword=view.findViewById(R.id.sign_in_forgot_password);
     //   admin=view.findViewById(R.id.login_as_admin_btn);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        donthaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkemail();
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new ResetPasswordFragment());
            }
        });
      //  admin.setOnClickListener(new View.OnClickListener() {
       //     @Override
         ///   public void onClick(View v) {
            //    checkemaila();
           // }
       // });
    }
    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(parentframelayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void checkInputs(){
        if(!TextUtils.isEmpty(email.getText())){

                if(!TextUtils.isEmpty(password.getText()) && password.length()<8){
                    signinBtn.setEnabled(true);

                }else
                {
                    signinBtn.setEnabled(true);
                }

            }else{

                signinBtn.setEnabled(false);
            }

    }
    private void checkemail(){
        final DatabaseReference Rootref;
        String CSV = email.getText().toString();

        final String[] values = CSV.split("@");
        Rootref= FirebaseDatabase.getInstance().getReference().child("Users");
        if(email.getText().toString().matches(emailPattern)){
            if(password.length()>8){
                firebaseauth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(values[0]).exists()) {
                                        Users userdata = dataSnapshot.child(values[0]).getValue(Users.class);
                                    }
                                    else{
                                        //Toast.makeText(getActivity(),"Account not found",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            Intent mainactivity=new Intent(getActivity(),MainActivity.class);
                            mainactivity.putExtra("email",email.getText().toString());
                            startActivity(mainactivity);
                            getActivity().finish();
                        }else{
                            String error=task.getException().getMessage();
                            Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else{
                Toast.makeText(getActivity(),"incorrect email or password",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getActivity(),"incorrect email or password",Toast.LENGTH_LONG).show();
        }

    }
    private void checkemaila()
    {
        if(email.getText().toString().matches(emailPattern)){
            if(password.length()>8){
                firebaseauth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent mainactivity=new Intent(getActivity(),AdminAddProduct.class);
                            startActivity(mainactivity);
                            getActivity().finish();
                        }else{
                            String error=task.getException().getMessage();
                            Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else{
                Toast.makeText(getActivity(),"incorrect email or password",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getActivity(),"incorrect email or password",Toast.LENGTH_LONG).show();
        }
    }
}
