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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private TextView haveanaccount;
    private FrameLayout parentframelayout;
   private EditText email;
   private  EditText fullName;
   private EditText password;
   //private EditText confirmpassword;
   private ImageButton closeBtn;
   private Button signupBtn;
   private FirebaseAuth firebaseauth;
   private String emailpattern;
    private DatabaseReference ProductRef;
   private FirebaseFirestore firebaseFirestore;
   private  String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        haveanaccount=view.findViewById(R.id.haveanaccount);
        parentframelayout=getActivity().findViewById(R.id.registerframelayout);
        email=view.findViewById(R.id.sign_up_email);
        fullName=view.findViewById(R.id.sign_up_fullname);
        password=view.findViewById(R.id.sign_up_password);
       // closeBtn=view.findViewById(R.id.sign_up_close_btn);
        signupBtn=view.findViewById(R.id.sign_up_btn);
        firebaseauth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        haveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());

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
        fullName.addTextChangedListener(new TextWatcher() {
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
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmail();

            }
        });
    }
    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(parentframelayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void checkInputs(){
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(fullName.getText()))
            {
                if(!TextUtils.isEmpty(password.getText()) && password.length()<8){
                    signupBtn.setEnabled(true);

                }else
                {
                    signupBtn.setEnabled(true);
                }

            }else{

                signupBtn.setEnabled(false);
            }
        }else{
            signupBtn.setEnabled(false);

        }
    }
    private void checkEmail()
    {
        if(email.getText().toString().matches(emailPattern)){
            firebaseauth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<Object,String> userdata = new HashMap<>();
                    userdata.put("fullname",fullName.getText().toString());
                    firebaseFirestore.collection("USERS").add(userdata).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                HashMap<String,Object> Usermap=new HashMap<>();
                                Usermap.put("address","empty");
                                Usermap.put("email",email.getText().toString());
                                Usermap.put("full_name",fullName.getText().toString());
                                Usermap.put("ph_no","empty");
                                String CSV = email.getText().toString();

                                String[] values = CSV.split("@");
                                ProductRef.child(values[0]).updateChildren(Usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getActivity(),"User added",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                Intent mainintent=new Intent(getActivity(),MainActivity.class);
                                mainintent.putExtra("email",email.getText().toString());
                                startActivity(mainintent);
                                getActivity().finish();
                            }else{
                                String error=task.getException().getMessage();
                                Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }
                else{
                    String error=task.getException().getMessage();
                    Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                }
                }
            });


        }else{
            email.setError("Invalid Email");

        }
    }
}
