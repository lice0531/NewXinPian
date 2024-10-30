package android_serialport_api.mx.xingbang.services.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.services.socket.DenatorClient;

/**
 * Created by 坤 on 2016/9/6.
 */
public class WifiListAdapter extends ArrayAdapter<DenatorClient> {

    private final LayoutInflater mInflater;
    private int mResource;

    public WifiListAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.serial);
        TextView signl = (TextView) convertView.findViewById(R.id.equNo);
        TextView ip = (TextView) convertView.findViewById(R.id.ip);
        TextView state = (TextView) convertView.findViewById(R.id.state);

        DenatorClient r = getItem(position);
        
        name.setText(""+r.getSerial());
        signl.setText(r.getEquNo());
        ip.setText(r.getIp());
        state.setText(r.getState());
        
        return convertView;
    }

}
