package koal.glide_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;
import koal.glide_demo.dagger.Pot;

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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_record:
                startActivity(new Intent(this, AudioRecordActivity.class));
                break;
            default:
                break;
        }
    }
}
