package com.stalkerone.depthkeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

class SettingsActivity:Activity(){
 companion object{
  const val PREFS="depth_keyboard_preferences";const val KEY_SIZE="keyboard_size";const val KEY_WIDTH="keyboard_width";const val KEY_HEIGHT="keyboard_height";const val KEY_COMPACT="compact_layout";const val KEY_ONE_HANDED="one_handed";const val KEY_FLOATING="floating_layout";const val KEY_LAYOUT_MODE="layout_mode";const val KEY_DARK_THEME="dark_theme";const val KEY_SUGGESTIONS="suggestions";const val KEY_INCOGNITO="incognito";const val KEY_LANGUAGE="language";const val KEY_LAYOUT="layout";const val KEY_THEME="theme_id"
 }
 private val p by lazy{getSharedPreferences(PREFS,MODE_PRIVATE)};private lateinit var status:TextView;private val updater by lazy{AppUpdateManager(this)}
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(content());updater.checkOnLaunch()}
 private fun content():View{val s=ScrollView(this);val r=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(28,24,28,28)}
  r.addView(TextView(this).apply{text="Depth Keyboard";textSize=28f});r.addView(TextView(this).apply{text="Complete keyboard controls";textSize=14f})
  status=TextView(this).apply{setPadding(14,12,14,12)};r.addView(status)
  r.addView(title("Keyboard"));r.addView(slider("Key size",KEY_SIZE,80,140));r.addView(slider("Height",KEY_HEIGHT,70,140));r.addView(slider("Width",KEY_WIDTH,70,100))
  r.addView(button("Standard layout"){mode("standard")});r.addView(button("One-handed layout"){mode("one-handed")});r.addView(button("Floating layout"){mode("floating")});r.addView(sw("Compact keys",KEY_COMPACT,false));r.addView(sw("Dark theme",KEY_DARK_THEME,true));r.addView(sw("Smart predictions",KEY_SUGGESTIONS,true));r.addView(sw("Incognito / stop learning",KEY_INCOGNITO,false))
  r.addView(title("Language & layout"));r.addView(select("Language",KEY_LANGUAGE,KeyboardLayouts.languages));r.addView(select("Keyboard layout",KEY_LAYOUT,listOf("QWERTY","QWERTZ","AZERTY")))
  r.addView(title("Themes"));r.addView(select("Theme",KEY_THEME,ThemeCatalog.themes.map{it.id}))
  r.addView(title("Privacy & data"));r.addView(button("Clear learned words"){TypingModel(this).clear();Toast.makeText(this,"Personalization cleared",Toast.LENGTH_SHORT).show()});r.addView(button("Clear clipboard history"){ClipboardStore(this).clear();Toast.makeText(this,"Clipboard history cleared",Toast.LENGTH_SHORT).show()})
  r.addView(title("System"));r.addView(button("Enable / choose keyboard"){try{(getSystemService(INPUT_METHOD_SERVICE)as InputMethodManager).showInputMethodPicker()}catch(_:Throwable){startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))}});r.addView(button("Open keyboard settings"){startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))});r.addView(TextView(this).apply{text="AI, web search, GIF/sticker providers and cloud sync use provider-neutral interfaces; no API secret is bundled in the keyboard.";textSize=12f;setPadding(0,20,0,0)})
  s.addView(r);updateStatus();return s }
 private fun title(x:String)=TextView(this).apply{text=x;textSize=18f;setPadding(0,22,0,8)}
 private fun button(x:String,a:()->Unit)=Button(this).apply{text=x;isAllCaps=false;setOnClickListener{a()}}
 private fun sw(x:String,k:String,d:Boolean):View=Switch(this).apply{text=x;isChecked=p.getBoolean(k,d);setOnCheckedChangeListener{_,v->p.edit().putBoolean(k,v).apply();if(k==KEY_ONE_HANDED&&v)mode("one-handed");if(k==KEY_FLOATING&&v)mode("floating")}}
 private fun slider(x:String,k:String,min:Int,max:Int):View{val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};box.addView(TextView(this).apply{text=x});val bar=SeekBar(this).apply{this.max=max-min;progress=p.getInt(k,100).coerceIn(min,max)-min;setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(v:SeekBar?,n:Int,f:Boolean){p.edit().putInt(k,n+min).apply()};override fun onStartTrackingTouch(v:SeekBar?){};override fun onStopTrackingTouch(v:SeekBar?){} })};box.addView(bar);return box}
 private fun select(x:String,k:String,values:List<String>):View{val b=Button(this);fun refresh(){b.text="$x: ${p.getString(k,values[0])}"};b.setOnClickListener{val i=values.indexOf(p.getString(k,values[0])).coerceAtLeast(0);p.edit().putString(k,values[(i+1)%values.size]).apply();refresh()};refresh();return b}
 private fun mode(m:String){p.edit().putString(KEY_LAYOUT_MODE,m).putBoolean(KEY_ONE_HANDED,m=="one-handed").putBoolean(KEY_FLOATING,m=="floating").apply();updateStatus()}
 private fun updateStatus(){if(!::status.isInitialized)return;status.text="Layout: ${p.getString(KEY_LAYOUT_MODE,"standard")} · ${p.getString(KEY_LANGUAGE,"English (US)")} · ${p.getString(KEY_LAYOUT,"QWERTY")}"}
 override fun onResume(){super.onResume();if(::status.isInitialized)updateStatus();updater.onResume()}
}
