package com.example.backgroundlocationtracker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.backgroundlocationtracker.util.findFirstLastIndex

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Task #1
        setOnClickHello()
    }

    //sets click on substring "Hello" of the text view
    private fun setOnClickHello() {
        val helloWorldTv = findViewById<TextView>(R.id.id_text_view)
        val tvText = helloWorldTv.text.toString()
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@MainActivity, "Hello Toast!", Toast.LENGTH_SHORT).show()
            }
        }
        val spannableString = SpannableString(tvText)
        val indexes = tvText.findFirstLastIndex(substring = "Hello")
        if (indexes.first > -1) {
            spannableString.setSpan(
                clickableSpan,
                indexes.first,
                indexes.second,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            helloWorldTv.text = spannableString
            helloWorldTv.highlightColor = Color.TRANSPARENT
            helloWorldTv.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}