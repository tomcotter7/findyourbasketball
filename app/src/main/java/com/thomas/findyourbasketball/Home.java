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
import android.support.annotation.Nullable;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
    String TAG = Home.class.getSimpleName();
    LatLng globalLocation;
    boolean placeSearched;
    double distanceToCourt = 0.02;
    float zoomToCourt = 13.5f;
    private static final int UPDATE_INTERVAL = 50;
    private static final int FASTEST_INTERVAL = 10;
    int attemptedSearches;
    int courtCount;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
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

    private void createLocationCallback(){
        Log.d(TAG, "createLocationCallback");
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);

                globalLocation = new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

            }
        };
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
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        createLocationRequest();

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
    private void createLocationRequest(){
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        createLocationCallback();
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("TAG", "onMapReady: called");
        mMap = googleMap;
    }

    public void getLocation(View view) {
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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
                                Log.d(TAG, ""+location.getLatitude()+","+location.getLongitude());
                                globalLocation = new LatLng(location.getLatitude(),location.getLongitude());
                                onLocationChange();
                            }
                        }
                    });

        }

    }



    public void onLocationChange() {
        mMap.clear();
        if (placeSearched = false) {
            mMap.addMarker(new MarkerOptions().position(globalLocation).title("Current Location"));
        }
        else {
            mMap.addMarker(new MarkerOptions().position(globalLocation)); //Add title for title of the place.
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(globalLocation));
        attemptedSearches = 0;
        courtCount = 0;
        zoomToCourt = 13.5f;
        distanceToCourt = 0.02;
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
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Court court = document.toObject(Court.class);
                        if (court.latitude <= globalLocation.latitude + distanceToCourt && court.latitude >= globalLocation.latitude - distanceToCourt) {
                            courtCount += 1;
                            LatLng courtLoc = new LatLng(court.latitude, court.longitude);
                            mMap.addMarker(new MarkerOptions().position(courtLoc).title(court.name).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)));
                        }
                    }
                    if (courtCount < 2 && attemptedSearches < 3){
                        Log.d(TAG,"Court Count:"+ courtCount + " " + "Attempted Searches:"+ attemptedSearches);
                        Toast.makeText(getActivity(), "Not many courts found nearby, looking further away.", Toast.LENGTH_SHORT).show();
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
        if (attemptedSearches == 3 && courtCount == 0) {
            Toast.makeText(getActivity(), "No courts found nearby, try searching for your local town.", Toast.LENGTH_LONG).show();
        }
    }}