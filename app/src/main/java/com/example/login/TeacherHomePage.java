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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TeacherHomePage extends AppCompatActivity {

    private Button logOutButton;
    private Button addSubButton;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String userName;
    private Button timeRestrictionsBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home_page);


        logOutButton = (Button) findViewById(R.id.logOutButton2);
        addSubButton = (Button) findViewById(R.id.addSubButton);
        timeRestrictionsBtn = (Button) findViewById(R.id.timeRestrictionsBtn);



        userName = getIntent().getStringExtra("user_name");

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");


//        LinearLayout linearLayout = new LinearLayout(TeacherHomePage.this);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linearLayout = findViewById(R.id.rootlayout);

        Query checkUser = reference.orderByChild("username").equalTo(userName);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                DataSnapshot firstChild = snapshot.child("courses");
                for (DataSnapshot ds : snapshot.child(userName).child("courses").getChildren()){
//                    String key = ds.getKey();
//                    System.out.println("-----------------------------------------------");
                    String subject = ds.child("Subject").getValue(String.class);
                    String time = ds.child("Time").getValue(String.class);
//                    System.out.println(subject);
//                    System.out.println(time);
                    String subjectTime = new StringBuilder().append(subject).append(" ").append(time).toString();

                    TextView textView = new TextView(TeacherHomePage.this);

//                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(30, 20, 20, 30);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    textView.setLayoutParams(params);

                    textView.setText(subjectTime);
                    textView.setPadding(50,50, 50, 50);
                    textView.setTextSize(25);

                    textView.setBackgroundResource(R.drawable.border);

                    textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TeacherHomePage.this, subject, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(TeacherHomePage.this, TeacherPerSubPage.class);
                        intent.putExtra("subject",subject);
                        intent.putExtra("time", time);
                        intent.putExtra("user_name",userName);
                        startActivity(intent);
                    }
                    });
                    if (linearLayout != null) {
                        linearLayout.addView(textView);
                    }
//                    linearLayout.addView(textView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(TeacherHomePage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addSubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHomePage.this, TeacherAddSubject.class);
                intent.putExtra("user_name", userName);
                startActivity(intent);
            }
        });
        timeRestrictionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHomePage.this, TeacherTimeRestrictions.class);
                intent.putExtra("user_name", userName);
                startActivity(intent);
            }
        });

    }
}