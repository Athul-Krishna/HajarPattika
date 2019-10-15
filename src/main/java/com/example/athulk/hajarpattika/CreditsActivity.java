package com.example.athulk.hajarpattika;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        Bundle bundle = getIntent().getExtras();
        String newsString = bundle.getString("news");

        TextView newsTextView = (TextView)findViewById(R.id.news);
        newsTextView.setText(newsString);
        newsTextView.setMovementMethod(new ScrollingMovementMethod());

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_layout);
        linearLayout.setAlpha((float) 0.4);
    }
}
