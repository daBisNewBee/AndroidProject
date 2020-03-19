package koal.glide_demo;

import android.media.AudioFormat;
import android.text.TextUtils;
import android.util.Log;

import com.ximalaya.mediaprocessor.AacEncoder;
import com.ximalaya.mediaprocessor.GlobalSet;

import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wenbin.liu on 2020-03-19
 *
 * @author wenbin.liu
 */
public class WAVTest {

    @Test
    public void aacTest() throws Exception {
        String pcmFilePath = "/sdcard/Sweep_-15dBFS_LR_Phase90_44k_16bit_2ch.pcm";
        String aacFilePath = "/sdcard/Sweep_-15dBFS_LR_Phase90_44k_16bit_2ch.aac";
        GlobalSet.RegisterFFmpeg();

        AacEncoder aacEncoder = new AacEncoder();
        aacEncoder.Init(aacFilePath, 44100, 2, 44100, 2);

        FileInputStream fis = new FileInputStream(pcmFilePath);
        DataInputStream dis = new DataInputStream(fis);

        short[] buf = new short[1024];
        long start = System.currentTimeMillis();
        while (true) {
            try {
                for (int i = 0; i < buf.length; i++) {
                    buf[i] = (short) dis.readUnsignedShort();
                }
            } catch (EOFException e) {
                System.out.println("EOFException e = " + e);
                break;
            }
            aacEncoder.EncodeAudioFrame(buf, buf.length);
        }
        long end  = System.currentTimeMillis();
        // 10s的pcm，编码输出双声道需要7236 ms ，输出单声道 5968ms
        Log.d("todo", "cost = " + (end - start) + " ms");
        float duration = aacEncoder.GetAacDurationInSec();
        Log.d("todo", "duration = " + duration);
        aacEncoder.FlushAndCloseFile();
    }

    @Test
    public void main() {
        Log.d("todo", "main() called");
        String pcmFilePath = "/sdcard/side_chain_test.pcm";
        String wavFilePath = "/sdcard/side_chain_test.wav";
        long start = System.currentTimeMillis();
        pcmToWave(pcmFilePath, wavFilePath, 44100, AudioFormat.CHANNEL_IN_MONO);
        long end  = System.currentTimeMillis();
        // 2min27s 需要约310ms
        Log.d("todo", "cost = " + (end - start) + " ms");
    }

    private void pcmToWave(String inFileName, String outFileName, int sampleRate, int channel) {
        if (TextUtils.isEmpty(inFileName) || TextUtils.isEmpty(outFileName)) {
            return;
        }
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long longSampleRate = sampleRate;
        long totalDataLen;
        int channels = channel == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;//录制的是单声道就是1 双声道就是2（如果错了声音可能会急促等）
        long byteRate = 16 * longSampleRate * channels / 8;
        byte[] data = new byte[1024];

        try {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);

            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.flush();
            out.close();
//            File pcmFile = new File(inFileName);
//            if (pcmFile.exists()) {
//                pcmFile.delete();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
     * FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的，
     */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
