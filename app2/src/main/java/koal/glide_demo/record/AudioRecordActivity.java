package koal.glide_demo.record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ximalaya.mediaprocessor.GlobalSet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import koal.glide_demo.R;

/**
 *
 * 参考:
 * Android多媒体学：利用AudioRecord类实现自己的音频录制程序.
 * http://jiahua8859-163-com.iteye.com/blog/1147948
 *
 * 录制AudioRecord 选择单声道、双声道的几点结论：
 *
 * 1. 单声道音量略小于双声道音量(华为)(其他手机：差别不明显，比如魅族)
 *    source：
 *    MIC:从表现来看，底层有对声音有做处理，类似取平均值，造成音量较弱的声道最终削弱了音量较强的声道
 *    voice_commun: 很大。
 *
 * 2. 双声道音源与两个mic收集到的声音对齐(华为)(其他手机：音量一样，表现为同一份声音的拷贝)
 *    source：
 *    华为nova3e上的测试结果为：
 *    上面的mic：对应右声道(后置MIC录音)
 *    下面的mic：对应左声道(前置MIC录音)
 *    影响：
 *      近距离录音时，左右声道强度有差别，体验差
 *    voice_commun:
 *    两声道数据一样，表现为拷贝
 *
 *    网上也有类似情况：
 *    双MIC安卓手机录音问题：
 *    https://www.cnblogs.com/zzugyl/p/3958553.html
 *
 *   mic位置：         上(back-奇数)       下(front-偶数)
 *   声道(奇数、偶数)
 *
 *   红米4A：  (左右左右....)
 *              下 上 下 上.....
 *   锤子OD103   下 上
 *   魅族16sPro  下 上
 *   oppo a57   声道一致(只有一个mic)
 *   oppo k3    下 上 （单声道时，音量更响，大约4倍）
 *   诺基亚 X7   声道一致(只有一个mic)
 *   小米cc9     下 上(更响)
 *   三星a9lite  声道一致(只有一个mic)
 *
 *
 *
 *
 *
 */
public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener{

    private File audioFile, audioFileMono, audioFileFront, audioFileBack;
    private volatile boolean isRecording=false, isPlaying=false;

    private static final int RECORDER_SAMPLE_RATE_LOW = 8000;
    private static final int RECORDER_SAMPLE_RATE_HIGH = 44100;
    private static final boolean isLow = false;

    private boolean isOpenSterero2Mono = false;

    // 录制频率，可以为8000hz或者11025hz等，不同的硬件设备这个值不同
    private static final int sampleRateInHz = isLow ? RECORDER_SAMPLE_RATE_LOW : RECORDER_SAMPLE_RATE_HIGH;
    // 录制通道
    private static final int audioSource = MediaRecorder.AudioSource.MIC; // MediaRecorder.AudioSource.VOICE_COMMUNICATION
    private static final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private static final int channelConfig_play = AudioFormat.CHANNEL_OUT_MONO;
//    private static final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 录制编码格式，可以为AudioFormat.ENCODING_16BIT和8BIT,其中16BIT的仿真性比8BIT好，但是需要消耗更多的电量和存储空间
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 录制缓冲大小
    private static final int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

    private AudioDecoderThread mAudioDecoderThread;

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
        findViewById(R.id.btn_codec).setOnClickListener(this);
        GlobalSet.RegisterFFmpeg();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
            String curTime = simpleDateFormat.format(new Date());
            audioFile = File.createTempFile("record-" + curTime, ".pcm", new File("/sdcard/pcaps/"));
            audioFileFront = File.createTempFile("record-front-" + curTime, ".pcm", new File("/sdcard/pcaps/"));
            audioFileBack = File.createTempFile("record-back-" +curTime, ".pcm", new File("/sdcard/pcaps/"));
            if (isOpenSterero2Mono) {
                audioFileMono = File.createTempFile("record-mono-" + curTime, ".pcm", new File("/sdcard/pcaps/"));
            }
            System.out.println("创建录音文件：" + audioFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAudioDecoderThread = new AudioDecoderThread();
        checkPermission();
    }

    void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    0);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    0);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    0);
            return;
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
            case R.id.btn_codec:
                if (!isRecording)
                {
                    try {
                        mAudioDecoderThread.startPlay("/sdcard/pcm/record.aac");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    mAudioDecoderThread.stop();
                }
                isRecording = !isRecording;
                break;
            default:
                break;
        }
    }

    private boolean hasMicrophone() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    class RecordTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (isRecording)
                    return null;

                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));
                DataOutputStream dosFront = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFileFront)));
                DataOutputStream dosBack = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFileBack)));
                DataOutputStream dos_mono = null;
                if (isOpenSterero2Mono) {
                    dos_mono = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFileMono)));
                }

                AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize);
                audioRecord.startRecording();

                /*
                * AudioRecord需要使用 "AudioSource.VOICE_COMMUNICATION" 的源
                * 下面才能正常运行
                * */
                boolean isAgc = AutomaticGainControl.isAvailable();
                Log.v("todo", "AutomaticGainControl isAgc = " + isAgc);
                if (isAgc && hasMicrophone()) {
                    AutomaticGainControl agc = AutomaticGainControl.create(audioRecord.getAudioSessionId());
                    boolean getEnabled = agc.getEnabled();
                    Log.v("todo", "getEnabled = " + getEnabled);
                    assert (AudioEffect.SUCCESS == agc.setEnabled(true));
                    getEnabled = agc.getEnabled();
                    Log.v("todo", "After set getEnabled = " + getEnabled);
                }

                // TODO:实际效果不明显！！没卵用
                boolean isAec = AcousticEchoCanceler.isAvailable();
                Log.v("todo", "AcousticEchoCanceler isAec = " + isAec);
                if (isAec && hasMicrophone()) {
                    AcousticEchoCanceler aec = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
                    boolean getEnabled = aec.getEnabled();
                    Log.v("todo", "getEnabled = " + getEnabled);
                    assert AudioEffect.SUCCESS == aec.setEnabled(true);
                    getEnabled = aec.getEnabled();
                    Log.v("todo", "After set getEnabled = " + getEnabled);
                }

                boolean isNs = NoiseSuppressor.isAvailable();
                Log.v("todo", "NoiseSuppressor isNs = " + isNs);
                if (isNs && hasMicrophone()) {
                    NoiseSuppressor ns = NoiseSuppressor.create(audioRecord.getAudioSessionId());
                    boolean getEnabled = ns.getEnabled();
                    Log.v("todo", "getEnabled = " + getEnabled);
                    assert AudioEffect.SUCCESS == ns.setEnabled(true);
                    getEnabled = ns.getEnabled();
                    Log.v("todo", "After set getEnabled = " + getEnabled);
                }

                short[] buffer = new short[bufferSize];

                isRecording = true;

                int r = 0;
                int i = 0;
                long last = System.currentTimeMillis();
                while (isRecording) {
                    long cur = System.currentTimeMillis();
                    if ( cur - last >= 1000) {
                        // 结论：stereo与mono模式下，1s内都可以read 13次，stereo的的数据量是mono 的两倍，因为bufferSize是两倍
                        System.out.println("i = " + i);
                        last = cur;
                        i = 0;
                    }
                    int size = audioRecord.read(buffer, 0, bufferSize);
                    i++;
                    /*
                    System.out.println("read size = " + size);
                    if (isOpenSterero2Mono) {
                        short[] dstMono = new short[size >> 1];
                        GlobalSet.StereoToMonoS16(dstMono, buffer, size >> 1);
                        for (int i = 0; i < dstMono.length; i++) {
                            System.out.println("i = " + i + " lenL" + dstMono.length);
                            dos_mono.writeShort(dstMono[i]);
                        }
                    }

                    // 分离双声道 为 左声道、右声道
                    for (int i = 0; i < size; i++) {
                        dos.writeShort(buffer[i]);
                        if (i % 2 == 0) {
                            dosFront.writeShort(buffer[i]);
                        } else {
                            dosBack.writeShort(buffer[i]);
                        }
                    }
                    r++;
                    publishProgress(r);
                     */
                }

                audioRecord.stop();
                System.out.println("停止录音. length: " + audioFile.length());
                dos.flush();
                dos.close();

                dosFront.flush();
                dosFront.close();
                dosBack.flush();
                dosBack.close();

                if (isOpenSterero2Mono) {
                    dos_mono.flush();
                    dos_mono.close();
                }
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
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig_play, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
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
