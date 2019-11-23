package koal.glide_demo.ui.basic;

import android.graphics.Rect;
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

import java.util.Collections;
import java.util.List;

import koal.glide_demo.R;
import koal.glide_demo.ui.fragment.MainFragment;
import koal.glide_demo.ui.fragment.OtherFragment;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

/**
 *
 * DecorView
 *
 * "android.R.id.content"相关：
 *
 * 1. "android.R.id.content" 为什么是一个FrameLayout？(ContentFrameLayout)
 *     TODO:
 *
 * 2. 为什么 "android.R.id.content as container for Fragment"， 即：".add(android.R.id.content, new MainFragment())" ？
 *    若添加在Activity的父布局，会产生布局冗余。
 *    （比如原content 为 Framelayout，Activity 也是，则产生两个 Framelayout，冗余了）
 *    ps：需要注意api14、19的不同，content只是屏幕的部分
 *    (https://stackoverflow.com/questions/24712227/android-r-id-content-as-container-for-fragment)
 *
 * 3. add(), show(), hide(), replace()的那点事
 *   区别：
 *   show、hide只改变可见性(setVisibility是true还是false)，onHiddenChanged回调，不会触发生命周期
 *   replace 会销毁视图，即调用onDestoryView、onCreateView等一系列生命周期
 *
 *   场景：
 *   show、hide:当前Fragment很高可能性再次复用，性能较高
 *   replace：原有Fragment占用大量内存，需及时销毁
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
        getSupportActionBar().hide();
        setContentView(R.layout.activity_empty);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_remove).setOnClickListener(this);
        findViewById(R.id.btn_hide).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_attach).setOnClickListener(this);
        findViewById(R.id.btn_detach).setOnClickListener(this);
        findViewById(R.id.btn_pop).setOnClickListener(this);
        findViewById(R.id.btn_replace).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);

        /*
        * "android.support.v4.app.Fragment" 与 "android.app.Fragment"区别：
        * 1. 前者的Activity必须继承自"FragmentActivity"
        *    后者可以继承自"Activity"
        *
        * 2. 获取的FragmentManager的方式不同：
        *    前者："getSupportFragmentManager"
        *    后者："getFragmentManager"
        *    本质：就是因为3.0以下的Activity不含有"FragmentManager"，因此需要构造含有"FragmentManager"的FragmentActivity，来获得一样的能力
        *
        * 3. 支持的最低版本不同：
        *    前者：4 （即，1.6 ，其实也是xxx.v4.xxx 命名的原由；v7表示最低支持到2.1）
        *    后者：11 （Fragment正式是在API 3.0以后引入的）
        * */
        mFragmentManager = getSupportFragmentManager();

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.v("todo", "statusBarHeight = " + statusBarHeight);

        int contentTop = findViewById(android.R.id.content).getTop();
        Log.v("todo", "contentTop = " + contentTop);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        Fragment findFrag;
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
//                transaction.add(R.id.empty_frag_container, new OtherFragment(), OTHER_FRAGMENT_TAG)                        .addToBackStack(""); // add多次，验证"popBackStack"弹出所有
                // ximalaya 模拟
//                mFragmentManager
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.framework_slide_in_right, R.anim.framework_slide_out_right
//                                , R.anim.framework_slide_in_right, R.anim.framework_slide_out_right)
//                        .add(android.R.id.content, new MainFragment())
//                        .addToBackStack(null)
//                        .commitAllowingStateLoss();
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
                mFragmentManager.popBackStack(); // 只弹出最顶上一个
//                mFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE); // 可以pop所有Frag
                break;
            case R.id.btn_attach:
                break;
            case R.id.btn_detach:
                break;
            case R.id.btn_hide:
                findFrag = mFragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG);
                if (findFrag != null) {
                    transaction.hide(findFrag);
                } else {
                    Log.d("test", "找不到fragment. return.");
                }
                break;
            case R.id.btn_show:
                findFrag = mFragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG);
                if (findFrag != null) {
                    transaction.show(findFrag);
                } else {
                    Log.d("test", "找不到fragment. return.");
                }
                break;
            case R.id.btn_replace:
//                transaction.add(R.id.empty_frag_container, new OtherFragment(), OTHER_FRAGMENT_TAG).addToBackStack("");
                transaction.replace(R.id.empty_frag_container, new OtherFragment(), OTHER_FRAGMENT_TAG);
                break;
            case R.id.btn_test:
                printFragments(mFragmentManager);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void printFragments(FragmentManager fragmentManager) {
        List<Fragment> list = fragmentManager.getFragments();
        if (list.size() == 0) {
            Log.d("test", "没有可用fragment. return.");
            return;
        }
        for (Fragment fragment : list) {
            Log.d("test", "fragment: " + fragment.toString());
//            FragmentManager childFM = fragment.getFragmentManager();
//            FragmentManager childFM = fragment.getChildFragmentManager();
//            if (childFM.getFragments().size() > 0) {
//                Log.d("todo", "发现子Fragment size = " + childFM.getFragments().size());
//                printFragments(childFM);
//            }
        }
    }
}
