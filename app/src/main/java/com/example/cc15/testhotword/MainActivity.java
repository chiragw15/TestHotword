package com.example.cc15.testhotword;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ai.kitt.snowboy.AppResCopy;
import ai.kitt.snowboy.MsgEnum;
import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.audio.PlaybackThread;
import ai.kitt.snowboy.audio.RecordingThread;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    int count = 0;
    Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        textView = (TextView) findViewById(R.id.text);

        checkPermission();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDetectionOn){
                    recordingThread.stopRecording();
                    isDetectionOn = false;
                    start.setText("START");
                } else {
                    start.setText("STOP");
                    init();
                }
            }
        });

    }

    private PlaybackThread playbackThread;
    private RecordingThread recordingThread;
    private boolean isDetectionOn = false;

    private void init(){
        AppResCopy.copyResFromAssetsToSD(this);

        recordingThread = new RecordingThread(handle, new AudioDataSaver());
        playbackThread = new PlaybackThread();

        recordingThread.startRecording();
        isDetectionOn = true;
    }

    private int activeTimes = 0;
    public Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Log.d(TAG, "handleMessage: called");
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
                    activeTimes++;
                    //Toast.makeText(MainActivity.this, "Detected Hi Susi", Toast.LENGTH_SHORT).show();
                    count++;
                    textView.setText("Hotword detected " + count);
                    //updateLog(" ----> Detected " + activeTimes + " times", "green");
                    // Toast.makeText(Demo.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
                    showToast("Active "+activeTimes);
                    break;
                case MSG_INFO:
                    //updateLog(" ----> "+message);
                    break;
                case MSG_VAD_SPEECH:
                    //updateLog(" ----> normal voice", "blue");
                    break;
                case MSG_VAD_NOSPEECH:
                    //updateLog(" ----> no speech", "blue");
                    break;
                case MSG_ERROR:
                    //updateLog(" ----> " + msg.toString(), "red");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(isDetectionOn){
            recordingThread.stopRecording();
            isDetectionOn = false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        } else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 3);
        }
    }
}
