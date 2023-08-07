package com.example.dicodingstoryapp.customui

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.example.dicodingstoryapp.R
import com.google.android.material.textfield.TextInputLayout

class EmailInputLayout : TextInputLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        errorIconDrawable = null
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val thisClass = this
        editText?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                thisClass.error = if (Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    null
                } else {
                    resources.getString(R.string.errorEmail)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }
}

