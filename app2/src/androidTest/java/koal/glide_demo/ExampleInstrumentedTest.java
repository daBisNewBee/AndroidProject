package koal.glide_demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("koal.glide_demo", appContext.getPackageName());
    }

    @Test
    public void MPTest() throws Exception {
        MediaPlayer player = new MediaPlayer();
        assertTrue(player != null);

        player.setDataSource("/sdcard/qingyunian.mp3");
        player.prepare();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("test", "onCompletion() called with: mp = [" + mp + "]");
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("test", "onError() called with: mp = [" + mp + "], what = [" + what + "], extra = [" + extra + "]");
                return false;
            }
        });
        Looper.prepare();
        player.addTimedTextSource("/sdcard/qingyunian.srt", MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);


        MediaPlayer.TrackInfo[] trackInfos = player.getTrackInfo();
        Log.d("test", "MPTest: trackInfos size:" + trackInfos.length);

        if (trackInfos != null && trackInfos.length > 0) {
            for (int i = 0; i < trackInfos.length; i++) {
                final MediaPlayer.TrackInfo info = trackInfos[i];
                Log.w("test", "TrackInfo: " + info.getTrackType() + " "
                        + info.getLanguage());

                if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    // mMediaPlayer.selectTrack(i);
                } else if (info.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT) {
                    player.selectTrack(i);
                }
            }
        }
        SortedMap<String,Charset> map = Charset.availableCharsets();

        player.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(MediaPlayer mp, TimedText text) {
                Log.d("test", "onTimedText() called with text = [" + (text != null ? text.getText() : "null") + "]");
            }
        });

        player.start();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await();

        Log.d("test", "MPTest() called");
    }
}
