package com.example.ridewithme.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.ridewithme.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private  BarChart Statistics_BARCHART_diagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Statistics_BARCHART_diagram = findViewById(R.id.Statistics_BARCHART_diagram);

        getStatsFB();
    }

    private void getStatsFB() {
        ArrayList<BarEntry> visitors = new ArrayList<>();
        visitors.add(new BarEntry(2014,204));
        visitors.add(new BarEntry(2015,355));
        visitors.add(new BarEntry(2016,234));
        visitors.add(new BarEntry(2017,345));
        visitors.add(new BarEntry(2018,767));
        visitors.add(new BarEntry(2019,544));
        visitors.add(new BarEntry(2020,234));

        BarDataSet barDataSet = new BarDataSet(visitors, "Visitors");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        Statistics_BARCHART_diagram.setFitBars(true);
        Statistics_BARCHART_diagram.setData(barData);
        Statistics_BARCHART_diagram.getDescription().setText("Bar chart example");
        Statistics_BARCHART_diagram.animateY(2000);
    }

}
