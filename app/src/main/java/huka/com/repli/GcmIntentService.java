package huka.com.repli;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import adapters.MyRecyclerCameraAdapter;
import servercalls.UploadPicToRandomAsyncTask;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public void showNotification(String title, String text){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.camera_icon)
                        .setContentTitle(title)
                        .setContentText(text);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        mBuilder.setLights(Color.rgb(18,255,0),500,500);
        long[] pattern = {500,500,500,500};
        mBuilder.setVibrate(pattern);
        mBuilder.setAutoCancel(true);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        mNotificationManager.notify(0, mBuilder.build());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                Intent cameraIntent = new Intent("unique_name");
                Intent repliIntent = new Intent("repli");
                //put whatever data you want to send, if any
                if(extras.getString("message") != null) {
                    if(!MainActivity.isActivityVisible()) {
                        showNotification("Repli", "New random pictures");
                    }
                    cameraIntent.putExtra("message", extras.getString("message")).putExtra("account", extras.getString("account"));
                    System.out.println("send brodcast");
                    System.out.println("Blobkey from gcm: " + extras.getString("message"));
                    //send broadcast
                    this.getApplication().sendBroadcast(cameraIntent);
                }
                if(extras.getString("accName")!= null){
                    if(!MainActivity.isActivityVisible()) {
                        showNotification("Repli", "New repli from " +  extras.getString("accName"));
                    }

                    repliIntent.putExtra("accName", extras.getString("accName"));
                    System.out.println("send brodcast");
                    System.out.println("Blobkey from gcm: " + extras.getString("accName"));
                    //send broadcast
                    this.getApplication().sendBroadcast(repliIntent);
                }
                //Toast.makeText(getApplicationContext(), extras.getString("message"), Toast.LENGTH_LONG).show();


//                String accountName = intent.getStringExtra("accountName");
//                Logger.getLogger("accountname received").log(Level.INFO, accountName);

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {

        // WeakReference<MyAsyncTask> asyncTaskWeakRef = new WeakReference<>(asyncTask);
        //   asyncTask.execute(message);
//
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//            }
//        });
    }
}