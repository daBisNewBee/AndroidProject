package koal.glide_demo.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import koal.glide_demo.R;
import koal.glide_demo.utlis.BaseUtil;

/**
 * Created by wenbin.liu on 2020-08-27
 *
 * @author wenbin.liu
 */
public class MyDialogFragment extends DialogFragment {

    public static MyDialogFragment newInstance() {
        MyDialogFragment fragment = new MyDialogFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        configureDialogStyle();
        View view = inflater.inflate(R.layout.fragment_dialog_my, container, false);
        return view;
    }

    void configureDialogStyle() {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window win = dialog.getWindow();
        if (win == null) {
            return;
        }
        win.setGravity(Gravity.BOTTOM);
        win.setDimAmount(0.5f);
        win.setBackgroundDrawableResource(R.color.host_transparent);
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onStart() {
        Log.d("todo", "onStart() called");
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window win = dialog.getWindow();
        if (win == null) {
            return;
        }

        try {
            int mode = win.getAttributes().softInputMode;
            Log.d("todo", "获取到的 mode = " + mode);
            WindowManager.LayoutParams lp = win.getAttributes();
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = BaseUtil.dp2px(getContext(), 390);
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
            win.setAttributes(lp);

            mode = win.getAttributes().softInputMode;
            Log.d("todo", "2222 获取到的 mode = " + mode);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditText editText = getView().findViewById(R.id.live_content_et);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setCursorVisible(true);
            }
        });
    }
}
