package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentHomePage extends AppCompatActivity {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
    DateTimeFormatter timeHour = DateTimeFormatter.ofPattern("HH");
    DateTimeFormatter timeMin = DateTimeFormatter.ofPattern("mm");
    private String CurrTime;
    private String CurrDate;

    private Button codeButton;
    private String studentCode;
    private EditText studentCodeTemp;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private String studName;
    private boolean exist;
    private Button registrationButton;
    private String registrationCode;
    private EditText registrationCodeTemp;
    private String subKey;
    private ProgressDialog progressDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_page);

        codeButton = (Button) findViewById(R.id.codeButton);
        studentCodeTemp = (EditText) findViewById(R.id.studentCode);
        registrationButton = (Button) findViewById(R.id.registrationButton);
        registrationCodeTemp = (EditText) findViewById(R.id.registrationCode);

        progressDialog = new ProgressDialog(this);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");
        reference2 = rootNode.getReference("Students");

        studName = getIntent().getStringExtra("user_name");

        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Finding the Code");
                progressDialog.show();
                LocalDateTime now = LocalDateTime.now();
                long millis=System.currentTimeMillis();
                java.sql.Date date=new java.sql.Date(millis);

                CurrDate = date.toString().substring(5, 7) + "-" + date.toString().substring(8, 10) + "-" + date.toString().substring(0, 4);
                CurrTime = dtf.format(now);
                exist = false;

                studentCode = studentCodeTemp.getText().toString().trim();

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()){
                            String userKey = ds.getKey();
                            String code = ds.child("code").getValue(String.class);

//                            System.out.println(userKey);
                            if (studentCode.equals(code)){
                                exist = true;
                                for (DataSnapshot ds2 : ds.child("courses").getChildren()){
                                        String courseKey = ds2.getKey();
                                        String courseCode = ds2.child("code").getValue(String.class);
                                        String subStartTemp = ds2.child("StartInMinutes").getValue(String.class);

//                                            System.out.println("Student Code: " + studentCode);
//                                            System.out.println("Course Code: " + courseCode);
                                        if (studentCode.equals(courseCode)){
                                            String r = ds.child("restrictions").getValue(String.class);
                                            if (isNumeric(subStartTemp)) {
                                                int subStart = Integer.parseInt(subStartTemp);
                                                int teacherRestrictions = Integer.parseInt(r);
//                                                System.out.println("teacherRestrictions: " + teacherRestrictions);
                                                int studHour = Integer.parseInt(timeHour.format(now));
//                                                System.out.println("studHour: " + studHour);
                                                int studMin = Integer.parseInt(timeMin.format(now));
//                                                System.out.println("studMin: " + studMin);
                                                int studTotalTime = (studHour*60) + studMin;
//                                                System.out.println("studTotalTime: " + studTotalTime);



//                                                System.out.println("subStart: " + subStart);
                                                if (studTotalTime <= (subStart + teacherRestrictions)){
                                                    reference.child(userKey).child("courses").child(courseKey).child("Date").child(CurrDate).child(studName).setValue(CurrTime);
                                                    progressDialog.dismiss();
                                                    Toast.makeText(StudentHomePage.this, "You are now present for this session", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                    startActivity(getIntent());
                                                    overridePendingTransition(0, 0);
                                                    break;
                                                }else{
                                                    progressDialog.dismiss();
                                                    Toast.makeText(StudentHomePage.this, "Attendance is now restricted", Toast.LENGTH_SHORT).show();
                                                    studentCodeTemp.setError("Attendance is now restricted");
                                                    break;
                                                }
                                            }
                                            else if(subStartTemp.equals("None")){
                                                reference.child(userKey).child("courses").child(courseKey).child("Date").child(CurrDate).child(studName).setValue(CurrTime);
                                                progressDialog.dismiss();
                                                Toast.makeText(StudentHomePage.this, "You are now present for this session", Toast.LENGTH_SHORT).show();
                                                finish();
                                                overridePendingTransition(0, 0);
                                                startActivity(getIntent());
                                                overridePendingTransition(0, 0);
                                                break;
                                            }
                                            else{
                                                progressDialog.dismiss();
                                                Toast.makeText(StudentHomePage.this, "Your teacher input wrong restrictions", Toast.LENGTH_SHORT).show();
                                                studentCodeTemp.setError("Your teacher input wrong restrictions");
                                                break;
                                            }
                                        }
                                    }

                                break;
                            }

                        }
                        if (exist == false){
                            progressDialog.dismiss();
                            studentCodeTemp.setError("Code cannot be found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Registering the subject");
                progressDialog.show();
                exist = false;
                registrationCode = registrationCodeTemp.getText().toString().trim();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()){
                            String userKey = ds.getKey();


                            for (DataSnapshot ds2 : ds.child("courses").getChildren()){
                                String courseKey = ds2.getKey();
                                String subjectCode = ds2.child("SubjectCode").getValue(String.class);

//                                            System.out.println("Student Code: " + studentCode);
//                                            System.out.println("Course Code: " + courseCode);
                                if (registrationCode.equals(subjectCode)){
                                    reference.child(userKey).child("courses").child(courseKey).child("Students").child(studName).setValue(0);
                                    reference2.child(studName).child("subjects").child(courseKey).setValue(0);
                                    progressDialog.dismiss();
                                    Toast.makeText(StudentHomePage.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    exist = true;

                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                    break;
                                }
                            }


                        }
                        if (exist == false){
                            progressDialog.dismiss();
                            registrationCodeTemp.setError("Code cannot be found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        LinearLayout linearLayout = findViewById(R.id.studentSubjectsLayout);
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                DataSnapshot firstChild = snapshot.child("courses");
                int count = 1;
                for (DataSnapshot ds : snapshot.child(studName).child("subjects").getChildren()){
                    subKey = ds.getKey();

//                    System.out.println(nameKey);
//                    System.out.println(studentTime);

                    TextView textView = new TextView(StudentHomePage.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 5, 5, 5);
                    textView.setLayoutParams(params);

                    textView.setText(new StringBuilder().append(count).append(". ").append(subKey).toString());
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

    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}