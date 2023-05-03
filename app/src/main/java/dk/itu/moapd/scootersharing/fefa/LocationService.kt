package dk.itu.moapd.scootersharing.fefa

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*

class LocationService : Service() {

    companion object {
        private lateinit var lat : String
        private lateinit var lon : String
        fun getLat() : String {
            return lat
        }
        fun getLon() : String {
            return lon
        }
        private const val LOCATION_NOTIFICATION_ID = 1001
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lat = locationResult.lastLocation!!.latitude.toString()
                lon = locationResult.lastLocation!!.longitude.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission", "LaunchActivityFromNotification")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getService(
            applicationContext,
            0,
            Intent(applicationContext, LocationService::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        startForegroundService(intent.apply {
            this!!.putExtra("inputExtra", "Foreground Service Example in Android")
            this.putExtra("pendingIntent", pendingIntent)
        })

        val builder = NotificationCompat.Builder(this, "default")
            .setContentTitle("Location Service")
            .setContentText("Running...")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        val notification = builder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "Channel name",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        startForeground(LOCATION_NOTIFICATION_ID, notification)

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

