package com.app.gogreen.activities.seller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.databinding.ActivityViewBuyerOrdersBinding;
import com.app.gogreen.models.OrderModelClass;
import com.app.gogreen.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewBuyerOrdersActivity extends BaseActivity {

    ActivityViewBuyerOrdersBinding binding;
    DatabaseReference databaseReference;
    List<OrderModelClass> list;
    OrdersListAdapter adapter;
    public static OrderModelClass model;
    String str ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_buyer_orders);

        showProgressDialog();
        databaseReference = FirebaseDatabase.getInstance().getReference("PlantOrders");
        list = new ArrayList<>();

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrderList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }});

    }

    @Override
    protected void onStart() {
        super.onStart();

        str = "pending";

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                binding.textView.setText("");
                binding.recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderModelClass model = snapshot.getValue(OrderModelClass.class);
                    if(str.equals(model.getFlag())){
                        list.add(model);
                    }
                }
                if(list.size()>0){
                    Collections.reverse(list);
                    adapter = new OrdersListAdapter(ViewBuyerOrdersActivity.this, list);
                    binding.recyclerView.setAdapter(adapter);
                }else {
                    binding.textView.setText("No Plant Orders!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrderList(String str) {
        List<OrderModelClass> filteredNames = new ArrayList<>();

        for (OrderModelClass s : list) {
            if (s.getItemName().toLowerCase().contains(str.toLowerCase())) {
                filteredNames.add(s);
            }
        }

        try {
            adapter.filterList(filteredNames);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<OrderModelClass> muploadList;

        public OrdersListAdapter(Context context , List<OrderModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.orders_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final OrderModelClass order = muploadList.get(position);
            holder.tvCustomerName.setText("Buyer : "+order.getBuyerName());
            holder.tvItemName.setText("Item : "+order.getItemName());
            holder.tvQty.setText("Quantity : "+order.getQuantity());

            float price = Float.parseFloat(order.getItemPrice());
            int qty = Integer.parseInt(order.getQuantity());
            holder.tvTotal.setText("Total : "+price+" * "+qty+" = GBP"+price*qty);

            holder.imgLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model = order;
                    startActivity(new Intent(getApplicationContext(), ViewUserLocationActivity.class));
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Did you accept or reject this order?").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(order.getId()).child("flag").setValue("Accepted");
                            list.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Order Accepted", Toast.LENGTH_SHORT).show();

                        }
                    }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(order.getId()).child("flag").setValue("Rejected");
                            list.remove(position);
                            notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(), "Order Rejected", Toast.LENGTH_SHORT).show();

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder{
            public TextView tvCustomerName;
            public TextView tvItemName;
            public TextView tvQty;
            public TextView tvTotal;
            public ImageView imgLoc;

            public ImageViewHolder(View itemView) {
                super(itemView);

                tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
                tvItemName = itemView.findViewById(R.id.tvItemName);
                tvQty = itemView.findViewById(R.id.tvQty);
                tvTotal = itemView.findViewById(R.id.tvTotal);
                imgLoc = itemView.findViewById(R.id.imgLoc);
            }
        }

        public void filterList(List<OrderModelClass> searchList) {
            this.muploadList = searchList;
            notifyDataSetChanged();
        }
    }
}