package com.example.rvlistcitiesfirestore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<City> myCitiesList;
    private final CustonItemClick listener;

    public MyAdapter(Context context, List<City> myCitiesList, CustonItemClick listener) {
        this.context = context;
        this.myCitiesList = myCitiesList;
        this.listener = listener;
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        private TextView city;
        private TextView tvCommunity;
        private Button delete;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.city);
            tvCommunity = itemView.findViewById(R.id.tvCommunity);
            delete = itemView.findViewById(R.id.delete);
        }

        public void setData(String name , String community) {
            city.setText(name);
            tvCommunity.setText(community);

        }
    }
        @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.cities, parent, false);
            MyHolder holder = new MyHolder(v);
            return holder;
        }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        City city = myCitiesList.get(position);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast toast = Toast.makeText(context, myCitiesList.get(position).getName() + myCitiesList.get(position).getCommunity(), Toast.LENGTH_SHORT);
                //toast.show();
                //listener.onClickListener(position);
            }
        });

        /*myHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongitemClick(position);
                return true;
            }
        });*/

        myHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });

        myHolder.setData(city.getName(),city.getCountry());
    }

    @Override
    public int getItemCount() {
        return myCitiesList.size();
    }

    public void add(City item) {
        myCitiesList.add(item);
    }

}















