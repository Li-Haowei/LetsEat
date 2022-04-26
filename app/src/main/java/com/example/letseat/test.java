package com.example.letseat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class test extends AppCompatActivity {
    private TextView info1, info2, info3, info4;
    private Button btn_back2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btn_back2 = (Button) findViewById(R.id.btn_back2);
        info1 = (TextView) findViewById(R.id.info1);
        info2 = (TextView) findViewById(R.id.info2);
        info3 = (TextView) findViewById(R.id.info3);
        info4 = (TextView) findViewById(R.id.info4);


        btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(test.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent data = getIntent();
        String restName = data.getStringExtra("restName");
        String restLabels = data.getStringExtra("restLabels");
        String restAdd = data.getStringExtra("restAdd");
        String InvitedBy = data.getStringExtra("InvitedBy");

        info1.setText(restName);
        info2.setText(restLabels);
        info3.setText(restAdd);
        info4.setText(InvitedBy);

    }
}