package koal.glide_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import javax.inject.Inject;
import koal.glide_demo.dagger.Pot;

public class MainActivity extends AppCompatActivity {

    @Inject
    Pot mPot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        DaggerActivityComponent.create().inject(this);
//        System.out.println("mPot = " + mPot.show());
    }
}
