package uk.ac.mmu.electricchargingproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CustomAdaper extends BaseAdapter {

    Context context;
    ArrayList<MyDataModel> dataList;
    private static LayoutInflater inflater=null;
    double latitude_current;
    double longitude_current;

    public CustomAdaper(Context context, ArrayList<MyDataModel> dataListOne,double latitude_current,double longitude_current)
    {
        // TODO Auto-generated constructor stub
        dataList=dataListOne;
        this.context=context;
        this.latitude_current =latitude_current;
        this.longitude_current =longitude_current;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView;
        rowView = inflater.inflate(R.layout.layout_row_view, null);

        TextView name = (TextView) rowView.findViewById(R.id.name_textveiw);
        TextView distance = (TextView) rowView.findViewById(R.id.distance_textview);
        TextView paymentRequired=(TextView) rowView.findViewById(R.id.payment_textview);

        LinearLayout linearLayout = rowView.findViewById(R.id.wahwah);

        Button map_btn =(Button)rowView.findViewById(R.id.map_btn);

        map_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String uri="https://www.google.com/maps/search/?api=1&query="+dataList.get(position).getLatitude()+","+dataList.get(position).getLongitude()+"&z=17";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.apps.maps");
                context.startActivity(intent);

            }
        });


        // Will show which charging point requires payment

        if (dataList.get(position).isPaymentRequired())
        {
            paymentRequired.setText("Yes");
        }
        else
        {
            paymentRequired.setText("No");
        }

        name.setText(String.valueOf(dataList.get(position).getName()));
        distance.setText(String.format("%.2f", (dataList.get(position).getDistance())));
        return rowView;
    }
}


