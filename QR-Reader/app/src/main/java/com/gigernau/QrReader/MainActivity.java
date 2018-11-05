package com.gigernau.QrReader;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;


@SuppressWarnings("serial")

//MODEL STEWARD
public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        //evitare apertura tastiera
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(!checkPermission())
                requestPermission();
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();

            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Exit")
                .setMessage("Sei sicuro di voler uscire?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        scannerView.stopCamera();
                        MainActivity.super.finish();
                        MainActivity.super.onDestroy();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    //Show Accepted pop-up
    public void showAcceped(final String myResult){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Scan Result");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.accepted);

        ImageView imageV2 = (ImageView) dialog.findViewById(R.id.ImageView01);
        imageV2.setImageResource(R.drawable.accepted);
        Animation myAnimation = AnimationUtils.loadAnimation(this,R.anim.tween2);
        imageV2.startAnimation(myAnimation);

        TextView textV1 = (TextView) dialog.findViewById(R.id.txt00);
        textV1.setText("\n\n");
        TextView textV = (TextView) dialog.findViewById(R.id.txt01);
        textV.setText(myResult);
        TextView textV2 = (TextView) dialog.findViewById(R.id.txt02);
        textV2.setText("\n\n");


        Button button01 = (Button) dialog.findViewById(R.id.butt01);
        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.resumeCameraPreview(MainActivity.this);
                dialog.dismiss();
            }
        });

        Button button03 = (Button) dialog.findViewById(R.id.butt03);
        button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInCustomTab(myResult);

            }
        });

        Button button02 = (Button) dialog.findViewById(R.id.butt02);
        button02.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(myResult,myResult);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Testo copiato negli appunti", Toast.LENGTH_LONG).show();

            }
        });

        dialog.show();

    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void openInCustomTab(String url){
        Uri websiteUri;
        if(!url.contains("https://") && !url.contains("http://")){

           String uri = "http://www.google.it/search?hl=it&as_q=&as_epq=&as_oq=&as_eq=&as_nlo=&as_nhi=&lr=lang_en&cr=countryUS&as_qdr=w&as_sitesearch=&as_occt=any&safe=images&as_filetype=&as_rights=";
           uri = uri.replace("&as_q=","&as_q="+ url);
           websiteUri = Uri.parse(uri);

        } else {
            websiteUri = Uri.parse(url);
        }

        CustomTabsIntent.Builder customtabintent = new CustomTabsIntent.Builder();
        customtabintent.setToolbarColor(Color.parseColor("#3F51B5"));
        customtabintent.setShowTitle(true);

        if(chromeInstalled())
            customtabintent.build().intent.setPackage("com.android.chrome");

        customtabintent.build().launchUrl(this,websiteUri);
    }

    private boolean chromeInstalled(){

        try{
            getPackageManager().getPackageInfo("com.android.chrome",0);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();

        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
        showAcceped(myResult);

    }
}




