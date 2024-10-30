package android_serialport_api.mx.xingbang.custom;

import android.os.AsyncTask;
import android.widget.TextView;

public class FiringAsyncTask extends AsyncTask<Void, Void, Void> {

	private TextView firstTxt ;
	
	public FiringAsyncTask(TextView firstTxt){
		this.firstTxt = firstTxt;
	}
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		//publishProgress(0);
		return null;
	}
	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		firstTxt.setText(""+values[0]);
		super.onProgressUpdate(values);
	}

	
	
}
