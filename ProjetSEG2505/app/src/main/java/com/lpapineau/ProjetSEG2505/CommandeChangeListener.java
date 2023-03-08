package com.lpapineau.ProjetSEG2505;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lpapineau.ProjetSEG2505.User.Commande;
import com.lpapineau.ProjetSEG2505.User.CommandeList;

import java.util.Map;

public class CommandeChangeListener extends Service {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    final String CHANNEL_ID = "Background Service ID";
    int run = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        run = 0;

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        firebaseFirestore = FirebaseFirestore.getInstance();
                        mAuth = FirebaseAuth.getInstance();
                        currentUser = mAuth.getCurrentUser();





                        firebaseFirestore.collection("commandes").whereEqualTo("clientID", currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                Log.d("Change to Data Listner", "Change detected!");
                                final String textTitle = "Update to Orders!";
                                final String textContent = "There is an update to one of your orders!";




                                //NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);


                                FirebaseAuth mAuth2 = FirebaseAuth.getInstance();
                                FirebaseUser currentUser2 = mAuth2.getCurrentUser();
                                if(currentUser2 != null) {
                                    Intent gotoCommandes = new Intent(CommandeChangeListener.this, CommandesActivity.class);
                                    gotoCommandes.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(CommandeChangeListener.this, 0, gotoCommandes, PendingIntent.FLAG_IMMUTABLE);



                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(CommandeChangeListener.this, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.mealericon)
                                            .setContentTitle(textTitle)
                                            .setContentText(textContent)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setContentIntent(pendingIntent);

                                    createNotificationChannel();

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CommandeChangeListener.this);

                                    // notificationId is a unique int for each notification that you must define
                                    if(run == 1) {
                                        notificationManager.notify(1, builder.build());
                                    }
                                    run = 1;
                                }









                            }
                        });
                    }
                }
        ).start();



        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TEST CHANNEL";
            String description = "TEST CHANNEL";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service destroyed", "CommandeChangeListener service");
    }
}
