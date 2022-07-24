package com.app.gogreen.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.gogreen.R;
import com.app.gogreen.activities.buyer.MainActivity;
import com.app.gogreen.models.OrderModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SellerResponseFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    Context context;
    View view;

    RecyclerView recyclerView;
    TextView textView;
    DatabaseReference databaseReference;
    List<OrderModelClass> list;
    EventsListAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_seller_response, container, false);

        context = container.getContext();

        showProgressDialog();
        databaseReference = FirebaseDatabase.getInstance().getReference("PlantOrders");
        list = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textView = view.findViewById(R.id.textView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                textView.setText("");
                recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    OrderModelClass model = snapshot.getValue(OrderModelClass.class);
                    if(MainActivity.userId.equals(model.getBuyerId())){
                        list.add(model);
                    }
                }
                if(list.size()>0){
                    Collections.reverse(list);
                    adapter = new EventsListAdapter(context, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No Orders!");
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressDialog();
                Toast.makeText(context, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<OrderModelClass> muploadList;

        public EventsListAdapter(Context context , List<OrderModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public EventsListAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.orders_list_layout, parent , false);
            return (new EventsListAdapter.ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final EventsListAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final OrderModelClass order = muploadList.get(position);
            holder.tvCustomerName.setText("Order Status: "+order.getFlag());
            holder.tvItemName.setText("Item : "+order.getItemName());
            holder.tvQty.setText("Quantity : "+order.getQuantity());

            float price = Float.parseFloat(order.getItemPrice());
            int qty = Integer.parseInt(order.getQuantity());
            holder.tvTotal.setText("Total : "+price+" * "+qty+" = GBP"+price*qty);

            holder.imgLoc.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading data..");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
