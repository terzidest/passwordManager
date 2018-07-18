package com.example.vicky.passmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static Button button_sbm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onClickButtonListener();
        onClickButtonListener2();
    }

    public void onClickButtonListener() {
        button_sbm = (Button) findViewById(R.id.button2);
        button_sbm.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,SetYourProfileActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public void onClickButtonListener2() {
        button_sbm = (Button) findViewById(R.id.button);
        button_sbm.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
