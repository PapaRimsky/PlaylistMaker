package com.paparimsky.playlistmaker

import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

object ImageViewExtensions {

    fun ImageView.loadImageGlide(
        link: String,
        layout_radius: Float? = null,
        image: Int
    ) {
        Glide.with(context)
            .load(link)
            .placeholder(image)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        layout_radius ?: 0f,
                        context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(this)

    }
}