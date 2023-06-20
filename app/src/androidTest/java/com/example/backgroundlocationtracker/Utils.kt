package com.example.backgroundlocationtracker

import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher
import org.hamcrest.Matchers


fun clickClickableSpan(textToClick: CharSequence): ViewAction {
    return object : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return Matchers.instanceOf(TextView::class.java)
        }

        override fun getDescription(): String {
            return "click on a ClickableSpan";
        }

        override fun perform(uiController: UiController, view: View) {
            val textView = view as TextView
            val spannableString = textView.text as SpannableString

            if (spannableString.isEmpty()) {
                throw NoMatchingViewException.Builder()
                    .includeViewHierarchy(true)
                    .withRootView(textView)
                    .build();
            }

            val spans =
                spannableString.getSpans(0, spannableString.length, ClickableSpan::class.java)
            if (spans.isNotEmpty()) {
                var spanCandidate: ClickableSpan
                for (span: ClickableSpan in spans) {
                    spanCandidate = span
                    val start = spannableString.getSpanStart(spanCandidate)
                    val end = spannableString.getSpanEnd(spanCandidate)
                    val sequence = spannableString.subSequence(start, end)
                    if (textToClick.toString().equals(sequence.toString())) {
                        span.onClick(textView)
                        return;
                    }
                }
            }

            throw NoMatchingViewException.Builder()
                .includeViewHierarchy(true)
                .withRootView(textView)
                .build()
        }
    }
}