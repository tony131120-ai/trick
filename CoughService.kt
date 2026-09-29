package com.example.coughredirect
import android.app.*
import android.content.Intent
import android.media.*
import android.os.*
import androidx.core.app.NotificationCompat
import kotlin.math.abs
import kotlin.math.sqrt
class CoughService:Service(){
 private var recorder:AudioRecord?=null;@Volatile private var running=false;private var lastTrigger=0L
 override fun onCreate(){super.onCreate();getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("cough","기침 감지",NotificationManager.IMPORTANCE_LOW));startForeground(10,NotificationCompat.Builder(this,"cough").setContentTitle("기침 이동").setContentText("기침 감지 중").setSmallIcon(android.R.drawable.ic_btn_speak_now).setOngoing(true).build())}
 override fun onStartCommand(i:Intent?,f:Int,id:Int):Int{if(!running)listen();return START_STICKY}
 private fun listen(){running=true;Thread{val rate=16000;val min=AudioRecord.getMinBufferSize(rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);val size=maxOf(min,2048);recorder=AudioRecord(MediaRecorder.AudioSource.MIC,rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,size);val buf=ShortArray(size);try{recorder?.startRecording()}catch(_:Exception){running=false;return@Thread};while(running){val n=recorder?.read(buf,0,buf.size)?:0;if(n<=0)continue;var sum=0.0;var peak=0.0;for(i in 0 until n){val a=abs(buf[i].toDouble())/32768.0;sum+=a*a;if(a>peak)peak=a};val rms=sqrt(sum/n);val sens=getSharedPreferences("settings",MODE_PRIVATE).getInt("sensitivity",50).coerceIn(1,100);val rt=.095-sens/100.0*.075;val pt=.32-sens/100.0*.22;val now=System.currentTimeMillis();if(rms>rt&&peak>pt&&now-lastTrigger>5000){lastTrigger=now;openTarget();running=false}};try{recorder?.stop()}catch(_:Exception){};recorder?.release();recorder=null}.start()}
 private fun openTarget(){val u=getSharedPreferences("settings",MODE_PRIVATE).getString("url","")?:"";if(u.isBlank())return;startActivity(Intent(Intent.ACTION_VIEW,android.net.Uri.parse(u)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));stopSelf()}
 override fun onBind(i:Intent?)=null
 override fun onDestroy(){running=false;try{recorder?.stop()}catch(_:Exception){};recorder?.release();recorder=null;super.onDestroy()}
}
