package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherSummary extends AppCompatActivity {

    BarChart barChartSummary;
    private String userName;
    private String subject;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    public ArrayList<BarEntry> BarEntries = new ArrayList<>();
    public ArrayList<String> theDates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_summary);

        barChartSummary = (BarChart) findViewById(R.id.bargraph);
        Description description = new Description();
        description.setText("");
        barChartSummary.setDescription(description);

        userName = getIntent().getStringExtra("user_name");
        subject = getIntent().getStringExtra("subject");

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Teachers");

        theDates.add("0");


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                DataSnapshot firstChild = snapshot.child("courses");
                int count = 1;
                for (DataSnapshot ds : snapshot.child(userName).child("courses").child(subject).child("Date").getChildren()){
                    String listDateKey = ds.getKey();

                    long pr = ds.getChildrenCount();
//                    float present = (float)pr;
                    int present = (int) pr;

                    System.out.println("Date: " + listDateKey);
                    System.out.println("Count: " + count);
                    System.out.println("Absents: "+ present);

                    BarEntries.add(new BarEntry(count, present));
                    theDates.add(listDateKey);

                    count++;
                }
                createGraph(count-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });



    }

    public void createGraph(int num){
        BarDataSet dataSet = new BarDataSet(BarEntries, "Present Students");
        dataSet.setColors(new int[] {Color.argb(240,139,0,139)});
        dataSet.setValueTextSize(15f);
        BarData theData = new BarData(dataSet);
        barChartSummary.setData(theData);
        barChartSummary.setFitBars(true);
        barChartSummary.invalidate();

        barChartSummary.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
        XAxis xAxis = barChartSummary.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        if (num > 5){
            xAxis.setLabelRotationAngle(-45);
        }
        xAxis.setLabelCount(num);
//        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
    }


}