package com.thomas.findyourbasketball;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Home extends Fragment implements View.OnClickListener,OnMapReadyCallback {

    MapView mMapView;
    PlaceAutocompleteFragment mPlaceAutocomplete;
    String TAG = Home.class.getSimpleName();
    LatLng globalLocation = new LatLng(51.405432,-0.544334);
    double distanceToCourt = 0.02;
    AutoCompleteTextView autoCompleteTextView;
    float zoomToCourt = 14.5f;
    int attemptedSearches = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .build();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore.setFirestoreSettings(settings);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        FloatingActionButton fab = rootView.findViewById(R.id.locationButton);
        fab.setOnClickListener(this);


        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e){
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set title for toolbar
        getActivity().setTitle("Find Your Basketball");
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"onClick running");
        switch (v.getId()) {
            case R.id.locationButton:
                getLocation(v);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("TAG", "onMapReady: called");
        mMap = googleMap;
    }

    public void getLocation(View view) {
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
        else {
            // add code here to get Location?
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                globalLocation = new LatLng(location.getLatitude(),location.getLongitude());
                                onLocationChange();
                            }
                        }
                    });

        }

    }

    public void onLocationChange() {
        mMap.addMarker(new MarkerOptions().position(globalLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(globalLocation));
        getCourts();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(globalLocation, zoomToCourt));
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getCourts() {
        CollectionReference courtsRef = firestore.collection("Courts");
        Query query = courtsRef.whereLessThanOrEqualTo("longitude", globalLocation.longitude + distanceToCourt)
                .whereGreaterThanOrEqualTo("longitude", globalLocation.longitude - distanceToCourt);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int courtCount = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Court court = document.toObject(Court.class);
                        if (court.latitude <= globalLocation.latitude + distanceToCourt && court.latitude >= globalLocation.latitude - distanceToCourt) {
                            courtCount += 1;
                            LatLng courtLoc = new LatLng(court.latitude, court.longitude);
                            mMap.addMarker(new MarkerOptions().position(courtLoc).title(document.getId()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)));
                        }
                    }
                    if (courtCount < 2 && attemptedSearches < 3){
                        Toast.makeText(getActivity(), "No courts found nearby, looking further away.", Toast.LENGTH_SHORT).show();
                        attemptedSearches += 1;
                        distanceToCourt += 0.02;
                        zoomToCourt -= 1.7f;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(globalLocation, zoomToCourt));
                        getCourts();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }}