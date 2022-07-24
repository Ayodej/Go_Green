package com.app.gogreen.activities.seller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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

import com.app.gogreen.R;
import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.databinding.ActivityViewExistingPlantsBinding;
import com.app.gogreen.models.PlantModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewExistingPlantsActivity extends BaseActivity {

    ActivityViewExistingPlantsBinding binding;
    DatabaseReference databaseReference;
    List<PlantModelClass> list;
    PlantsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_existing_plants);

        showProgressDialog();
        databaseReference = FirebaseDatabase.getInstance().getReference("Plants");
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                binding.textView.setText("");
                binding.recyclerView.setAdapter(null);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PlantModelClass model = snapshot.getValue(PlantModelClass.class);
                    list.add(model);
                }
                if(list.size()>0){
                    Collections.reverse(list);
                    adapter = new PlantsListAdapter(ViewExistingPlantsActivity.this, list);
                    binding.recyclerView.setAdapter(adapter);
                }else {
                    binding.textView.setText("No Plants!");
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
        List<PlantModelClass> filteredNames = new ArrayList<>();

        for (PlantModelClass s : list) {
            if (s.getTitle().toLowerCase().contains(str.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        try {
            adapter.filterList(filteredNames);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public class PlantsListAdapter extends RecyclerView.Adapter<PlantsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<PlantModelClass> muploadList;

        public PlantsListAdapter(Context context , List<PlantModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public PlantsListAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.plant_list_layout, parent , false);
            return (new PlantsListAdapter.ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final PlantsListAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final PlantModelClass plantModel = muploadList.get(position);
            holder.tvPlantName.setText("Title: "+plantModel.getTitle());
            holder.tvPrice.setText("Price: £"+plantModel.getPrice());
            Picasso.with(mcontext).load(plantModel.getPicUrl()).placeholder(R.drawable.loading).into(holder.imgPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewExistingPlantsActivity.this);
                    builder.setTitle("Confirmation?");
                    builder.setMessage("Are tou sure to delete this plant?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(plantModel.getId()).removeValue();
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
            public ImageView imgPic;
            public TextView tvPlantName;
            public TextView tvPrice;

            public ImageViewHolder(View itemView) {
                super(itemView);

                imgPic = itemView.findViewById(R.id.imgPic);
                tvPlantName = itemView.findViewById(R.id.tvPlantName);
                tvPrice = itemView.findViewById(R.id.tvPrice);

            }
        }

        public void filterList(List<PlantModelClass> searchList) {
            this.muploadList = searchList;
            notifyDataSetChanged();
        }
    }
}