package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by NIKUNJ on 09-02-2018.
 */

public class NotesCursorAdapter extends BaseAdapter implements Filterable {

    private ImageView photoField;
    String address,country;
    Context context;
    List<model> gallryList = new ArrayList<model>();
    private static LayoutInflater inflater = null;
    public static List<model> arraylist = null;
    CustomFilter cs;
    ArrayList<model> filters = null;



    public NotesCursorAdapter(FragmentActivity activity, List<model> data) {

        this.gallryList = data;
        this.arraylist = data;
        this.context = activity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return gallryList.size();
    }

    @Override
    public Object getItem(int i) {
        return gallryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {
        TextView tvNote, tvLocation, tvtime, tvcity;
        ImageView Image;


    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.note_list_item, null);
            holder = new Holder();

            holder.tvNote = (TextView) view.findViewById(R.id.tvNote);
            holder.tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            holder.tvtime= (TextView) view.findViewById(R.id.tvtime);
            holder.tvcity= (TextView) view.findViewById(R.id.tvcity);
            holder.Image = (ImageView) view.findViewById(R.id.imageDocIcon);

            view.setTag(holder);

        } else {
            holder = (Holder) view.getTag();
        }



        final model getSet = gallryList.get(i);
        String City = getSet.getCity();

        String State = getSet.getState();


        holder.tvNote.setText(getSet.getNoteText());
        holder.tvLocation.setText(getSet.getAddress());
        holder.tvtime.setText(getSet.getDataTime());
        holder.tvcity.setText(City+", "+State+", " + getSet.getCountry() );
        Log.e("dataType", getSet.getDataType()+"");
        Log.e("Log",getSet.getAudioPath());

        if(getSet.getDataType()==1) {

            Uri imageFilePath = Uri.parse(getSet.getPath());
            Log.e("imageFile", getSet.getPath());
            holder.Image.setImageURI(imageFilePath);
        }else {

            holder.Image.setImageResource(R.drawable.audio_icon);
        }

                holder.Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                if(getSet.getDataType()==1) {

                    Intent intent = new Intent(context, FullImageActivity.class );

                    intent.putExtra("imageString", getSet.getPath());
                    context.startActivity(intent);
                }else {

                    Toast.makeText(context, "No Image Available", Toast.LENGTH_SHORT).show();
                }



            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getSet.getDataType()==1) {

                    Intent intent = new Intent(context, CameraActivity.class);
                    intent.putExtra("edit", true );
                    intent.putExtra("id", getSet.getId());
                    intent.putExtra("title", getSet.getNoteText());
                    intent.putExtra("imagePath", getSet.getPath());
                    intent.putExtra("lati", getSet.getLatitude());
                    intent.putExtra("longi", getSet.getLongitude());
                    intent.putExtra("dateTime", getSet.getDataTime());
                    intent.putExtra("address", getSet.getAddress());
                    context.startActivity(intent);


                }else {

                    Intent intent = new Intent(context, AudioNote_RecordActivity.class);

                    intent.putExtra("id", getSet.getId());
                    intent.putExtra("title", getSet.getNoteText());
                    intent.putExtra("lati", getSet.getLatitude());
                    intent.putExtra("longi", getSet.getLongitude());
                    intent.putExtra("dateTime", getSet.getDataTime());
                    intent.putExtra("address", getSet.getAddress());
                    intent.putExtra("AudioPath", getSet.getAudioPath());
                    context.startActivity(intent);


                }
            }

        });


        return view;
    }

    @Override
    public Filter getFilter() {

        if(cs == null){
            cs = new CustomFilter();
        }

        return cs;
    }

//cropped4853222538286229255
    class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filters = null;
            FilterResults results = new FilterResults();

            if (arraylist == null)
                arraylist = gallryList;

            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toLowerCase();
                Log.e("canstraint", constraint+"");
                Log.e("filterText", Map_mainFragment.check);
                filters = new ArrayList<>();
                for (int i = 0; i < arraylist.size(); i++) {

                    if(Map_mainFragment.check.equals("title")){

                        if (arraylist.get(i).getNoteText().toLowerCase().contains(constraint)) {

                         //   model model = new model(arraylist.get(i).getNoteText(), arraylist.get(i).getAddress(),  arraylist.get(i).getCity(), arraylist.get(i).getState(), arraylist.get(i).getCountry(), arraylist.get(i).getAudioPath(), arraylist.get(i).getPath(), arraylist.get(i).getId(), arraylist.get(i).getDataTime()  );
                            filters.add(arraylist.get(i));
                            Log.e("AudioPath", arraylist.get(i).getAudioPath());

                        }
                    }
                    else if (Map_mainFragment.check.equals("city")){

                        if (arraylist.get(i).getCity().toLowerCase().contains(constraint)) {

                            filters.add(arraylist.get(i));

                        }}
                    else if (Map_mainFragment.check.equals("state")){

                        if (arraylist.get(i).getState().toLowerCase().contains(constraint)) {

                            filters.add(arraylist.get(i));
                        }}
                    else if (Map_mainFragment.check.equals("country")){

                        if (arraylist.get(i).getCountry().toLowerCase().contains(constraint)) {

                            filters.add(arraylist.get(i));


                        }
                    }

                }
                results.count = filters.size();
                results.values = filters;
            }else {
                results.count = arraylist.size();
                results.values = arraylist;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            gallryList = (ArrayList<model> )filterResults.values;
            notifyDataSetChanged();

        }
    }


}

