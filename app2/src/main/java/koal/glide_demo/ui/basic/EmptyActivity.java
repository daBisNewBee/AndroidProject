package koal.glide_demo.ui.basic;

import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import koal.glide_demo.R;
import koal.glide_demo.ui.fragment.MainFragment;
import koal.glide_demo.ui.fragment.OtherFragment;

/**
 *
 * add:
 *    onAttach:
 *    onCreateView:
 *    onActivityCreated:
 *    onStart:
 *    onResume:
 *
 *    onDestroyView:
 *    onDetach:
 *
 *
 */
public class EmptyActivity extends AppCompatActivity
        implements MainFragment.OnFragmentInteractionListener,
        View.OnClickListener {

    private FragmentManager mFragmentManager;
    private final String MAIN_FRAGMENT_TAG = "main_fragment_tag";
    private final String OTHER_FRAGMENT_TAG = "other_fragment_tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
        setContentView(R.layout.activity_empty);
        getSupportActionBar().hide();
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_remove).setOnClickListener(this);
        findViewById(R.id.btn_hide).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_attach).setOnClickListener(this);
        findViewById(R.id.btn_detach).setOnClickListener(this);
        findViewById(R.id.btn_pop).setOnClickListener(this);
        findViewById(R.id.btn_replace).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        // 事务不能全局，只能被提交一次
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        int id = v.getId();
        switch (id){
            case R.id.btn_add:
                /*
                * onAttach:
                  onCreateView:
                  onActivityCreated:
                  onStart:
                  onResume:
                * */
                transaction
                        .add(R.id.empty_frag_container, new MainFragment(),MAIN_FRAGMENT_TAG)
//                        .add(R.id.empty_frag_container, new OtherFragment(),"other_fragment_tag")
                        .addToBackStack("这里随便传什么");
                break;
            case R.id.btn_remove:
                /*
                * 未添加.addToBackStack("")
                *   onDestroyView:
                    onDetach:

                  添加.addToBackStack("")
                    onDestroyView：
                * */
                transaction.remove(mFragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG));
                break;
            case R.id.btn_pop:
                /*
                * 1. 必须已经调用过"addToBackStack"
                * 2. "popBackStack" 针对的是一个 "transaction"，
                * 即该transaction上add进去的frag，而非指定tag的frag
                * 
                * OtherFragment onDestroyView:
                * OtherFragment onDetach:
                * onDestroyView:
                * onDetach:
                * */
                mFragmentManager.popBackStack();
                break;
            case R.id.btn_attach:
                break;
            case R.id.btn_detach:
                break;
            case R.id.btn_hide:
                break;
            case R.id.btn_show:
                break;
            case R.id.btn_replace:
                transaction.replace(R.id.empty_frag_container, new OtherFragment(), OTHER_FRAGMENT_TAG);
                break;
            case R.id.btn_test:
                List<Fragment> list = mFragmentManager.getFragments();
                if (list.size() == 0) {
                    Log.d("test", "没有可用fragment. return.");
                    return;
                }
                for (Fragment fragment : list) {
                    Log.d("test", "fragment: " + fragment.toString());
                }

                break;
            default:
                break;
        }
        transaction.commit();
    }
}
