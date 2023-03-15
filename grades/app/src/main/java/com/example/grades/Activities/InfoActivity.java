package com.example.grades.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.grades.R;

public class InfoActivity extends AppCompatActivity {
    TextView infoTV,infoTV2,infoTV3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.theme));
        }
        setContentView(R.layout.activity_info);
        androidx.appcompat.widget.Toolbar mToolbar = findViewById(R.id.toolbar);

        Intent i = getIntent();
        String item = i .getStringExtra("item");

        mToolbar.setTitle(item);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        infoTV = findViewById(R.id.infoTV);
        infoTV2 = findViewById(R.id.infoTV2);
        infoTV3 = findViewById(R.id.infoTV3);
        if(item.equals("CO")) {
            infoTV.setText("CO, aka carbon monoxide, is a colourless, odourless gas.");
            infoTV2.setText("- Oxygen deprivation\n- Dizziness, headaches\n- Upset stomach, vomiting\n- Chest pain");
            infoTV3.setText("- Red blood cells are unable to transport oxygen");
        }
        else if(item.equals("NO2")) {
            infoTV.setText("NO2, aka nitrogen dioxide, is a precursor to smog (smoke + fog) as well as several other pollutants such as ozone and particulate matter.");
            infoTV2.setText("- Lungs irritation");
            infoTV3.setText("- Reduced immunity to respiratory illnesses\n- Higher risk of asthma\n- Lungs damage");
        }
        else if(item.equals("SO2")) {
            infoTV.setText("SO2, aka sulfur dioxide, is a colourless gas.");
            infoTV2.setText("- Wheezing\n- Chest tightness\n- Shortness of breath");
            infoTV3.setText("- Respiratory illnesses\n- Higher chances of cardiovascular diseases\n- Alterations of lungs' defense system");
        }
        else if(item.equals("PM25")) {
            infoTV.setText("PM2.5 particles are particulate matters that are 2.5 micrometers or less in diameter. They can be directly absorbed into the bloodstream upon inhalation.");
            infoTV2.setText("- Eye, throat and nose irritation\n- Irregular heartbeats\n- Asthma attacks\n- Coughing, shortness of breath");
            infoTV3.setText("- Respiratory illnesses\n- Lung damage\n- Cancer, heart attack, stroke");
        }
        else if(item.equals("PM10")) {
            infoTV.setText("PM10 particles are particulate matters that are 10 micrometers or less in diameter.");
            infoTV2.setText("- Breathing difficulty\n- Chest pain\n- Sore throat\n- Nasal congestion");
            infoTV3.setText("- Lung damage\n- Cancer\n- Asthma");
        }
        else {
            infoTV.setText("O3, aka ozone, naturally exists in the atmosphere and blocks UV rays from the Sun. However, at lower heights, ozone is toxic.");
            infoTV2.setText("- Shortness of breath\n- Pain while breathing deeply\n- Wheezing, coughing");
            infoTV3.setText("- Lung functions reduced\n- Lung lining inflammation");
        }
    }
}