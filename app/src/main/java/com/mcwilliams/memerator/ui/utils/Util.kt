package com.mcwilliams.memerator.ui.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.muddzdev.quickshot.QuickShot
import java.time.LocalDate
import java.time.LocalTime

class HandleSavedImage(val context: Context, val fileName: String) :
    QuickShot.QuickShotListener {
    override fun onQuickShotSuccess(path: String?) {
        Toast.makeText(context, "Meme Saved", Toast.LENGTH_SHORT).show()
    }

    override fun onQuickShotFailed(path: String?) {
        Toast.makeText(context, "Error Saving", Toast.LENGTH_SHORT).show()
    }
}

fun String.buildMemeImageUrl(): String {
    val imageName = this.replace(" ", "-")
    Log.d("TAG", "buildMemeImageUrl: $imageName")
    return "https://apimeme.com/meme?meme=$imageName&top=&bottom="
}

fun share(view: ComposeView, name: String, context: Context) {
    view.toBitmap(
        onBitmapReady = { bitmap ->
            Log.d("TAG", "share: bitmap ready")
            val fileName =
                "$name-${LocalDate.now()}-${LocalTime.now().hour}-${LocalTime.now().minute}"
            QuickShot.of(bitmap, view.context)
                .setResultListener(HandleSavedImage(context, fileName))
                .setFilename(fileName)
                .setPath("Memerator")
                .toJPG()
                .save();
        },
        onBitmapError = {
            Log.d("TAG", it.localizedMessage)
        }
    )
}

// start of extension.
fun View.toBitmap(onBitmapReady: (Bitmap) -> Unit, onBitmapError: (Exception) -> Unit) {

    try {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val temporalBitmap =
                Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

            // Above Android O, use PixelCopy due
            // https://stackoverflow.com/questions/58314397/
            val window: Window = (this.context as Activity).window

            val location = IntArray(2)

            this.getLocationInWindow(location)

            val viewRectangle =
                Rect(location[0], location[1], location[0] + this.width, location[1] + this.height)

            val onPixelCopyListener: PixelCopy.OnPixelCopyFinishedListener =
                PixelCopy.OnPixelCopyFinishedListener { copyResult ->

                    if (copyResult == PixelCopy.SUCCESS) {

                        onBitmapReady(temporalBitmap)
                    } else {

                        error("Error while copying pixels, copy result: $copyResult")
                    }
                }

            PixelCopy.request(
                window, viewRectangle, temporalBitmap, onPixelCopyListener, Handler(
                    Looper.getMainLooper()
                )
            )
        } else {

            val temporalBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.RGB_565)

            val canvas = android.graphics.Canvas(temporalBitmap)

            this.draw(canvas)

            canvas.setBitmap(null)

            onBitmapReady(temporalBitmap)
        }

    } catch (exception: Exception) {

        onBitmapError(exception)
    }
}

class ColorState(
    val color: Color,
    val updateColor: (Color) -> Unit
)

@Composable
fun rememberColorState(
    color: Color = Color.Black, updateColor: (Color) -> Unit = {}
) = ColorState(
    color = color,
    updateColor = updateColor
)