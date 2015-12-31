package com.streetsnax.srinidhi.streetsnax.adapters;

/**
 * Created by I15230 on 12/31/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.streetsnax.srinidhi.streetsnax.R;
import com.streetsnax.srinidhi.streetsnax.utilities.ItemDetails;

import java.io.InputStream;
import java.util.ArrayList;

public class ItemListBaseAdapter extends BaseAdapter {
    private static ArrayList<ItemDetails> itemDetailsrrayList;

    private LayoutInflater l_Inflater;

    public ItemListBaseAdapter(Context context, ArrayList<ItemDetails> results) {
        itemDetailsrrayList = results;
        l_Inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return itemDetailsrrayList.size();
    }

    public Object getItem(int position) {
        return itemDetailsrrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.search_item_list, null);
            holder = new ViewHolder();
            holder.searchImageContent = (ImageView) convertView.findViewById(R.id.searchImageContent);
            holder.searchTextContent = (TextView) convertView.findViewById(R.id.searchTextContent);
            holder.searchSnackContent = (TextView) convertView.findViewById(R.id.searchSnackContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.searchTextContent.setText(itemDetailsrrayList.get(position).getplaceAddress());
        new DownloadImageTask(holder.searchImageContent).execute(itemDetailsrrayList.get(position).getItemImageSrc());
        holder.searchSnackContent.setText(itemDetailsrrayList.get(position).getsnackType());

        return convertView;
    }

    static class ViewHolder {
        TextView searchTextContent;
        ImageView searchImageContent;
        TextView searchSnackContent;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}