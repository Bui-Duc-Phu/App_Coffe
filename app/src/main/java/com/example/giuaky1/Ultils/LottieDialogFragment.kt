package com.example.giuaky1.Ultils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import com.example.giuaky1.R

class LottieDialogFragment(@NonNull context: Context) : Dialog(context) {

    init {
        val wlmp: WindowManager.LayoutParams = window?.attributes!!

        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window?.attributes = wlmp
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.dialog_custom, null
        )
        setContentView(view)
    }
}
