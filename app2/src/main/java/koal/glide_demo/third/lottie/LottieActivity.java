package koal.glide_demo.third.lottie;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

import koal.glide_demo.R;

public class LottieActivity extends AppCompatActivity {

    private LottieAnimationView mLottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);
        mLottieAnimationView = findViewById(R.id.lottie_view);
        mLottieAnimationView.setImageAssetsFolder("lottie");
        mLottieAnimationView.setAnimation("lottie/data.json");
        mLottieAnimationView.setRepeatCount(ValueAnimator.INFINITE);
        mLottieAnimationView.playAnimation();
        float minFrame = mLottieAnimationView.getMinFrame();
        float maxFragme = mLottieAnimationView.getMaxFrame();
    }
}
