package com.example.covidmc;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class SymptomTracker extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_tracker);
        Bundle a = getIntent().getExtras();
        final RatingBar ratings[] = new RatingBar[10];
        ratings[0] = (RatingBar) findViewById(R.id.breath);
        ratings[1] = (RatingBar) findViewById(R.id.fever);
        ratings[2] = (RatingBar) findViewById(R.id.headache);
        ratings[3] = (RatingBar) findViewById(R.id.vomiting);
        ratings[4] = (RatingBar) findViewById(R.id.cough);
        ratings[5] = (RatingBar) findViewById(R.id.diarhhea);
        ratings[6] = (RatingBar) findViewById(R.id.soreThroat);
        ratings[7] = (RatingBar) findViewById(R.id.feelingTired);
        ratings[8] = (RatingBar) findViewById(R.id.muscleAche);
        ratings[9] = (RatingBar) findViewById(R.id.smellTaste);
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String query = "insert into monitor(HeartRate, RespiratoryRate, Fever, ShortnessOfBreath, Headache, Vomiting, Cough, Diarhhea, SoreThroat, FeelingTired,MuscleAche, LossOfSmell) " +
                        "values("+MainActivity.mainHeartRate+", "+MainActivity.mainRespRate+" , ";
                String temp = "";
                for (int i = 0; i < 10; i++) {
                    float rating = (float) ratings[i].getRating();
                    temp += rating;
                    if (i != 9) {
                        temp += ",";
                    }
                }
                query += temp + ");";
                try {
                    db = SQLiteDatabase.openOrCreateDatabase(getExternalFilesDir(null).getAbsolutePath() + "/records.db", null);
                    db.beginTransaction();
                    try {
                        db.execSQL(query);
                        db.setTransactionSuccessful();
                        Toast.makeText(SymptomTracker.this, "Data stored", Toast.LENGTH_LONG).show();
                    } catch (SQLiteException e) {
                        Toast.makeText(SymptomTracker.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        db.endTransaction();
                    }
                    Intent intent = new Intent(SymptomTracker.this, MainActivity.class);
                    startActivity(intent);
                } catch (SQLException e) {
                    Toast.makeText(SymptomTracker.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}