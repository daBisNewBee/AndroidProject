package koal.glide_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;
import koal.glide_demo.dagger.Pot;
import koal.glide_demo.record.AudioRecordActivity;
import koal.glide_demo.ui.DpPxSpActivity;
import koal.glide_demo.ui.LayoutActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Inject
    Pot mPot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        DaggerActivityComponent.create().inject(this);
//        System.out.println("mPot = " + mPot.show());
        findViewById(R.id.btn_record).setOnClickListener(this);
        findViewById(R.id.btn_dpi).setOnClickListener(this);
        findViewById(R.id.btn_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_record:
                startActivity(new Intent(this, AudioRecordActivity.class));
                break;
            case R.id.btn_dpi:
                startActivity(new Intent(this, DpPxSpActivity.class));
                break;
            case R.id.btn_layout:
                startActivity(new Intent(this, LayoutActivity.class));
                break;
            default:
                break;
        }
    }
}
