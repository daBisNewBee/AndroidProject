package koal.glide_demo.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import koal.glide_demo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherFragment extends Fragment {


    public OtherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("test", "OtherFragment onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_ther, container, false);
        rootView.findViewById(R.id.btn_jump_to_last).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity context) {
        Log.d("test", "OtherFragment onAttach: ");
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        Log.d("test", "OtherFragment onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.d("test", "OtherFragment onDetach: ");
        super.onDetach();
    }
}
