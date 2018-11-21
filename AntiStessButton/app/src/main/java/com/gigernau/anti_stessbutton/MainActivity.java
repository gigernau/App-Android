package com.gigernau.stessbutton;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.PendingIntent.getActivity;


public class MainActivity extends AppCompatActivity{

    private ImageButton button1;
    private InterstitialAd mInterstitialAd;
    private String text = "NON TOCCARE!";
    private TextToSpeech ts;
    private String localLanguage = Locale.getDefault().toString();
    private int mCounter;
    private TextView txv;






    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MobileAds.initialize(this, "ca-app-pub-3741444071150926~8413492746");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3741444071150926/7423860614");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Sorpresa!")
                .setMessage("Hai premuto il pulsante 10 volte!\nA 100 riceverai un premio!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        mCounter = 0;
        txv = (TextView) findViewById (R.id.tx);
        button1 = (ImageButton) findViewById(R.id.button1);

        builder.setMessage("Scherzetto! Devi arrivare a 1000!\nAhahahahahahah").show();

        ts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != TextToSpeech.ERROR){
                    int result = your_Language();
                }

                button1.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        ts.speak(text.toLowerCase(), TextToSpeech.QUEUE_FLUSH, null);
                        mCounter++;
                        txv.setText(Integer.toString(mCounter));

                        if(mCounter == 10) {

                            builder.show();
                        }
                        else if(mCounter == 100){
                            builder.setMessage("Scherzetto! Devi arrivare a 1000!\nAhahahahahahah").show();
                        }
                        else if(mCounter == 500){
                            builder.setMessage("Ci sei quasi!\nSei a metà strada.").show();
                        }
                        else if (mCounter == 1000)
                        {
                            builder.setMessage("Complimenti,\n " +
                                    "hai vinto un buono da usare\n nei migliori centri commerciali").show();
                        }


                    }
                });

            }
        });





        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                ha.postDelayed(this, 20000);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }, 20000);


    }


    private int your_Language(){

        if(!Locale.getDefault().toString().equals("tr_TR")){
            if(localLanguage.equals(Locale.ITALIAN.toString())){
                ts.setLanguage(Locale.ITALIAN);
            }
            else if(localLanguage.equals(Locale.ITALY.toString())){
                ts.setLanguage(Locale.ITALY);
            }
            else if(localLanguage.equals(Locale.UK.toString())){
                ts.setLanguage(Locale.UK);
            }
            else if(localLanguage.equals(Locale.US.toString())){
                ts.setLanguage(Locale.US);
            }

            else if(localLanguage.equals(Locale.ENGLISH.toString())){
                ts.setLanguage(Locale.ENGLISH);
            }
        }

        return 1;
    }
}

