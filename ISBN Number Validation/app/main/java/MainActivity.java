package com.example.isbn_validation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etISBN;
    Button btnValid;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etISBN=findViewById(R.id.etISBN);
        btnValid=findViewById(R.id.btnValid);
        tvResult=findViewById(R.id.tvResult);

        tvResult.setVisibility(View.GONE);

        btnValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String isbn=etISBN.getText().toString().trim();
                isbn = isbn.replaceAll( "-", "" );
                char a[]=isbn.toCharArray(); boolean flag=false;
                if(a.length==10) // 10 digit ISBN number
                {
                    int sum=0,k=10;
                    for(int i=0;i<a.length-1;i++)
                    {
                        int n=a[i]-'0';
                        sum+=n*k; k--;
                    }
                    if(a[9]=='X') sum+=10;
                    else sum+=(a[9]-'0');
                    if(sum%11==0)  flag=true;
                    else flag=false;
                }
                else if(a.length==13)
                {
                    int sum=0;
                    for(int i=0;i<a.length;i++)
                    {
                        int n=a[i]-'0';
                        if(i%2==0) sum+= n;
                        else sum+=n*3;
                    }
                    flag = (sum % 10) == 0;
                }
                else // invalid isbn
                {
                    flag=false;
                }
                String yes=getString(R.string.valid_isbn), no=getString(R.string.inv_isbn);
                if(flag)
                    tvResult.setText(yes);
                else tvResult.setText(no);
                tvResult.setVisibility(View.VISIBLE);
            }
        });
    }
}
