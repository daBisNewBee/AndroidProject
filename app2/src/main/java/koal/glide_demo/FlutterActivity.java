package koal.glide_demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//import io.flutter.facade.Flutter;

public class FlutterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter);

//        View view = Flutter.createView(this, getLifecycle(), "/");
//        ViewGroup.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        addContentView(view, params);
    }
}
