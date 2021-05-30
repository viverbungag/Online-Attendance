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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StudentSignUp extends AppCompatActivity {

    private EditText signUpUser;
    private EditText signUpPass;
    private EditText signUpEmail;
    private Button registerButton;
    private TextView backToLogin;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_up);
        setupUIViews();

        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    progressDialog.setMessage("Verifying Username and Password");
                    progressDialog.show();
                    String signUpUserStr = signUpUser.getText().toString().trim();
                    String signUpPassStr = signUpPass.getText().toString().trim();
                    String signUpEmailStr = signUpEmail.getText().toString().trim();
//                    String signUpPhoneStr = signUpPhone.getText().toString().trim();

                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("Students");
                    UserHelperClass helperClass  = new UserHelperClass(signUpUserStr, signUpEmailStr, signUpPassStr);

                    Query checkUser = reference.orderByChild("username").equalTo(signUpUserStr);
                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                progressDialog.dismiss();
                                signUpUser.setError("Username already exist");
                            }
                            else{
                                progressDialog.dismiss();
                                reference.child(signUpUserStr).setValue(helperClass);
                                Toast.makeText(StudentSignUp.this, "You have registered Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(StudentSignUp.this, StudentLogin.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(StudentSignUp.this, StudentLogin.class);
                startActivity(intent);
            }
        });

    }

    private void setupUIViews(){
        signUpUser = (EditText) findViewById(R.id.signUpUser2);
        signUpPass = (EditText) findViewById(R.id.signUpPass2);
        signUpEmail = (EditText) findViewById(R.id.signUpEmail2);
        registerButton = (Button) findViewById(R.id.registerButton2);
        backToLogin = (TextView) findViewById(R.id.backToLogin2);
    }

    private boolean validate(){
        Boolean result = false;

        String name = signUpUser.getText().toString();
        String password = signUpPass.getText().toString();
        String email = signUpEmail.getText().toString();

        if (name.isEmpty()){
            signUpUser.setError("Username cannot be empty");
            Toast.makeText(StudentSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            signUpPass.setError("Password cannot be empty");
            Toast.makeText(StudentSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
        else if (email.isEmpty()) {
            signUpEmail.setError("Email cannot be empty");
            Toast.makeText(StudentSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 7){
            signUpPass.setError("Password must have 7 characters and up");
            Toast.makeText(StudentSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
        else{
            result = true;
        }

        return result;
    }



}
