package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherViewStudents extends AppCompatActivity {

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String userName;
    private Button viewStudentRefreshBtn;
    private Button summaryBtn;
    private String subject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_students);

        viewStudentRefreshBtn = (Button) findViewById(R.id.viewStudentRefreshBtn);
        summaryBtn = (Button) findViewById(R.id.summaryBtn);

        userName = getIntent().getStringExtra("user_name");
        subject = getIntent().getStringExtra("subject");

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");

        summaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherViewStudents.this, TeacherSummary.class);
                intent.putExtra("user_name", userName);
                intent.putExtra("subject", subject);
                startActivity(intent);
            }
        });

        LinearLayout linearLayout = findViewById(R.id.listStudentLayout);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                DataSnapshot firstChild = snapshot.child("courses");
                int count = 1;
                for (DataSnapshot ds : snapshot.child(userName).child("courses").child(subject).child("Students").getChildren()){
                    String studNameKey = ds.getKey();


                    TextView textView = new TextView(TeacherViewStudents.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 5, 5, 5);
                    textView.setLayoutParams(params);

                    textView.setText(new StringBuilder().append(count).append(". ").append(studNameKey).toString());
                    textView.setPadding(10,10, 10, 10);
                    textView.setTextSize(15);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TeacherViewStudents.this, TeacherIndivStudents.class);
                            intent.putExtra("user_name", userName);
                            intent.putExtra("subject",subject);
                            intent.putExtra("studentName", studNameKey);
                            startActivity(intent);
                        }
                    });

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




    }
}