package com.example.coughredirect
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity: ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState)
  val p=getSharedPreferences("settings",MODE_PRIVATE)
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(30,40,30,30)}
  fun t(s:String,z:Float=16f)=TextView(this).apply{text=s;textSize=z;setPadding(0,10,0,10)}
  box.addView(t("🤧 기침 이동",26f));box.addView(t("다른 사이트를 보고 있어도 기침을 감지합니다."))
  val url=EditText(this).apply{hint="https://example.com";setSingleLine(true);setText(p.getString("url",""))};box.addView(url)
  val save=Button(this).apply{text="목적지 저장"};box.addView(save)
  val st=t("기침 감도: ${p.getInt("sensitivity",50)}");box.addView(st)
  val seek=SeekBar(this).apply{max=99;progress=p.getInt("sensitivity",50)-1};box.addView(seek)
  box.addView(t("낮을수록 작은 소리에도 민감합니다.",13f))
  val start=Button(this).apply{text="🎤 백그라운드 감지 시작"};box.addView(start)
  val stop=Button(this).apply{text="감지 중지"};box.addView(stop)
  val status=t("대기 중");box.addView(status);setContentView(box)
  seek.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(b:SeekBar?,v:Int,u:Boolean){val x=v+1;st.text="기침 감도: $x";p.edit().putInt("sensitivity",x).apply()};override fun onStartTrackingTouch(b:SeekBar?){ };override fun onStopTrackingTouch(b:SeekBar?){ }})
  save.setOnClickListener{p.edit().putString("url",url.text.toString().trim()).apply();status.text="목적지를 저장했습니다."}
  start.setOnClickListener{val target=url.text.toString().trim();if(!(target.startsWith("http://")||target.startsWith("https://"))){status.text="http:// 또는 https:// 주소를 입력하세요.";return@setOnClickListener};p.edit().putString("url",target).apply();if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.POST_NOTIFICATIONS),100);status.text="권한을 허용한 뒤 다시 눌러주세요.";return@setOnClickListener};ContextCompat.startForegroundService(this,Intent(this,CoughService::class.java));status.text="🎤 백그라운드에서 감지 중"}
  stop.setOnClickListener{stopService(Intent(this,CoughService::class.java));status.text="중지됨"}
 }
}
