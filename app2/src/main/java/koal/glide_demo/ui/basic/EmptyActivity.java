package koal.glide_demo.ui.basic;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import koal.glide_demo.R;
import koal.glide_demo.ui.fragment.MainFragment;

public class EmptyActivity extends AppCompatActivity
        implements MainFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.empty_frag_container, new MainFragment())
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
