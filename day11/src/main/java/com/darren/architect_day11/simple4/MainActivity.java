package com.darren.architect_day11.simple4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.darren.architect_day11.R;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            FileReader fr = new FileReader("xxxx.file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
