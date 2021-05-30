package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherTimeRestrictions extends AppCompatActivity {

    private EditText timeRestrictionText;
    private Button saveTimeBtn;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String userName;
    private TextView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_time_restrictions);

        timeRestrictionText = (EditText) findViewById(R.id.timeRestrictionText);
        saveTimeBtn = (Button) findViewById(R.id.saveTimeBtn);
        goBack = (TextView) findViewById(R.id.goBack);

        userName = getIntent().getStringExtra("user_name");

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String restrict = snapshot.child(userName).child("restrictions").getValue(String.class);
                timeRestrictionText.setText(restrict);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        saveTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(userName).child("restrictions").setValue(timeRestrictionText.getText().toString());
                Toast.makeText(TeacherTimeRestrictions.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherTimeRestrictions.this, TeacherHomePage.class);
                intent.putExtra("user_name",userName);
                startActivity(intent);
            }
        });







    }
}