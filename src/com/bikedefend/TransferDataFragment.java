package com.bikedefend;


import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class TransferDataFragment extends Fragment {
	
	public interface ChangeLocationListener{
		public void onLocationChanged(double x, double y);
	}

	ChangeLocationListener callBack;
	static TextView requestData;
	TextView responseData;
	TextView destination;
	Button setData;
	Button getData;
	Button refresh;
	Button genXY;
	Client client;
	View view;
	MediaPlayer mediaPlayer;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.datatransfer_layout, container, false);
		//requestData = (TextView) view.findViewById(R.id.requestData);
		responseData = (TextView) view.findViewById(R.id.responseData);
		//setData = (Button) view.findViewById(R.id.setButton);
		//getData = (Button) view.findViewById(R.id.getButton);
		refresh = (Button) view.findViewById(R.id.Refresh);
		genXY = (Button) view.findViewById(R.id.GenXY);
		
		refresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				destination = (TextView) view.findViewById(R.id.editText1);
				String[] dest = destination.getText().toString().split(":");
				Request request = new Request();
				request.execute("get", dest[0], dest[1]);
				responseData.setText("");
			}
		});
		try {
            callBack = (ChangeLocationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
		
		genXY.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Random rand = new Random();
				double x = rand.nextInt(80);
				double y = rand.nextInt(170);
				callBack.onLocationChanged(x,y);				
			}
			
		});
		
		Timer myTimer = new Timer();
		final Handler uiHandler = new Handler();
		myTimer.schedule(new TimerTask() {
		    @Override
		    public void run() {
		    	uiHandler.post(new Runnable() {
		            @Override
		            public void run() {
		            	destination = (TextView) view.findViewById(R.id.editText1);
						String[] dest = destination.getText().toString().split(":");
						new Request().execute("get", dest[0], dest[1]);
		            }
		        });
		    };
		}, 5000, 60000);
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	private class Request extends AsyncTask<String, Void, String> {
		String result;
        @Override
        protected String doInBackground(String... param) {
        	Client client = new Client();
        	String message = param[0];
        	String serverIp = param[1];
        	int port = Integer.valueOf(param[2]);
        	try {
        		result = client.sendMessage(message, serverIp, port);
			} catch (Exception e) {
				Log.e("Request task", "Failed request", e);
			}
            return result;
        }
        
        @Override
        protected void onPostExecute(String result) {
        	super.onPostExecute(result);
        	if(result != null){
        		double [] coord = parse(result);
        		responseData.setText(result);
        		callBack.onLocationChanged(coord[0],coord[1]);
        		if(result.contains("alert")){
        			LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        		    View popupView = layoutInflater.inflate(R.layout.alert_layout, null);  
        		    final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        		    mediaPlayer = MediaPlayer.create(view.getContext(), R.raw.alert);
    		        mediaPlayer.start();
    		                  
        		    Button ok = (Button) popupView.findViewById(R.id.Ok);
        		    ok.setOnClickListener(new Button.OnClickListener(){
        		     @Override
        		     public void onClick(View v) {
        		    	 mediaPlayer.stop();
        		    	 MediaPlayer mp = MediaPlayer.create(view.getContext(), R.raw.disable_alert);
        		    	 mp.start();
        		    	 try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        		    	mp.stop();
        		    	popupWindow.dismiss(); 
        		     }});
        		    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);        		                       		   
        		}
        	}
        	else{
        		Toast.makeText(view.getContext(),"Failed to connect to server", Toast.LENGTH_SHORT).show();
        	}
        }
    }
	
	private double[] parse(String result){
		String [] buf = result.split("\\$");
		String current = buf[1];
		String [] dataOfCurrent = current.split(",");
		String lastValid = null;
		String [] dataOfLast = null;
		Double x = null;
		Double y = null;
		
		if(buf.length == 3){
			lastValid = buf[2];
			dataOfLast = lastValid.split(",");
		}
				
		if(dataOfCurrent[2].equals("A")){
			x = Double.valueOf(dataOfCurrent[3].substring(0, 2))+Double.valueOf(dataOfCurrent[3].substring(2, dataOfCurrent[3].length()))/60.0;
			y = Double.valueOf(dataOfCurrent[5].substring(0, 3))+Double.valueOf(dataOfCurrent[5].substring(3, dataOfCurrent[5].length()))/60.0;
			if(dataOfCurrent[4].equals("S")){
				x=-x;
			}
			if(dataOfCurrent[6].equals("W")){
				y=-y;
			}
		}
		else if(lastValid != null){
			x = Double.valueOf(dataOfLast[3].substring(0, 2))+Double.valueOf(dataOfLast[3].substring(2, dataOfLast[3].length()))/60.0;
			y = Double.valueOf(dataOfLast[5].substring(0, 3))+Double.valueOf(dataOfLast[5].substring(3, dataOfLast[5].length()))/60.0;
			if(dataOfLast[4].equals("S")){
				x=-x;
			}
			if(dataOfLast[6].equals("W")){
				y=-y;
			}
		}
		double [] coord = {x,y};
		return coord;
	}

}
