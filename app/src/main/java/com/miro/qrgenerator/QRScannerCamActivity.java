package com.miro.qrgenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.miro.qrgenerator.model.QRURLModel;
import com.miro.qrgenerator.model.QRVCardModel;

public class QRScannerCamActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView tvResult;
    private String currentUserId, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner_cam);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        scannerView = findViewById(R.id.scanner);
        tvResult = findViewById(R.id.tv_result);


        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(QRScannerCamActivity.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QRScannerCamActivity.this, "Must allow this permission", Toast.LENGTH_LONG).show();
                        finish();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }

                })
                .check();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        processRawResult(rawResult.getText());
        scannerView.startCamera();
    }

    private void processRawResult(String text) {
        if (text.startsWith("BEGIN: ")) {
            String[] tokens = text.split("\n");
            QRVCardModel qrvCardModel = new QRVCardModel();
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].startsWith("BEGIN: ")) {
                    qrvCardModel.setType(tokens[i].substring("BEGIN: ".length()));
                } else if (tokens[i].startsWith("N: ")) {
                    qrvCardModel.setName(tokens[i].substring("N: ".length()));
                } else if (tokens[i].startsWith("ORG: ")) {
                    qrvCardModel.setOrg(tokens[i].substring("ORG: ".length()));
                } else if (tokens[i].startsWith("TEL: ")) {
                    qrvCardModel.setTel(tokens[i].substring("TEL: ".length()));
                } else if (tokens[i].startsWith("URL: ")) {
                    qrvCardModel.setUrl(tokens[i].substring("URL: ".length()));
                } else if (tokens[i].startsWith("EMAIL: ")) {
                    qrvCardModel.setEmail(tokens[i].substring("EMAIL: ".length()));
                } else if (tokens[i].startsWith("ADR: ")) {
                    qrvCardModel.setAddress(tokens[i].substring("ADR: ".length()));
                } else if (tokens[i].startsWith("NOTE: ")) {
                    qrvCardModel.setNote(tokens[i].substring("NOTE: ".length()));
                } else if (tokens[i].startsWith("SUMMARY: ")) {
                    qrvCardModel.setSummary(tokens[i].substring("SUMMARY: ".length()));
                } else if (tokens[i].startsWith("DTSTART: ")) {
                    qrvCardModel.setDtstart(tokens[i].substring("DTSTART: ".length()));
                } else if (tokens[i].startsWith("DTEND: ")) {
                    qrvCardModel.setDtend(tokens[i].substring("DTEND: ".length()));
                }
                tvResult.setText(qrvCardModel.getType());
            }
        } else if (text.startsWith("http://") || text.startsWith("https://") || text.startsWith("www.")) {
            QRURLModel qrurlModel = new QRURLModel(text);
            tvResult.setText(qrurlModel.getUrl());
        } else {
            Toast.makeText(this, " is Checked", Toast.LENGTH_SHORT).show();
        }
        scannerView.resumeCameraPreview(QRScannerCamActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}