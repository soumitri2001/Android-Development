package com.example.temppredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etInput;
    Button btnSubmit, btnclick;
    TextView tvShow, tvResult;
    ImageView thermo_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput=findViewById(R.id.etInput);
        btnclick=findViewById(R.id.btnclick);
        btnSubmit=findViewById(R.id.btnSubmit);
        tvShow=findViewById(R.id.tvShow);
        tvResult=findViewById(R.id.tvResult);
        thermo_img=findViewById(R.id.thermo_img);

        tvShow.setVisibility(View.GONE);
        tvResult.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        etInput.setVisibility(View.GONE);

        btnclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String showtxt=getString(R.string.txt1) + " " + getString(R.string.txt2);
                tvShow.setText(showtxt);
                tvShow.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                etInput.setVisibility(View.VISIBLE);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int num=Integer.parseInt(etInput.getText().toString().trim());
                        boolean flag=true;
                        if(num<0 || num>10000) flag=false;
                        double temp=((double)num/3.0)+4.0;
                        String res=getString(R.string.reslt) + " " + Double.toString(temp) + " " + getString(R.string.cel);
                        String inv=getString(R.string.inval);
                        if(flag)
                            tvResult.setText(res);
                        else tvResult.setText(inv);
                        tvResult.setVisibility(View.VISIBLE);

                    }
                });
            }
        });
    }
}
