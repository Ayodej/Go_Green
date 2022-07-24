package com.app.gogreen.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.gogreen.R;
import com.app.gogreen.activities.buyer.PlantDetailsActivity;
import com.app.gogreen.models.PlantModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    Context context;
    View view;
    RecyclerView recyclerView;
    TextView textView;
    DatabaseReference databaseReference;
    List<PlantModelClass> list;
    EditText edtSearch;
    PlantsListAdapter adapter;
    public static PlantModelClass model;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        context = container.getContext();

        showProgressDialog();
        databaseReference = FirebaseDatabase.getInstance().getReference("Plants");
        list = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        textView = view.findViewById(R.id.textView);
        edtSearch = view.findViewById(R.id.edtSearch);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPlantList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }});

        return view;
    }


    //Get all the accepted events from firebase in this function and set them up in a list..
    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                recyclerView.setAdapter(null);
                textView.setText("");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PlantModelClass model = snapshot.getValue(PlantModelClass.class);
                    list.add(model);
                }
                if(list.size()>0){
                    adapter = new PlantsListAdapter(context, list);
                    recyclerView.setAdapter(adapter);
                }else {
                    textView.setText("No items");
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

    private void filterPlantList(String text) {
        List<PlantModelClass> filterdNames = new ArrayList<>();

        for (PlantModelClass s : list) {
            if (s.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }

        try {
            adapter.filterList(filterdNames);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static class PlantsListAdapter extends RecyclerView.Adapter<PlantsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<PlantModelClass> muploadList;

        public PlantsListAdapter(Context context , List<PlantModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.plant_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, final int position) {

            final PlantModelClass plantModel = muploadList.get(position);
            holder.tvPlantName.setText("Title: "+plantModel.getTitle());
            holder.tvPrice.setText("Price: £"+plantModel.getPrice());
            Picasso.with(mcontext).load(plantModel.getPicUrl()).placeholder(R.drawable.loading).into(holder.imgPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model = plantModel;
                    mcontext.startActivity(new Intent(mcontext, PlantDetailsActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public static  class ImageViewHolder extends RecyclerView.ViewHolder{
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

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading");
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
