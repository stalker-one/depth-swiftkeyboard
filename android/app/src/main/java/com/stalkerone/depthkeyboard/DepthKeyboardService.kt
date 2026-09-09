package com.stalkerone.depthkeyboard

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class DepthKeyboardService : InputMethodService() {
    private var shift=false; private var symbols=false; private var tools=false
    private lateinit var model: TypingModel
    private val p get()=getSharedPreferences(SettingsActivity.PREFS,MODE_PRIVATE)
    private val scale get()=p.getInt(SettingsActivity.KEY_SIZE,100)/100f
    private val height get()=p.getInt(SettingsActivity.KEY_HEIGHT,100)/100f
    private val compact get()=p.getBoolean(SettingsActivity.KEY_COMPACT,false)
    private val dark get()=p.getBoolean(SettingsActivity.KEY_DARK_THEME,true)

    override fun onCreate(){super.onCreate();model=TypingModel(this)}
    override fun onCreateInputView():View=build()
    override fun onStartInput(a:EditorInfo?,r:Boolean){super.onStartInput(a,r);shift=false;symbols=false;tools=false}
    override fun onEvaluateInputViewShown()=true

    private fun build():View{
        val bg=if(dark)Color.rgb(17,24,39)else Color.rgb(245,247,250);val fg=if(dark)Color.WHITE else Color.rgb(24,36,58)
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(dp(5),dp(3),dp(5),dp(7));setBackgroundColor(bg)}
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};val w=(resources.displayMetrics.widthPixels*p.getInt(SettingsActivity.KEY_WIDTH,100)/100f).roundToInt();root.addView(box,LinearLayout.LayoutParams(w,-2))
        val bar=LinearLayout(this).apply{gravity=Gravity.CENTER_VERTICAL}
        key(bar,"☰",.8f,fg){tools=!tools;refresh()};key(bar,"AI",.8f,fg){ai()};key(bar,"⌕",.8f,fg){search()};key(bar,"📋",.8f,fg){paste()};key(bar,"🌐",.8f,fg){language()}
        bar.addView(TextView(this).apply{text=(p.getString(SettingsActivity.KEY_LANGUAGE,"English (US)")? :"English").take(3).uppercase();setTextColor(fg);gravity=Gravity.CENTER},LinearLayout.LayoutParams(0,scaled(42),1f))
        key(bar,"⚙",.8f,fg){startActivity(Intent(this,SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))};box.addView(bar,LinearLayout.LayoutParams(-1,scaled(45)))
        if(tools){val r=LinearLayout(this).apply{gravity=Gravity.CENTER};listOf("😊 Emoji","Translate","GIFs","Stickers","Layout").forEach{s->key(r,s,1f,fg){when(s.takeWhile{it!=' '}){"😊"->commit("😀");"Translate"->commit("[Translate] ");"GIFs","Stickers"->Toast.makeText(this,"Media provider not configured",Toast.LENGTH_SHORT).show();"Layout"->startActivity(Intent(this,SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))}}};box.addView(r,LinearLayout.LayoutParams(-1,scaled(46)))}else suggestions(box,fg)
        val lang=p.getString(SettingsActivity.KEY_LANGUAGE,"English (US)")?:"English (US)";val lay=p.getString(SettingsActivity.KEY_LAYOUT,"QWERTY")?:"QWERTY";val rows=if(symbols)listOf("1234567890","@#$%&*+-=","()[]{}!?/")else KeyboardLayouts.rows(lang,lay)
        rows.forEachIndexed{i,s->{val r=LinearLayout(this).apply{gravity=Gravity.CENTER};if(i==2)key(r,if(symbols)"ABC":"⇧",1f,fg){if(symbols)symbols=false else shift=!shift;refresh()};s.forEach{ch->val v=if(shift&&!symbols)ch.uppercaseChar().toString()else ch.toString();key(r,v,1f,fg){commit(v);if(shift&&!symbols){shift=false;refresh()}}};if(i==2)key(r,"⌫",1.2f,fg){backspace()};box.addView(r,LinearLayout.LayoutParams(-1,scaled(if(compact)43 else 51)))}
        val b=LinearLayout(this).apply{gravity=Gravity.CENTER};key(b,"#+=",.9f,fg){symbols=!symbols;refresh()};key(b,",",.8f,fg){commit(",")};key(b,"space",4f,fg){space()};key(b,".",.8f,fg){commit(".")};key(b,"↵",1.1f,fg){enter()};box.addView(b,LinearLayout.LayoutParams(-1,scaled(56)));return root
    }
    private fun suggestions(box:LinearLayout,fg:Int){val text=currentInputConnection?.getTextBeforeCursor(80,0)?.toString().orEmpty();val pre=text.takeLastWhile{it.isLetter()};val prev=text.dropLast(pre.length).trimEnd().substringAfterLast(' ');val words=(model.suggestions(pre,prev)+FeatureEngine.predictions(prev)).distinct().take(3).ifEmpty{listOf("the","and","you")};val r=LinearLayout(this).apply{gravity=Gravity.CENTER};words.forEach{x->key(r,x,1f,fg){commit(x+" ")}};box.addView(r,LinearLayout.LayoutParams(-1,scaled(44)))}
    private fun key(r:LinearLayout,s:String,w:Float,fg:Int,a:()->Unit){val b=Button(this).apply{text=s;textSize=if(s.length>1)10f else 18f;setTextColor(fg);isAllCaps=false;setPadding(0,0,0,0);minHeight=0;minWidth=0;background=GradientDrawable().apply{setColor(if(dark)Color.rgb(31,41,55)else Color.WHITE);cornerRadius=dp(9).toFloat()};setOnClickListener{a()}};r.addView(b,LinearLayout.LayoutParams(0,scaled(if(s.length>1)42 else if(compact)43 else 51),w).apply{setMargins(dp(2),dp(2),dp(2),dp(2))})}
    private fun commit(s:String){currentInputConnection?.commitText(s,1);if(!p.getBoolean(SettingsActivity.KEY_INCOGNITO,false))model.learn(s)}
    private fun paste(){val cm=getSystemService(CLIPBOARD_SERVICE)as android.content.ClipboardManager;cm.primaryClip?.let{if(it.itemCount>0)commit(it.getItemAt(0).coerceToText(this).toString())}}
    private fun space(){if(currentInputConnection?.getTextBeforeCursor(2,0)?.toString()=="  "){currentInputConnection?.deleteSurroundingText(2,0);commit(". ")}else commit(" ")}
    private fun backspace(){val c=currentInputConnection?:return;val x=c.getTextBeforeCursor(80,0)?.toString().orEmpty();c.deleteSurroundingText(if(x.endsWith(" "))1 else x.takeLastWhile{!it.isWhitespace()}.length.coerceAtLeast(1),0)}
    private fun enter(){val c=currentInputConnection?:return;val a=currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)?:0;if(a!=0)c.performEditorAction(a)else{c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_ENTER))}}
    private fun ai(){val x=currentInputConnection?.getTextBeforeCursor(500,0)?.toString().orEmpty();if(x.isBlank())Toast.makeText(this,"Type text first",Toast.LENGTH_SHORT).show()else commit(FeatureEngine.tonePrompt("professional",x))}
    private fun search(){val q=currentInputConnection?.getTextBeforeCursor(150,0)?.toString().orEmpty();startActivity(Intent(Intent.ACTION_VIEW,android.net.Uri.parse("https://www.google.com/search?q="+android.net.Uri.encode(q))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))}
    private fun language(){val a=KeyboardLayouts.languages;val old=p.getString(SettingsActivity.KEY_LANGUAGE,a[0])?:a[0];p.edit().putString(SettingsActivity.KEY_LANGUAGE,a[(a.indexOf(old)+1).mod(a.size)]).apply();refresh()}
    private fun refresh(){setInputView(build())};private fun scaled(v:Int)=dp(v*scale*height);private fun dp(v:Int)=(v*resources.displayMetrics.density).roundToInt()
}
