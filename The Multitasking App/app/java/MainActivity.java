package com.example.themultitasker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView btncall, btncam, btnmap, btnlink;
    Button btnCC, btnCF, btnGit, btnSst;
    TextView tvAck, tvCopyright, tvMyLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btncall=findViewById(R.id.btncall);
        btncam=findViewById(R.id.btncam);
        btnmap=findViewById(R.id.btnmap);
        btnCC=findViewById(R.id.btnCC);
        btnCF=findViewById(R.id.btnCF);
        btnGit=findViewById(R.id.btnGit);
        btnSst=findViewById(R.id.btnSst);
        btnlink=findViewById(R.id.btnlink);
        tvAck=findViewById(R.id.tvAck);
        tvCopyright=findViewById(R.id.tvCopyRIght);
        tvMyLink=findViewById(R.id.tvMyLink);

        btnCC.setVisibility(View.GONE);
        btnCF.setVisibility(View.GONE);
        btnSst.setVisibility(View.GONE);
        btnGit.setVisibility(View.GONE);

        tvMyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/soumitri2001"));
                startActivity(intent);
            }
        });

        btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        });

        btncam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        });

        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0.0?q=Kolkata"));
                startActivity(intent);
            }
        });

        btnlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCC.setVisibility(View.VISIBLE);
                btnCF.setVisibility(View.VISIBLE);
                btnSst.setVisibility(View.VISIBLE);
                btnGit.setVisibility(View.VISIBLE);

                btnCC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.codechef.com/users/soumitri12"));
                        startActivity(intent);
                    }
                });

                btnCF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.codeforces.com/profile/soumitri12"));
                        startActivity(intent);
                    }
                });

                btnGit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/soumitri2001"));
                        startActivity(intent);
                    }
                });

                btnSst.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.stopstalk.com/user/profile/codingbyomkesh"));
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
