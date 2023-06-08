package com.kar.kfd.gov.kfdsurvey.notification;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kar.kfd.gov.kfdsurvey.Database;
import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.adapter.NotificationAdapter;
import com.kar.kfd.gov.kfdsurvey.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView rvNotifications;
    RelativeLayout rlNotification_activity, rlNoNotification;
    List<NotificationModel> notificationModelList;
    private NotificationAdapter notificationAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        rvNotifications = findViewById(R.id.rvNotifications);
        rlNotification_activity = findViewById(R.id.rlNotification_activity);
        rlNoNotification = findViewById(R.id.rlNoNotification);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        notificationModelList = new ArrayList<>();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupRecyclerView();

        getNotification();
    }

    private void getNotification() {
        Database db = new Database(this);
        Cursor cursor = db.getNotifications();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int notificationId = cursor.getInt(cursor.getColumnIndex(Database.NOTIFICATION_ID));
                int notificationStatus = cursor.getInt(cursor.getColumnIndex(Database.NOTIFICATION_STATUS));
                String title = cursor.getString(cursor.getColumnIndex(Database.NOTIFICATION_TITLE));
                String message = cursor.getString(cursor.getColumnIndex(Database.NOTIFICATION_MESSAGE));
                String url = cursor.getString(cursor.getColumnIndex(Database.NOTIFICATION_URL));
                String timeStamp = cursor.getString(cursor.getColumnIndex(Database.CREATION_TIMESTAMP));


                notificationModelList.add(new NotificationModel(notificationId, notificationStatus, title, message, url, timeStamp));
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (notificationModelList.isEmpty()) {
            rlNoNotification.setVisibility(View.VISIBLE);
            rlNotification_activity.setVisibility(View.INVISIBLE);
        } else {
            rlNoNotification.setVisibility(View.INVISIBLE);
            rlNotification_activity.setVisibility(View.VISIBLE);
        }

        notificationAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {

        notificationAdapter = new NotificationAdapter(this, notificationModelList);
        notificationAdapter.setNotificationData(notificationModelList);
        rvNotifications.setAdapter(notificationAdapter);

    }

}
