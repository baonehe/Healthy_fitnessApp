package com.google.uddd_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

    TextView tvKM, tvCL;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
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
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        editor = sharedPreferences.edit();
        tvKM = view.findViewById(R.id.txtProgress);
        tvCL = view.findViewById(R.id.calorieCount);

        if(!sharedPreferences.getString("DistancesKM", "").isEmpty() || !sharedPreferences.getString("Calories", "").isEmpty()){
            String KM = sharedPreferences.getString("DistancesKM", "");
            String CL = sharedPreferences.getString("Calories", "");
            tvKM.setText(KM);
            tvCL.setText(CL);
        }

        view.findViewById(R.id.imgRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        return  view;
    }


    @Override
    public void onResume() {
        if(!sharedPreferences.getString("DistancesKM", "").isEmpty() || !sharedPreferences.getString("Calories", "").isEmpty()){
            String KM = sharedPreferences.getString("DistancesKM", "");
            String CL = sharedPreferences.getString("Calories", "");
            tvKM.setText(KM);
            tvCL.setText(CL);
        }
        super.onResume();
    }
}