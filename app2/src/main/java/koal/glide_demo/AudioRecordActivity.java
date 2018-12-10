package koal.glide_demo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * 参考:
 * Android多媒体学：利用AudioRecord类实现自己的音频录制程序.
 * http://jiahua8859-163-com.iteye.com/blog/1147948
 *
 */
public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener{

    private File audioFile;
    private volatile boolean isRecording=false, isPlaying=false;

    private static final int RECORDER_SAMPLE_RATE_LOW = 8000;
    private static final int RECORDER_SAMPLE_RATE_HIGH = 44100;
    private static final boolean isLow = false;

    // 录制频率，可以为8000hz或者11025hz等，不同的硬件设备这个值不同
    private static final int sampleRateInHz = isLow ? RECORDER_SAMPLE_RATE_LOW : RECORDER_SAMPLE_RATE_HIGH;
    // 录制通道
    private static final int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    // 录制编码格式，可以为AudioFormat.ENCODING_16BIT和8BIT,其中16BIT的仿真性比8BIT好，但是需要消耗更多的电量和存储空间
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 录制缓冲大小
    private static final int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

    static {
        System.out.println("getMinBufferSize: " + bufferSize);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        findViewById(R.id.btn_start_record).setOnClickListener(this);
        findViewById(R.id.btn_stop_record).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        try {
            audioFile = File.createTempFile("record-" + Long.toString(System.currentTimeMillis()), ".pcm", new File("/sdcard/"));
            System.out.println("创建录音文件：" + audioFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_start_record:
                new RecordTask().execute();
                break;
            case R.id.btn_stop_record:
                isRecording = false;
                break;
            case R.id.btn_play:
                new PlayTask().execute();
                break;
            case R.id.btn_stop:
                isPlaying = false;
                break;
            default:
                break;
        }
    }

    class RecordTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (isRecording)
                    return null;

                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));

                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, bufferSize);

                short[] buffer = new short[bufferSize];

                audioRecord.startRecording();
                isRecording = true;

                int r = 0;
                while (isRecording){
                    int size = audioRecord.read(buffer, 0, bufferSize);
                    for (int i = 0; i < size; i++){
                        dos.writeShort(buffer[i]);
                    }
                    r++;
                    publishProgress(r);
                }

                audioRecord.stop();
                System.out.println("停止录音. length: " + audioFile.length());
                dos.flush();
                dos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            System.out.println("onProgressUpdate = " + values[0]);
        }
    }

    class PlayTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if (isPlaying)
                return null;

            short[] buffer = new short[bufferSize];
//            short[] buffer = new short[bufferSize/4];

            try {
                System.out.println("准备播放的文件是："+audioFile.getPath());
                System.out.println("准备播放的文件大小是："+audioFile.length());
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(audioFile)));
                // MediaPlayer无法播放pcm
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
                audioTrack.play();
                isPlaying = true;
                int sum = 0;
                while (isPlaying && dis.available() > 0){
                    int i = 0;
                    while (dis.available()>0 && i<buffer.length){
                        buffer[i] = dis.readShort();
                        i++;
                    }
                    audioTrack.write(buffer, 0, buffer.length);
                    sum += buffer.length;
                    System.out.println("写入到audioTrack的数据大小为：" + buffer.length +" 合计：" + sum);
                }
                isPlaying = false;
                System.out.println("结束播放.");
                audioTrack.stop();
                audioTrack.release();
                dis.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
