package com.example.activityhomework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class MessageList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Map<String, String>> arrayList;
    @Override
    public int getItemViewType(int position) {
        return Integer.valueOf(arrayList.get(position).get("viewType"));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public MessageList(Context context, ArrayList<Map<String, String>> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(context).inflate(
                        R.layout.recycler_view_01, parent, false
                );
                return new ViewHolder0(view);
            case 2:
                View view2 = LayoutInflater.from(context).inflate(
                        R.layout.recycler_view_02, parent, false
                );
                return new ViewHolder2(view2);
            case 3:
                View view3 = LayoutInflater.from(context).inflate(
                        R.layout.recycler_view_03, parent, false
                );
                return new ViewHolder3(view3);
            case 4:
                View view4 = LayoutInflater.from(context).inflate(
                        R.layout.recycler_view_04, parent, false
                );
                return new ViewHolder4(view4);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder0 viewHolder0 = (ViewHolder0)holder;
                viewHolder0.message_01.setText(arrayList.get(position).get("message"));
                viewHolder0.datetime_01.setText(arrayList.get(position).get("dateTime"));

                break;

            case 2:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;
                viewHolder2.message_02.setText(arrayList.get(position).get("message"));
                viewHolder2.datetime_02.setText(arrayList.get(position).get("dateTime"));
                break;
            case 3:
                ViewHolder3 viewHolder3 = (ViewHolder3)holder;
                byte[] decodedString = Base64.decode(arrayList.get(position).get("encodedImage"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                viewHolder3.image.setImageBitmap(decodedByte);
                viewHolder3.datetime_03.setText(arrayList.get(position).get("dateTime"));
                break;
            case 4:
                ViewHolder4 viewHolder4 = (ViewHolder4)holder;
                String imageUrl = "http://10.0.2.2:3060/uploads/" + arrayList.get(position).get("fileName");
                new DownloadImageTask(viewHolder4.image).execute(imageUrl);
                viewHolder4.datetime_04.setText(arrayList.get(position).get("dateTime"));
                break;
        }
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
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    class ViewHolder0 extends RecyclerView.ViewHolder {

        ImageView imageView_01;
        TextView message_01;
        TextView datetime_01;
        public ViewHolder0(View itemView){
            super(itemView);
            imageView_01 = itemView.findViewById(R.id.imageView_01);
            message_01 = itemView.findViewById(R.id.message_01);
            datetime_01 = itemView.findViewById(R.id.datetime_01);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        ImageView imageView_02;
        TextView message_02;
        TextView datetime_02;
        public ViewHolder2(View itemView){
            super(itemView);
            imageView_02 = itemView.findViewById(R.id.imageView_02);
            message_02 = itemView.findViewById(R.id.message_02);
            datetime_02 = itemView.findViewById(R.id.datetime_02);
        }
    }
    class ViewHolder3 extends RecyclerView.ViewHolder {
        ImageView imageView_03;
        ImageView image;
        TextView datetime_03;
        public ViewHolder3(View itemView){
            super(itemView);
            imageView_03 = itemView.findViewById(R.id.imageView_03);
            image = itemView.findViewById(R.id.image3);
            datetime_03 = itemView.findViewById(R.id.datetime_03);
        }
    }
    class ViewHolder4 extends RecyclerView.ViewHolder {
        ImageView imageView_04;
        ImageView image;
        TextView datetime_04;
        public ViewHolder4(View itemView){
            super(itemView);
            imageView_04 = itemView.findViewById(R.id.imageView_04);
            image = itemView.findViewById(R.id.image4);
            datetime_04 = itemView.findViewById(R.id.datetime_04);
        }
    }
}