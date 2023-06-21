package com.example.backgroundlocationtracker.util

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.TextView

//TvExtension that receives a substring to make it clickable and receives the action to perform
fun TextView.linkify(substring: String, action: ClickableSpan){
    val tvText = text.toString()
    val spannableString = SpannableString(tvText)
    val indexes = tvText.findFirstLastIndex(substring = substring)
    if (indexes.first > -1) {
        spannableString.setSpan(
            action,
            indexes.first,
            indexes.second,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = spannableString
        highlightColor = Color.TRANSPARENT
        movementMethod = LinkMovementMethod.getInstance()
    }
}