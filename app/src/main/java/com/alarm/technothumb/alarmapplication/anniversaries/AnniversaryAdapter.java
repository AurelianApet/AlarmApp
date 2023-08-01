package com.alarm.technothumb.alarmapplication.anniversaries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.technothumb.alarmapplication.R;

import java.util.ArrayList;
import java.util.List;



public class AnniversaryAdapter  extends BaseAdapter  implements Filterable  {

    Context context;
    List<Model> gallryList = new ArrayList<Model>();
    public static List<Model> arraylist = null;
    private static LayoutInflater inflater = null;
    dbHelper db;
    ArrayList<Model> filters = null;
    CustomFilter cs;



    public AnniversaryAdapter(Anniversary activity, List<Model> personIDList) {
        context = activity;
        gallryList = personIDList;
        arraylist = personIDList;
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
        TextView date, Store;
        ImageView icon, detectIcon;

    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_listview, null);
            holder = new Holder();

            db = new dbHelper(context);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.Store = (TextView) view.findViewById(R.id.title);
            holder.icon = (ImageView) view.findViewById(R.id.Icon);
            holder.detectIcon = (ImageView) view.findViewById(R.id.delect);

            view.setTag(holder);

        } else {
            holder = (Holder) view.getTag();
        }

        final Model getSet = gallryList.get(i);

        String image = getSet.getCategory();

        if(image.equals("Anniversary")){

            holder.icon.setImageResource(R.drawable.ic_action_anniversay);
        }else if(image.equals("Public Holiday")){
            holder.icon.setImageResource(R.drawable.ic_action_holiday);
        }else if(image.equals("Wedding Day")){
            holder.icon.setImageResource(R.drawable.ic_action_wedding);
        }else if(image.equals("Birthday")){
            holder.icon.setImageResource(R.drawable.ic_action_bithday);
        }else if(image.equals("Day of Departure")){
            holder.icon.setImageResource(R.drawable.ic_action_departure);
        }else {
            holder.icon.setImageResource(R.drawable.ic_action_other);
        }
        holder.date.setText(getSet.getDate());
        holder.Store.setText(getSet.getTitle());
        holder.detectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = gallryList.get(i).getTitleID();

           //   Log.e("ID", Id+"");
              db.isdelect(id);
              notifyDataSetChanged();
              Intent intent = new Intent(context, Anniversary.class);
              context.startActivity(intent);

          //     ((Anniversary)context).refresh();

                //Toast.makeText(context, "Click working fine", Toast.LENGTH_SHORT).show();

            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = gallryList.get(i).getTitleID();
                Intent intent = new Intent(context, addAnniversary.class);
                intent.putExtra("id", id);
                intent.putExtra("date", getSet.getDate());
                intent.putExtra("Title", getSet.getTitle());
                intent.putExtra("AnniID", getSet.getAnniID());
                intent.putExtra("edit", true);
                context.startActivity(intent);
                ((Activity)context).finish();

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

                filters = new ArrayList<>();
                for (int i = 0; i < arraylist.size(); i++) {

                        if (arraylist.get(i).getCategory().toLowerCase().contains(constraint)) {

                            Model model = new Model(arraylist.get(i).getTitle(), arraylist.get(i).getDate(),  arraylist.get(i).getTitleID(), arraylist.get(i).getCategory(), arraylist.get(i).getAnniID()  );
                            filters.add(model);

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
            gallryList = (ArrayList<Model> )filterResults.values;
            notifyDataSetChanged();

        }
    }


}
