package com.niteshjha.androidvideoformat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    SurfaceView sView;
    SurfaceHolder sHolder;
    MediaRecorder recorder;

    Button startButton;

    String name = Long.toString(System.currentTimeMillis());

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.button_record);

        sView = (SurfaceView) findViewById(R.id.surfaceView1);

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Video settings
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

//        camera.setDisplayOrientation(90);
        recorder.setOrientationHint(90);

        // Audio settings
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
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
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recorder.start();
            }
        });
    }
    public void stop(View view) {
        recorder.stop();
        addFastStart();
        Toast.makeText(MainActivity.this, getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + name + ".mp4",Toast.LENGTH_LONG).show();
    }

    private File getOutputMediaFile()
    {
        File mediaStorageDir = new File(
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator);

        File[] files =mediaStorageDir.listFiles();

        if(!mediaStorageDir.exists())
        {
            if(!mediaStorageDir.mkdirs())
            {
                Log.d("Video_format","Failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(mediaStorageDir.getPath() + File.separator+ "VID_"+timeStamp+".mp4");

        Log.d("recorder_location", file.getAbsolutePath());

        Log.d("recorder_location", file.getAbsolutePath());

        String fileM =  file.getAbsolutePath();

        return file;
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
                Toast.makeText(MainActivity.this, "Saved to : " + output,Toast.LENGTH_LONG).show();
            }
        });
    }
}
