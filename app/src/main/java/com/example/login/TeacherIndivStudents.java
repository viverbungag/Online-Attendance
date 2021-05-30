package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherIndivStudents extends AppCompatActivity {

    PieChart piechart;
    int[] colorClassArray = new int[]{Color.argb(240, 213, 171, 255), Color.argb(240, 181, 114, 247)};
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String userName;
    private String subject;
    private String studentName;
    private boolean exist;
    private int numPresent = 0;
    private int numAbsent = 0;
    private TextView studNameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_indiv_students);

        piechart = (PieChart) findViewById(R.id.piechart);
        studNameText = (TextView) findViewById(R.id.studNameText);




        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");

        userName = getIntent().getStringExtra("user_name");
        subject = getIntent().getStringExtra("subject");
        studentName = getIntent().getStringExtra("studentName");

        studNameText.setText(studentName);

        LinearLayout linearLayout = findViewById(R.id.listAbsentLayout);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 1;
                for (DataSnapshot ds : snapshot.child(userName).child("courses").child(subject).child("Date").getChildren()){
                    String dateKey = ds.getKey();

                    exist = false;

                    for (DataSnapshot ds2 : ds.getChildren()) {
                        String nameKey = ds2.getKey();
                        if (nameKey.equals(studentName)){
                            numPresent++;
                            exist = true;
                            break;
                        }

                    }
                    if (exist == false){
                        numAbsent++;
                        TextView textView = new TextView(TeacherIndivStudents.this);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 5, 5, 5);
                        textView.setLayoutParams(params);

                        textView.setText(new StringBuilder().append(count).append(". ").append(dateKey).toString());
                        textView.setPadding(10,10, 10, 10);
                        textView.setTextSize(15);
                        count++;

                        if (linearLayout != null) {
                            linearLayout.addView(textView);
                        }
                    }

                }
                createGraph();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void createGraph(){

        PieDataSet pieDataSet = new PieDataSet(datavalues1(), "");
        pieDataSet.setColors(colorClassArray);
        pieDataSet.setValueTextSize(20f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(piechart));
        piechart.setData(pieData);
        piechart.invalidate();
        piechart.setUsePercentValues(true);
        piechart.setEntryLabelTextSize(20);
        piechart.setDrawHoleEnabled(false);
    }

    private ArrayList<PieEntry> datavalues1(){
        ArrayList<PieEntry> dataVals = new ArrayList<>();
        //add values here
        dataVals.add(new PieEntry(numAbsent, "Absent"));
        dataVals.add(new PieEntry(numPresent, "Present"));

        return dataVals;
    }
}