package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class TeacherAddSubject extends AppCompatActivity {

    private Button createSubjectButton;
    private TimePicker timePicker1;
    private TimePicker timePicker2;
    private String format = "";
    private String finalTime1;
    private String finalTime2;
    private String finalTime3;
    private EditText subjectName;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;
    private String randomChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_subject);

        String userName = getIntent().getStringExtra("user_name");

        createSubjectButton = (Button) findViewById(R.id.createSubjectButton);
        subjectName = (EditText) findViewById(R.id.subjectName);
        progressDialog = new ProgressDialog(this);



        createSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectNameStr = subjectName.getText().toString();
                timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
                int hour1 = timePicker1.getCurrentHour();
                int min1 = timePicker1.getCurrentMinute();

                int startMin = (hour1*60) + min1;

                progressDialog.setMessage("Adding the subject");
                progressDialog.show();

                if (hour1 == 0) {
                    hour1 += 12;
                    format = "AM";
                } else if (hour1 == 12) {
                    format = "PM";
                } else if (hour1 > 12) {
                    hour1 -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }

                if (min1 == 0){
                    finalTime1 = new StringBuilder().append("(").append(hour1).append(":0").append(min1).append(" ").append(format).append(")").toString();
                }
                else{
                    finalTime1 = new StringBuilder().append("(").append(hour1).append(":").append(min1).append(" ").append(format).append(")").toString();
                }






                timePicker2 = (TimePicker) findViewById(R.id.timePicker2);
                int hour2 = timePicker2.getCurrentHour();
                int min2 = timePicker2.getCurrentMinute();

                if (hour2 == 0) {
                    hour2 += 12;
                    format = "AM";
                } else if (hour2 == 12) {
                    format = "PM";
                } else if (hour2 > 12) {
                    hour2 -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }

                if (min2 == 0){
                    finalTime2 = new StringBuilder().append("(").append(hour2).append(":0").append(min2).append(" ").append(format).append(")").toString();
                }
                else{
                    finalTime2 = new StringBuilder().append("(").append(hour2).append(":").append(min2).append(" ").append(format).append(")").toString();
                }


//                Intent intent = new Intent(TeacherHomePage.this, MainActivity.class);
//                startActivity(intent);

                finalTime3 = new StringBuilder().append(finalTime1).append(" - ").append(finalTime2).toString();

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("Teachers");
                Query checkUser = reference.orderByChild("username").equalTo(userName);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            progressDialog.dismiss();
                            randomChar = getSaltString(6);
                            reference.child(userName).child("courses").child(subjectNameStr).child("Subject").setValue(subjectNameStr);
                            reference.child(userName).child("courses").child(subjectNameStr).child("Time").setValue(finalTime3);
                            reference.child(userName).child("courses").child(subjectNameStr).child("StartInMinutes").setValue(startMin);
                            reference.child(userName).child("courses").child(subjectNameStr).child("SubjectCode").setValue(randomChar);
                            Toast.makeText(TeacherAddSubject.this, "Subject was added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(TeacherAddSubject.this, TeacherHomePage.class);
                            startActivity(intent);





                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    protected String getSaltString(int num) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < num) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();

        return saltStr;

    }
}