package com.dicoding.mysharestory

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

class MyEditText : AppCompatEditText {

    private lateinit var startIcon: Drawable
    private lateinit var warningIcon: Drawable
    private lateinit var passIcon: Drawable
    private var label: String = ""

    var isNotEmpty: Boolean = false
    var isEmailValid: Boolean = false
    var isPasswordValid: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        iLabel()
        iIconWarning()
        iIconPass()
        iIconStart()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s?.isNotEmpty() as Boolean) {
                    startIcon.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.light_pink
                        )
                    )
                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    isNotEmpty = false
                    startIcon.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.purple_701
                        )
                    )
                    warningIcon()
                } else {
                    isNotEmpty = true
                    startIcon.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.light_pink
                        )
                    )

                    if (
                        this@MyEditText.inputType != PASSWORD &&
                        this@MyEditText.inputType != EMAIL
                    ) {
                        if (this@MyEditText.isEnabled) passIcon()
                    }
                }

                if (this@MyEditText.inputType == PASSWORD) {
                    if (s?.length ?: 0 < 6) {
                        isPasswordValid = false
                        warningIcon()
                    } else {
                        isPasswordValid = true
                        passIcon()
                    }
                } else if (this@MyEditText.inputType == EMAIL) {
                    Log.d("TAG", "onTextChanged: ${validateEmail(s.toString())}")
                    if (validateEmail(s.toString())) {
                        passIcon()
                    } else warningIcon()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun iIconWarning() {
        warningIcon = ContextCompat.getDrawable(
            context,
            R.drawable.ic_warning_password
        ) as Drawable
    }

    private fun iIconPass() {
        passIcon = ContextCompat.getDrawable(
            context,
            R.drawable.ic_check_password
        ) as Drawable
    }

    private fun iLabel() {
        label = when (this.inputType) {
            PASSWORD -> "Password"
            EMAIL -> "Email"
            PERSON_NAME -> "Full Name"
            else -> "Description"
        }
    }

    private fun iIconStart() {
        startIcon = when (this.inputType) {
            PASSWORD -> {
                ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable
            }
            EMAIL -> {
                ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
            }
            PERSON_NAME -> {
                ContextCompat.getDrawable(context, R.drawable.ic_profile) as Drawable
            }
            else -> {
                ContextCompat.getDrawable(context, R.drawable.ic_description) as Drawable
            }
        }.apply {
            setTint(
                ContextCompat.getColor(context, R.color.purple_701)
            )
        }

        setDrawable(startIcon)
    }

    fun invalidEmail() {
        setDrawable(startOfTheText = startIcon, endOfTheText = warningIcon)
        Toast.makeText(context, context.getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
    }

    fun duplicateEmail() {
        setDrawable(startOfTheText = startIcon, endOfTheText = warningIcon)
        Toast.makeText(context, context.getString(R.string.duplicate_email), Toast.LENGTH_SHORT).show()
    }

    fun invalidPassword() {
        setDrawable(startOfTheText = startIcon, endOfTheText = warningIcon)
        Toast.makeText(context, context.getString(R.string.invalid_password), Toast.LENGTH_SHORT).show()
    }

    fun emptyForm() {
        setDrawable(startOfTheText = startIcon, endOfTheText = warningIcon)
        Toast.makeText(
            context,
            context.getString(R.string.form_could_not_be_empty, label),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun validateEmail(text: String): Boolean {
        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
        return isEmailValid
    }

    private fun warningIcon() {
        setDrawable(startOfTheText = startIcon, endOfTheText = warningIcon)
    }

    private fun passIcon() {
        setDrawable(startOfTheText = startIcon, endOfTheText = passIcon)
    }

    private fun setDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )

        background = ContextCompat.getDrawable(context, R.drawable.bg_radius_18dp)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        compoundDrawablePadding = 48
        setTextColor(ContextCompat.getColor(context, R.color.light_pink))
        setHintTextColor(ContextCompat.getColor(context, R.color.purple_701))
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    companion object {
        const val PASSWORD = 129
        const val EMAIL = 33
        const val PERSON_NAME = 97
    }
}