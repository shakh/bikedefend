package com.bikedefend;

import java.util.Iterator;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationOverlay;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.CoordConversion;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MapFragment extends Fragment implements OnMyLocationListener {
	
	double x = 56.493498;
	double y = 84.951064;
	TextView xInput;
	TextView yInput;
	View fragmentView;
	MapController mapController;
	OverlayManager overlayManager;
	LinearLayout viewLocation;
	GeoPoint myGeoPoint;
	Button whereMyBike;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.map_layout, container, false);
		MapView mapView = (MapView) fragmentView.findViewById(R.id.map);
		mapView.showBuiltInScreenButtons(true);
		mapView.showJamsButton(false);
		mapController = mapView.getMapController();
		mapController.setZoomCurrent(10);
		
		overlayManager = mapController.getOverlayManager();
		
		mapController.getOverlayManager().getMyLocation().addMyLocationListener(this);
		viewLocation = (LinearLayout) fragmentView.findViewById(R.id.view);
		xInput = (TextView) fragmentView.findViewById(R.id.X);
		yInput = (TextView) fragmentView.findViewById(R.id.Y);
		((MainActivity) getActivity()).setFragmentTag("Map");
		
		whereMyBike = (Button) fragmentView.findViewById(R.id.WhereMyBike);
		whereMyBike.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mapController.setPositionAnimationTo(new GeoPoint(x,y));
			}
		});
		
		return fragmentView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onMyLocationChange(MyLocationItem myLocationItem) {
		myGeoPoint = myLocationItem.getGeoPoint();
		Log.e("Tracker", "Location detected");
	}
	
	public void showObject(GeoPoint geoPoint){
        Resources res = getResources();
        // Create a layer of objects for the map
        Overlay overlay = new Overlay(mapController);
        // Create an object for the layer
        OverlayItem object = new OverlayItem(geoPoint, res.getDrawable(R.drawable.ymk_user_location_gps));
        // Add the object to the layer
        overlay.clearOverlayItems();
        overlay.addOverlayItem(object);
        //Remove previous layer
        overlayManager.removeOverlay((Overlay) overlayManager.getOverlays().get(overlayManager.getOverlays().size()-1));
        // Add the layer to the map
        overlayManager.addOverlay(overlay);        
    }
	
	public void changeLocation(double x, double y){
		this.x = x;
		this.y = y;
		showObject(new GeoPoint(x, y));
    	mapController.setPositionAnimationTo(new GeoPoint(x, y));
    	xInput.setText(String.valueOf(x));
    	yInput.setText(String.valueOf(y));
	}
}
