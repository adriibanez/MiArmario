package com.adrianibanez.nosequeponerme.Servicios;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.adrianibanez.nosequeponerme.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificacionesFirebase extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    //Método de escucha de notificaciones recibidas
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String titulo = remoteMessage.getNotification().getTitle();
        String cuerpo = remoteMessage.getNotification().getBody();

        //Canal de notificaciones que vincula la app con el dispositivo movil
        final String ID_CANAL = "CANAL_PRINCIPAL";
        NotificationChannel canal = new NotificationChannel(ID_CANAL,"canal1", NotificationManager.IMPORTANCE_HIGH);
        //Se crea el canal
        getSystemService(NotificationManager.class).createNotificationChannel(canal);
        //Se crea la notificacion
        Notification.Builder notificacion = new Notification.Builder(getApplicationContext(),ID_CANAL)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setSmallIcon(R.drawable.armario)
                .setAutoCancel(true);
        //Se lanza la notificacion
        NotificationManagerCompat.from(this).notify(1,notificacion.build());




    }
}
