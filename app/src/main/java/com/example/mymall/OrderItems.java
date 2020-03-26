package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymall.Model.Products;
import com.example.mymall.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OrderItems extends AppCompatActivity {
    public ListView listView;
    public String[] items;
    public String[] values;
    public String email;
    public String[] itemarray;
    public TextView text;
    private RecyclerView recyclerView;
    private DatabaseReference ProductRef;
    private TextView nametxt;
    private TextView quantxt;

    RecyclerView.LayoutManager layoutManager;
    HashMap<String,String> productidmap=new HashMap<>();
  //  final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.order_item_items,items);
    public final List<String> lis = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);
        Bundle extras;
        extras=getIntent().getExtras();
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        ArrayList<Object> listitems = (ArrayList<Object>) args.getSerializable("ARRAYLIST");
        email=extras.getString("email");
        values = email.split("@");
        productidmap=(HashMap<String, String>)getIntent().getSerializableExtra("productmap");
        recyclerView = findViewById(R.id.recycler_order);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ProductRef= FirebaseDatabase.getInstance().getReference().child("Products");
        nametxt=(TextView)findViewById(R.id.order_item_name);
        quantxt=(TextView)findViewById(R.id.order_item_quantity);

        //  items=extras.getStringArray("pmap");
       // ArrayList<String> list = new ArrayList<String>();
       // for (String s : items)
         //   if (!s.equals(""))
           //     list.add(s);
       // items = listitems.toArray(new String[listitems.size()]);
       // ListView listView = (ListView) findViewById(R.id.order_items_listview1);
       // text=(TextView)findViewById(R.id.orderitemid);
        //Toast.makeText(OrderItems.this,items[0],Toast.LENGTH_LONG).show();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.order_item_items,items);

      //  listView.setAdapter(adapter);



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options= new FirebaseRecyclerOptions.Builder<Products>().setQuery(ProductRef,Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                if(productidmap.containsKey(products.getPid())) {
                    productViewHolder.ordername.setText("Product Name : "+products.getName());
                    productViewHolder.quantity.setText("Product Quantity : "+productidmap.get(products.getPid()));

                }

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_item,parent,false);
                ProductViewHolder holder= new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    }


