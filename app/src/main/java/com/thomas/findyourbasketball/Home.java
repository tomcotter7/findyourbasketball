package com.thomas.findyourbasketball;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;


public class Home extends Fragment implements View.OnClickListener,OnMapReadyCallback {

    MapView mMapView;
    String TAG = Home.class.getSimpleName();
    LatLng currentLocation;
    LatLng placeLocation;
    boolean placeSearched;
    String placeName;
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
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
    LocationRequest mLocationRequest;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    //sets up fire base so I can retrieve data from it.
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .build();



    // the fragment initialization parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // This method means that the location stored in the variable currentLocation is updated every time the users location refreshes.
    private void createLocationCallback(){
        Log.d(TAG, "createLocationCallback");
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);

                currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());

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


        // Set up the FusedLocationProviderClient - which allows me to request the users location and location updates.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().getApplicationContext());
        // Create a location request - this contains data about the request accuracy and volume which can be used bu the LocationProviderClient.
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        createLocationRequest();
        // Set up Places - which is how the search bar returns the data.
        Places.initialize(this.requireActivity(), getString(R.string.google_api_key));


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Set up onClickListeners for both the floating action button and search button.
        FloatingActionButton fab = rootView.findViewById(R.id.locationButton);
        fab.setOnClickListener(this);
        ImageButton searchIcon = rootView.findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(this);

        // Load in the map.
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e){
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set title for toolbar.
        requireActivity().setTitle("Find Your Basketball");
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

    public void findCourtsAroundLocation(Place place){
        // Get latitude and longitude of given place.
        // Update placeLocation to be these coordinates.
        placeLocation = place.getLatLng();
        placeName = place.getName();
        //Update placeSearched so that the program knows which location variable to use.
        placeSearched = true;
        onLocationChange();
    }

    public void onSearchIconClicked() {
        // Create an Intent to load up the search bar - .OVERLAY means it loads on top of the previous screen.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this.requireActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    // This method runs automatically when the user searches for a location.
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            // If the search worked then.
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                // Run this method with the place returned from the search.
                findCourtsAroundLocation(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // If user clicked the Floating Action Button then.
            case R.id.locationButton:
                getLocation(v);
                break;
            // If the user clicked the Search Icon then.
            case R.id.search_icon:
                onSearchIconClicked();
                break;
        }
    }

    private void createLocationRequest(){
        if (ContextCompat.checkSelfPermission( requireActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        createLocationCallback();
        // Set up the location updates with all the provided data.
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void getLocation(View view) {
        if (ContextCompat.checkSelfPermission( requireActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            // Set up map.
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                                placeSearched = false;
                                onLocationChange();
                            }
                        }
                    });

        }

    }


    public void onLocationChange() {
        LatLng locationNeeded = currentLocation;
        // if a place has been searched for the locationNeeded should be changed to the placeLocation.
        if (placeSearched) locationNeeded = placeLocation;
        // Clear the map of all pins from previous searches.
        mMap.clear();
        // Add a marker to the location.
        mMap.addMarker(new MarkerOptions().position(locationNeeded));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locationNeeded));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationNeeded, zoomToCourt));
        // Reset all variables.
        attemptedSearches = 0;
        courtCount = 0;
        zoomToCourt = 13.3f;
        distanceToCourt = 0.02;
        getCourts(locationNeeded);
    }

    public void methodFailure() {
        Toast.makeText(getActivity(),"There has been some corruption in the data", Toast.LENGTH_LONG).show();
        LatLng centre = new LatLng(0,0);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centre,0.5f));
        mMap.clear();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getCourts(final LatLng globalLocation) {
        attemptedSearches += 1;
        // Makes sure that distance to court has not been corrupted anywhere - it must always be positive.
        if (distanceToCourt < 0) {
            methodFailure();
            return;
        }
        // This try-catch statement is needed because "task.getResult() may return null - however it would be unlikely.
        try {
            // Set up a reference for the Courts collection in my firestore.
            CollectionReference courtsRef = firestore.collection("Courts");
            // Query for all courts within a latitude band with width 2 * distanceToCourt.
            Query query = courtsRef.whereLessThanOrEqualTo("latitude", globalLocation.latitude + distanceToCourt)
                    .whereGreaterThanOrEqualTo("latitude", globalLocation.latitude - distanceToCourt);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert the document to a Court object.
                            Court court = new Court(document.getString("name"), document.getDouble("latitude"), document.getDouble("longitude")/**,requireActivity().getApplicationContext()**/);
                            if (court.isNearby(distanceToCourt, globalLocation)) {
                                Log.d(TAG, "Court Name:" +court.getName());
                                courtCount += 1;
                                // Add a different style marker to where the court is.
                                mMap.addMarker(new MarkerOptions().position(court.getLatLng()).title(court.getName())./**snippet(court.getAddress()).**/icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin)));
                            }
                        }
                        // Make sure that enough courts are displayed in the search.
                        if (courtCount < 2 && attemptedSearches < 3) {
                            Toast.makeText(getActivity(), "Not many courts found nearby, looking further away.", Toast.LENGTH_SHORT).show();
                            // Increase the box size.
                            distanceToCourt += 0.02;
                            zoomToCourt -= 1.3f;
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(globalLocation, zoomToCourt));
                            // Rerun the method.
                            getCourts(globalLocation);
                        }
                    // Runs if task is not successful.
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
            // If no courts have been found within three boxes - the user is told there are no courts nearby.
            if (attemptedSearches == 3 && courtCount == 0) {
                if (!placeSearched) {
                    Toast.makeText(getActivity(), "No courts found nearby, try searching for your local town.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "No courts found near "+ placeName, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e){
            Log.d(TAG, "Error: task.getResult returned null");
        }
    }
}