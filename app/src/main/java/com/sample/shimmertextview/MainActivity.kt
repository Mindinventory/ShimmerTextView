package com.sample.shimmertextview

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.app.shimmertextview.Shimmer
import com.app.shimmertextview.ShimmerTextView


class MainActivity : AppCompatActivity() {

    lateinit var tv1: ShimmerTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_main)

        tv1 = findViewById(R.id.tv1)
        val tv2 = findViewById<ShimmerTextView>(R.id.tv2)
        val tv3 = findViewById<ShimmerTextView>(R.id.tv3)
        val tv4 = findViewById<ShimmerTextView>(R.id.tv4)
        val btnNext = findViewById<AppCompatButton>(R.id.btnNext)
        tv1.setBaseColor(ContextCompat.getColor(this, R.color.dark_red))
            .setHighLightColor(ContextCompat.getColor(this, R.color.orange))
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .build()
        tv1.startShimmer()
        tv2.startShimmer()
        tv3.startShimmer()
        tv4.startShimmer()

        btnNext.setOnClickListener {
            startActivity(Intent(this, OfferActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tv1.stopShimmer()
    }

    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}