package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StudAttPage extends AppCompatActivity {

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String nameKey;
    private String userName;
    private String subject;
    private String dateKey;
    private String studentTime;
    private Button refreshButton2;
    private TextView dateAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stud_att_page);

        refreshButton2 = (Button) findViewById(R.id.refreshButton2);
        dateAttendance = (TextView) findViewById(R.id.dateAttendance);



        userName = getIntent().getStringExtra("user_name");
        subject = getIntent().getStringExtra("subject");
        dateKey = getIntent().getStringExtra("dateKey");

        dateAttendance.setText(dateKey);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");

        LinearLayout linearLayout = findViewById(R.id.attendanceLayout);

        Query checkUser = reference.orderByChild("username").equalTo(userName);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                DataSnapshot firstChild = snapshot.child("courses");
                int count = 1;
                for (DataSnapshot ds : snapshot.child(userName).child("courses").child(subject).child("Date").child(dateKey).getChildren()){
                    nameKey = ds.getKey();
                    studentTime = ds.getValue(String.class);

//                    System.out.println(nameKey);
//                    System.out.println(studentTime);

                    TextView textView = new TextView(StudAttPage.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 5, 5, 5);
                    textView.setLayoutParams(params);

                    textView.setText(new StringBuilder().append(count).append(". ").append(nameKey).append(" - ").append(studentTime).toString());
                    textView.setPadding(10,10, 10, 10);
                    textView.setTextSize(15);


                    if (linearLayout != null) {
                        linearLayout.addView(textView);
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refreshButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


    }
}