package koal.glide_demo.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import koal.glide_demo.R;
import koal.glide_demo.utlis.FlagsUtlis;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("test", "onCreateView: ");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView.findViewById(R.id.btn_jump_to_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack("")
                        .add(R.id.fl_center_bottom_container, new OtherFragment())
//                        .replace(R.id.empty_frag_container, new OtherFragment())
                        .commit();
            }
        });

        rootView.findViewById(R.id.btn_set_flags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlagsUtlis.setScreenOrientation(getActivity(), true);
                FlagsUtlis.setFlags(getActivity());
                List<Fragment> list = getFragmentManager().getFragments();
                for (Fragment fragment : list) {
                    Log.d("todo", "getFragmentManager fragment = " + fragment);
                }
            }

        });
        rootView.findViewById(R.id.btn_unset_flags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlagsUtlis.setScreenOrientation(getActivity(), false);
                FlagsUtlis.unsetFlags(getActivity());
                List<Fragment> list = getChildFragmentManager().getFragments();
                for (Fragment fragment : list) {
                    Log.d("todo", "getChildFragmentManager fragment = " + fragment);
                }
            }
        });

        rootView.findViewById(R.id.btn_show_flags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlagsUtlis.showFlags(getActivity());
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("test", "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("todo", "onHiddenChanged() called with: hidden = [" + hidden + "]");
    }

    @Override
    public void onStart() {
        Log.d("test", "onStart: ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("test", "onResume: ");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.d("test", "onDestroyView: ");
        super.onDestroyView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d("test", "onAttach: ");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d("test", "onDetach: ");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
