package com.example.mapion.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mapion.BuildConfig;
import com.example.mapion.R;
import com.example.mapion.databinding.FragmentHomeBinding;
import com.example.mapion.models.MStorageMapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class HomeFragment extends Fragment {

    GPSTracker mGpsTracker;
    private MapView mMap = null;
    private IMapController mMapController;
    //private Marker startMarker;
    private FragmentHomeBinding binding;
    private MStorageMapView storageMapView=MStorageMapView.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mMap = (MapView) root.findViewById(R.id.map);

        //startMarker = new Marker(map);

        mMap.setTileSource(TileSourceFactory.MAPNIK);


        mMap.setMultiTouchControls(true);
        mMapController = mMap.getController();
        mMapController.setZoom(storageMapView.zoom);
        GeoPoint startPoint = new GeoPoint(storageMapView.lat, storageMapView.lon);
        mMapController.setCenter(startPoint);
        //startMarker.setPosition(startPoint);

        //startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        //map.getOverlays().add(startMarker);


        // add compass to map
        CompassOverlay compassOverlay = new CompassOverlay(getActivity(),
                new InternalCompassOrientationProvider(getActivity()), mMap);
        compassOverlay.enableCompass();
        mMap.getOverlays().add(compassOverlay);
        //startMarker.setPosition(startPoint);

        //startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        //map.getOverlays().add(startMarker);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            private MyLocationNewOverlay mLocationOverlay;

            @Override
            public void onClick(View view) {

                if(mGpsTracker==null){
                    mGpsTracker=new GPSTracker(getContext());
                    if(mGpsTracker.canGetLocation==false){
                        mGpsTracker.showSettingsAlert();
                    }else{
                        GeoPoint startPoint = new GeoPoint(mGpsTracker.getLatitude(), mGpsTracker.getLongitude());
                        mMapController.setCenter(startPoint);
                        mGpsTracker.onLocationChangedCore(location -> {
                            mMapController.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        });



                        Snackbar.make(view, "Open Location", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }else{
                    mGpsTracker=null;
                    Snackbar.make(view, "Close Location", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }



//                BoundingBox boundingBox = map.getProjection().getBoundingBox();
//                new SenderRouteFactory().getFreeRoute(boundingBox,(s) -> {
//                    Snackbar.make(view, s, Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                });


            }
        });


        return root;
    }
    @Override
    public void onResume() {
        super.onResume();

        if (mMap != null) {
            mMap.onResume();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mMap != null) {
            mMap.onPause();
        }
    }

    @Override
    public void onStop() {
        storageMapView.lat=  mMap.getMapCenter().getLatitude();
        storageMapView.lon= mMap.getMapCenter().getLongitude();
        storageMapView.zoom= mMap.getZoomLevelDouble();
        storageMapView.save();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}