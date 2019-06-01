package koal.glide_demo.touch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import koal.glide_demo.R;

public class TouchActivity extends Activity {

    public void onClickOther(View view){
        Log.v("aa", "onClickOther view = [" + view + "]");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
    }

}
