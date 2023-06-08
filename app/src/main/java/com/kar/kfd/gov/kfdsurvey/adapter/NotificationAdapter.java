package com.kar.kfd.gov.kfdsurvey.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.constants.Constants;
import com.kar.kfd.gov.kfdsurvey.model.NotificationModel;
import com.kar.kfd.gov.kfdsurvey.notification.ViewNotificationActivity;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.AdapterViewHolder> {

    public static final String TAG = Constants.SARATH;
    List<NotificationModel> mNotification;
    private Context mContext;
    private LayoutInflater layoutInflater;


    public NotificationAdapter(Context mContext, List<NotificationModel> notificationModelList) {
        this.mContext = mContext;
        this.mNotification = notificationModelList;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notification_row, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {

        NotificationModel notificationModel = mNotification.get(position);
        holder.tvHeader.setText(notificationModel.getTitle());
        holder.tvbody.setText(notificationModel.getMessage());
        holder.rlNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ViewNotificationActivity.class);
                i.putExtra("title", notificationModel.getTitle());
                i.putExtra("body", notificationModel.getMessage());
                i.putExtra("url", notificationModel.getUrl());
                mContext.startActivity(i);
            }
        });

    }

    public void setNotificationData(List<NotificationModel> notification) {
        this.mNotification = notification;
    }


    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader, tvbody;
        View rlNotification;


        public AdapterViewHolder(View itemView) {
            super(itemView);

            tvHeader = itemView.findViewById(R.id.tvHeader);
            tvbody = itemView.findViewById(R.id.tvbody);
            rlNotification = itemView.findViewById(R.id.rlNotification);

            rlNotification.setOnClickListener(v -> {
                mContext.startActivity(new Intent(mContext, ViewNotificationActivity.class));
            });

        }


    }


}


