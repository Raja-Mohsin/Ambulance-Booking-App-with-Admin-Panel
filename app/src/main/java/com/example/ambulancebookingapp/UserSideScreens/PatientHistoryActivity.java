package com.example.ambulancebookingapp.UserSideScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ambulancebookingapp.R;
import com.example.ambulancebookingapp.adapters.AdapterLvPatientHistory;
import com.example.ambulancebookingapp.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PatientHistoryActivity extends AppCompatActivity {

    String userID, driverID, status, date, bookingID;
    int cost;

    ListView listView;
    AdapterLvPatientHistory adapter;
    ArrayList<Booking> items;
    ProgressBar progressBar;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String currentUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        listView = findViewById(R.id.LvPatientHistory);
        progressBar = findViewById(R.id.progressPatientHistory);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserKey = firebaseAuth.getCurrentUser().getUid();
        items = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Booking").whereEqualTo("userID", currentUserKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful())
                {
                    if(task.getResult().size() > 0)
                    {
                        for(DocumentSnapshot snapshot : task.getResult())
                        {
                            if(snapshot.get("status").toString().equals("completed"))
                            {
                                bookingID = snapshot.get("bookingID").toString();
                                userID = snapshot.get("userID").toString();
                                driverID = snapshot.get("driverID").toString();
                                status = snapshot.get("status").toString();
                                date = snapshot.get("date").toString();
                                cost = Integer.parseInt(snapshot.get("cost").toString());
                                Booking booking = new Booking(bookingID, userID, driverID, 0.0, 0.0, status, date, "", 0.0, 0.0, cost);
                                items.add(booking);
                            }
                        }
                        adapter = new AdapterLvPatientHistory(getApplicationContext(), items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(PatientHistoryActivity.this, UserReviewActivity.class);
                                intent.putExtra("bookingID", items.get(i).getBookingID());
                                intent.putExtra("userID", items.get(i).getUserID());
                                intent.putExtra("driverID", items.get(i).getDriverID());
                                startActivity(intent);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You have no completed rides", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}