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

public class TeacherSignUp extends AppCompatActivity {

    private EditText signUpUser;
    private EditText signUpPass;
    private EditText signUpEmail;
    private Button registerButton;
    private TextView backToLogin;
    private TextView warningText;
//    private EditText signUpPhone;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_sign_up);
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
                    reference = rootNode.getReference("Teachers");
                    UserHelperClass helperClass  = new UserHelperClass(signUpUserStr, signUpEmailStr, signUpPassStr);

                    Query checkUser = reference.orderByChild("username").equalTo(signUpUserStr);
                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                progressDialog.dismiss();
                                warningText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                warningText.setText("Username already exist");
                            }
                            else{
                                progressDialog.dismiss();
                                reference.child(signUpUserStr).setValue(helperClass);
                                Toast.makeText(TeacherSignUp.this, "You have registered Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(TeacherSignUp.this, TeacherLogIn.class);
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
                Intent intent = new Intent(TeacherSignUp.this, TeacherLogIn.class);
                startActivity(intent);
            }
        });
        
    }

    private void setupUIViews(){
        signUpUser = (EditText) findViewById(R.id.signUpUser);
        signUpPass = (EditText) findViewById(R.id.signUpPass);
        signUpEmail = (EditText) findViewById(R.id.signUpEmail);
        registerButton = (Button) findViewById(R.id.registerButton);
        backToLogin = (TextView) findViewById(R.id.backToLogin);
        warningText = (TextView) findViewById(R.id.warningText2);
    }

    private boolean validate(){
        Boolean result = false;

        String name = signUpUser.getText().toString();
        String password = signUpPass.getText().toString();
        String email = signUpEmail.getText().toString();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty()){
            warningText.setText("Username or Password cannot be empty");
            Toast.makeText(TeacherSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            warningText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        else if(password.length() < 7){
            warningText.setText("Password must have 7 characters and up");
            Toast.makeText(TeacherSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            warningText.setTextColor(getResources().getColor(android.R.color.holo_red_light));

        }
        else{
            warningText.setText("");
            result = true;
        }

        return result;
    }
}