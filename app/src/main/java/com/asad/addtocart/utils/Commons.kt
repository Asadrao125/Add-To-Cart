package com.asad.addtocart.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.asad.addtocart.R
import com.squareup.picasso.Picasso

class Commons {
    companion object {
        fun LoadImage(url: String, view: ImageView) {
            Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).into(view)
        }

        fun Toast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

    }
}