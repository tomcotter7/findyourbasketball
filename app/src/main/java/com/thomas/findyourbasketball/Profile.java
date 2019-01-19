package com.thomas.findyourbasketball;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        FloatingActionButton fabTheme1 = rootView.findViewById(R.id.colorTheme1Button);
        FloatingActionButton fabTheme2 = rootView.findViewById(R.id.colorTheme2Button);
        FloatingActionButton fabTheme3 = rootView.findViewById(R.id.colorTheme3Button);
        FloatingActionButton fabTheme4 = rootView.findViewById(R.id.colorTheme4Button);
        FloatingActionButton fabTheme5 = rootView.findViewById(R.id.colorTheme5Button);
        FloatingActionButton fabTheme6 = rootView.findViewById(R.id.colorTheme6Button);
        fabTheme1.setOnClickListener(this);
        fabTheme2.setOnClickListener(this);
        fabTheme3.setOnClickListener(this);
        fabTheme4.setOnClickListener(this);
        fabTheme5.setOnClickListener(this);
        fabTheme6.setOnClickListener(this);
        return rootView;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set title for toolbar
        getActivity().setTitle("Profile");
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.colorTheme1Button):
                //test
                colorChange(R.color.colorThemeDarkBlue,R.id.colorTheme1Button);
                break;
            case (R.id.colorTheme2Button):
                colorChange(R.color.colorThemeGreen,R.id.colorTheme2Button);
                break;
            case (R.id.colorTheme3Button):
                colorChange(R.color.colorPrimary,R.id.colorTheme3Button);
                break;
            case (R.id.colorTheme4Button):
                colorChange(R.color.colorThemeRed,R.id.colorTheme4Button);
                break;
            case (R.id.colorTheme5Button):
                colorChange(R.color.colorThemeTurquoise,R.id.colorTheme5Button);
                break;
            case (R.id.colorTheme6Button):
                colorChange(R.color.colorThemeYellow,R.id.colorTheme6Button);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void colorChange(int colorId, int fabId) {
        // do stuff with the colour
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
