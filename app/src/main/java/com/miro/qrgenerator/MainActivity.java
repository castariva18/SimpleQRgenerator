package com.miro.qrgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MainActivity extends AppCompatActivity {

    private EditText etText;
    private Button btnGenerate;
    private ImageView imgQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etText = findViewById(R.id.et_text);
        btnGenerate = findViewById(R.id.btn_generate);
        imgQR = findViewById(R.id.img_qr);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInput();
            }
        });
    }

    private void CheckInput() {
        String text = etText.getText().toString();
        if (TextUtils.isEmpty(text)){
            etText.setError("Email cannot be empty");
            etText.requestFocus();
        }else {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = qrCodeWriter.encode(text.toString(),
                        BarcodeFormat.QR_CODE, 600, 600);
                Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565);

                for (int i = 0; i < 600; i++) {
                    for (int j = 0; j < 600; j++) {
                        bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }

                imgQR.setImageBitmap(bitmap);

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }
}