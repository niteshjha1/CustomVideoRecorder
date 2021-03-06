package com.niteshjha.androidvideoformat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class RecordActivity extends AppCompatActivity {

    SurfaceView sView;
    SurfaceHolder sHolder;
    MediaRecorder recorder;

    Button startButton;

    String name = Long.toString(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Button startButton = findViewById(R.id.button_record);
        Button stopButton = findViewById(R.id.button_stop);
        stopButton.setEnabled(false);

        sView = (SurfaceView) findViewById(R.id.surfaceView1);

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Video settings
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        // Camera.setDisplayOrientation(90);
        recorder.setOrientationHint(90);

        // Audio settings
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // Output format
        recorder.setOutputFile(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + name + ".mp4");
        Log.i("Video_format_recordedF-file","OUTPUT OF VIDEO RECORDER" + getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + name + ".mp4");

        sHolder = sView.getHolder();

        sHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                recorder.setPreviewDisplay(surfaceHolder.getSurface());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startButton.setEnabled(false);

                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recorder.start();
                stopButton.setEnabled(true);
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startButton.setEnabled(true);
                recorder.stop();
                addFastStart();
            }
        });
    }

    public void addFastStart() {

        if (FFmpeg.getInstance(this).isSupported()) {
            Log.d("Video_format","FFMPEG Available ");
            // ffmpeg is supported
        } else {
            Log.i("Video_format","FFMPEG NOT Available ");
            // ffmpeg is not supported
        }

        FFmpeg ffmpeg = FFmpeg.getInstance(this);

        String input = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + name + ".mp4";
        Log.i("Video_format_recordedF-file"," INPUT TO FFMPEG" + input);

//        String input = getOutputMediaFile().getAbsoluteFile().toString();

        String output =   new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/moov_" + (System.currentTimeMillis())+ ".mp4").getPath();

        String[] cmd = {"-i", input, "-movflags", "+faststart", output };

        ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

            @Override
            public void onStart() {
                Log.i("Video_format_onStart"," cmd " + cmd.toString());
            }

            @Override
            public void onProgress(String message) {
                Log.i("Video_format_onProgress",  message);
            }

            @Override
            public void onFailure(String message) {
                Log.i("Video_format_onFailure"," onFailure " + message);
            }

            @Override
            public void onSuccess(String message) {
                Log.i("Video_format_onSuccess"," OnSuccess " + message);
            }

            @Override
            public void onFinish() {
                Log.i("Video_format_onFinish"," It is finished " );
                Toast.makeText(RecordActivity.this, "Saved to : " + output,Toast.LENGTH_LONG).show();
            }
        });
    }
}




