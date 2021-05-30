package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TeacherLogIn extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private Button Login;
    private TextView signUpText;
    private ProgressDialog progressDialog;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private TextView loginAsStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_log_in);

        Name = (EditText) findViewById(R.id.teacherUser);
        Password = (EditText) findViewById(R.id.teacherPass);
        Login = (Button) findViewById(R.id.logInButton);
        signUpText = (TextView) findViewById(R.id.signUpText2);
        progressDialog = new ProgressDialog(this);
        loginAsStudent = (TextView) findViewById(R.id.loginAsStudent);

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null){
//            finish();
//            Intent intent = new Intent(TeacherLogIn.this, TeacherHomePage.class);
//            startActivity(intent);
//        }


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });


        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherLogIn.this, TeacherSignUp.class);
                startActivity(intent);
            }
        });

        loginAsStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherLogIn.this, StudentLogin.class);
                startActivity(intent);
            }
        });



    }


    private void validate(String userName, String userPass){
        progressDialog.setMessage("Verifying Username and Password");
        progressDialog.show();
        if ((!userName.isEmpty()) && (!userPass.isEmpty())){
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("Teachers");
            Query checkUser = reference.orderByChild("username").equalTo(userName);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String passwordDB = snapshot.child(userName).child("password").getValue(String.class);

                        if(passwordDB.equals(userPass)){
                            progressDialog.dismiss();
                            Toast.makeText(TeacherLogIn.this, "LogIn Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherLogIn.this, TeacherHomePage.class);
                            intent.putExtra("user_name", userName);
                            startActivity(intent);
                        }else{
                            progressDialog.dismiss();
                            Password.setError("Wrong Password");
                            Toast.makeText(TeacherLogIn.this, "LogIn Failed", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        progressDialog.dismiss();
                        Name.setError("Username does not exist");
                        Toast.makeText(TeacherLogIn.this, "LogIn Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            progressDialog.dismiss();
            if (userName.isEmpty()){
                Name.setError("Username cannot be empty");
            }

            if (userPass.isEmpty()){
                Password.setError("Password cannot be empty");
            }

        }

    }
    }