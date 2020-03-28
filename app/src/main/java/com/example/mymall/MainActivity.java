package com.example.mymall;

import android.content.Intent;
import android.os.Bundle;

import com.example.mymall.Model.Products;
import com.example.mymall.Model.Rating;
import com.example.mymall.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        private FrameLayout frameLayout;
        private DatabaseReference ProductRef;
        private RecyclerView  recyclerView;
        RecyclerView.LayoutManager layoutManager;
        public String email;
        private int num,den;
        private DatabaseReference ratingref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras;
        extras=getIntent().getExtras();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ProductRef= FirebaseDatabase.getInstance().getReference().child("Products");
        ratingref=FirebaseDatabase.getInstance().getReference().child("Rating");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        View headerview = navigationView.getHeaderView(0);
        final TextView usernametextview = headerview.findViewById(R.id.mainfullname);
        email=extras.getString("email");
        final ImageView profileimageview= headerview.findViewById(R.id.mainprofileimage);
     //   usernametextview.setText(email);

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

                        Picasso.get().load(image).into(profileimageview);
                        usernametextview.setText(name);
                    }else
                    {
                        usernametextview.setText(email);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



       //   Picasso.get().load(Prevalant.currentOnlineUsers.getImage()).placeholder(R.drawable.common_google_signin_btn_icon_dark).into(profileimageview);

      //  frameLayout=findViewById(R.id.main_frame_layout);
       // setfragment(new HomeFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options= new FirebaseRecyclerOptions.Builder<Products>().setQuery(ProductRef,Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.productname.setText(products.getName());
                if(products.getRating()!= null)
                productViewHolder.productdescription.setText("Rating :"+products.getRating());
                else
                    productViewHolder.productdescription.setText("Rating :  N.A");
                productViewHolder.productprice.setText("Price :"+products.getPrice());
                num=0;
                den=0;

                Picasso.get().load(products.getPimg()).into(productViewHolder.imageview);
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(MainActivity.this,ProductDetailsActivity.class);
                        intent.putExtra("pid",products.getPid());
                        intent.putExtra("email",email);
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
                ProductViewHolder holder= new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_search_icon) {
            //search icon
            return true;
        }else if (id == R.id.main_notification_icon) {
            //notification
            return true;
        }else if (id == R.id.main_cart_icon) {
            //maincart
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_orders) {
            Toast.makeText(MainActivity.this,"clicked orders",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(MainActivity.this,MyOrdersActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
            finish();
            // Handle the camera action
        } else if (id == R.id.nav_my_rewards) {
            Intent intent =new Intent(MainActivity.this,AdminAddProduct.class);
            intent.putExtra("email",email);
            startActivity(intent);

        } else if (id == R.id.nav_my_cart) {
            Intent intent =new Intent(MainActivity.this,CartActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);


        } else if (id == R.id.nav_my_wishlist) {

            Intent intent =new Intent(MainActivity.this,My_Listings.class);
            intent.putExtra("email",email);
            startActivity(intent);

        } else if (id == R.id.nav_my_orders) {
            Toast.makeText(MainActivity.this,"clicked orders",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(MainActivity.this,MyOrdersActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
            finish();


        } else if (id == R.id.nav_my_account ) {
            Intent intent =new Intent(MainActivity.this,Settings.class);
            intent.putExtra("email",email);
            startActivity(intent);

        }else if (id == R.id.nav_sign_out) {
            Intent intent =new Intent(MainActivity.this,RegisterActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);


        }else if (id == R.id.nav_my_mall) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void setfragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
