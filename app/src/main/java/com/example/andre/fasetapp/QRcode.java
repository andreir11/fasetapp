package com.example.andre.fasetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRcode extends AppCompatActivity {
    String str;
    Intent it;
    Button btn2,btn3;
    String userID;
    private ZXingScannerView zXingScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        Intent it=getIntent();
        userID = it.getStringExtra("userid");
        zXingScannerView =new ZXingScannerView(this);
        zXingScannerView.setResultHandler(new ZXingScannerResultHandler());

        setContentView(zXingScannerView);
        zXingScannerView.startCamera();
    }
    public void save(View view)
    {
        Intent it=new Intent(QRcode.this,save.class);//CALL Saved Class
        it.putExtra("ID",str);//input value
        it.putExtra("userid",userID);
        startActivity(it);
    }
    public void cancel(View view)
    {
        Intent it=new Intent(QRcode.this,VirturalCabinet.class);
        startActivity(it);
    }
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    class ZXingScannerResultHandler implements  ZXingScannerView.ResultHandler
    {
        public void handleResult(Result result) {
            str=result.getText();
            Toast.makeText(QRcode.this,"已成功決定是否儲存",Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_qrcode);
            zXingScannerView.stopCamera();
            btn2 = (Button) findViewById(R.id.button2);
            btn3 = (Button) findViewById(R.id.button3);

        }
    }
}
