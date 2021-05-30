package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class TeacherPerSubPage extends AppCompatActivity {

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String userName;
    private String subject;
    private String time;
    private TextView subjectTitle;
    private TextView timeTitle;
    private String randomChar;
    private EditText generateText;
    private Button generateCodeButton;
    private Button refreshButton1;
    private Button viewStudentsBtn;
    private TextView SubjectCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_per_sub_page);

        userName = getIntent().getStringExtra("user_name");
        subject = getIntent().getStringExtra("subject");
        time = getIntent().getStringExtra("time");


        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");
        
        subjectTitle = (TextView) findViewById(R.id.subjectTitle);
        timeTitle = (TextView) findViewById(R.id.timeTitle);
        generateText = (EditText) findViewById(R.id.generateText);
        generateCodeButton = (Button) findViewById(R.id.generateCodeButton);
        refreshButton1 = (Button) findViewById(R.id.refreshButton1);
        viewStudentsBtn = (Button) findViewById(R.id.viewStudentsBtn);
        SubjectCodeText = (TextView) findViewById(R.id.SubjectCodeText);

//        String codeText = reference.child(userName).getValue();

//        SubjectCodeText.setText()

        subjectTitle.setText(subject);
        subjectTitle.setAllCaps(true);

        System.out.println(time);
        timeTitle.setText(time);
        timeTitle.setAllCaps(true);


        generateCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomChar = getSaltString(6);
                generateText.setText(randomChar);
                reference.child(userName).child("courses").child(subject).child("code").setValue(randomChar);
                reference.child(userName).child("code").setValue(randomChar);
                Toast.makeText(TeacherPerSubPage.this, "Code Generated Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        refreshButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        viewStudentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherPerSubPage.this, TeacherViewStudents.class);
                intent.putExtra("user_name", userName);
                intent.putExtra("subject",subject);
                startActivity(intent);
            }
        });

        LinearLayout linearLayout = findViewById(R.id.dateLayout);

        Query checkUser = reference.orderByChild("username").equalTo(userName);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String codeText = snapshot.child(userName).child("courses").child(subject).child("SubjectCode").getValue(String.class);
                SubjectCodeText.setText(codeText);
                for (DataSnapshot ds : snapshot.child(userName).child("courses").child(subject).child("Date").getChildren()){
                    String dateKey = ds.getKey();


                    TextView textView = new TextView(TeacherPerSubPage.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(30, 20, 20, 30);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    textView.setLayoutParams(params);

                    textView.setText(new StringBuilder().append("------------    ").append(dateKey).append("    ------------").toString());
                    textView.setPadding(50,50, 50, 50);
                    textView.setTextSize(18);

                    textView.setBackgroundResource(R.drawable.border);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(TeacherPerSubPage.this, subject, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(TeacherPerSubPage.this, StudAttPage.class);
                            intent.putExtra("subject",subject);
                            intent.putExtra("dateKey", dateKey);
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









//        long millis=System.currentTimeMillis();
//        java.sql.Date date=new java.sql.Date(millis);
//        String Currdate = date.toString().substring(5, 7) + "/" + date.toString().substring(8, 10) + "/" + date.toString().substring(0, 4);


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