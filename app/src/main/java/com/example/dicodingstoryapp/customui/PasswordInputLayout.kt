package com.example.dicodingstoryapp.customui

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.dicodingstoryapp.R
import com.google.android.material.textfield.TextInputLayout

class PasswordInputLayout : TextInputLayout {

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

                thisClass.error = if (s.toString().length >= 8) {
                    null
                } else {
                    resources.getString(R.string.errorPassword)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }
}