package com.codepath.instagramphotoviewer;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    private static class ViewHolder {
        TextView username;
        TextView caption;
        TextView createdTime;
        TextView likes;
        ImageView photo;
        RoundedImageView avatar;
    }

    public PhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent,false);
            viewHolder.username = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.caption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.createdTime = (TextView)convertView.findViewById(R.id.tvCreatedTime);
            viewHolder.likes =(TextView) convertView.findViewById(R.id.tvlikes);
            viewHolder.avatar = (RoundedImageView) convertView.findViewById(R.id.rivAvatar);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.username.setText(photo.userName);
        viewHolder.caption.setText(photo.caption);
        viewHolder.likes.setText(String.format("%s %s",photo.likeCount , getContext().getResources().getString(R.string.likes)));
        viewHolder.createdTime.setText(DateUtils.getRelativeTimeSpanString(photo.createdTime * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        int deviceWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        Picasso.with(getContext()).load(photo.imageUrl).resize(deviceWidth, 0).into(viewHolder.photo);



        Picasso.with(getContext())
                .load(photo.avatarUrl)
                .fit()
                .into(viewHolder.avatar);
        return convertView;
    }
}
