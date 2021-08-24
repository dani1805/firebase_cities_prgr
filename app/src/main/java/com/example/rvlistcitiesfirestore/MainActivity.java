package com.example.rvlistcitiesfirestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<City> cities;
    MyAdapter adapter;
    int index = 0;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");

        RecyclerView myRecyclerView = findViewById(R.id.myRecyclerView);
        Button add = findViewById(R.id.add);
        Button clearAll = findViewById(R.id.clearAll);

        myRecyclerView.setHasFixedSize(true);

        cities = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        myRecyclerView.setLayoutManager(llm);

        FirebaseUser user = mAuth.getCurrentUser();
        usersRef.document(user.getEmail()).collection("cities").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        cities.add(new City(documentSnapshot.getData().get("name").toString(),documentSnapshot.getData().get("country").toString()));
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter = new MyAdapter(MainActivity.this, cities, new CustonItemClick() {
            @Override
            public void onItemClick(int position) {
                String name = cities.get(position).getName();

                cities.remove(position);
                adapter.notifyDataSetChanged();

                FirebaseUser user = mAuth.getCurrentUser();

                usersRef.document(user.getEmail()).collection("cities").whereEqualTo("name", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                usersRef.document(user.getEmail()).collection("cities").document(documentSnapshot.getId()).delete();
                            }
                        }
                    }
                });
            }
        });
        myRecyclerView.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Mi lista de destinos");
        }

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cities.clear();
                //adapter.notifyDataSetChanged();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, secondActivity.class);
                startActivityForResult(intent,3);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                //cities.clear();
                //adapter.notifyDataSetChanged();
                break;
            case R.id.add: {
                Intent intent = new Intent(MainActivity.this, secondActivity.class);
                startActivityForResult(intent,3);
            }
            break;
            case R.id.logout: {
                mAuth.signOut();

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser == null) {
                    startActivity();
                }
            }
            break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 3) {
            String city = data.getStringExtra("city");
            String country = data.getStringExtra("country");
            City mCity = new City(city, country);

            adapter.add(mCity);
            adapter.notifyDataSetChanged();

            FirebaseUser user = mAuth.getCurrentUser();

            City cityDb = new City(city, country);
            usersRef.document(user.getEmail()).collection("cities").add(cityDb);
        }

    }

    private void startActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
