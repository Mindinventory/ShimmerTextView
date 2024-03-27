package com.sample.shimmertextview

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.app.shimmertextview.ShimmerTextView

class OfferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_offer)

        val tvOffer1 = findViewById<ShimmerTextView>(R.id.tvOffer1)
        val tvOffer2 = findViewById<ShimmerTextView>(R.id.tvOffer2)
        val tvOffer3 = findViewById<ShimmerTextView>(R.id.tvOffer3)
        val tvOffer5 = findViewById<ShimmerTextView>(R.id.tvOffer5)
        val tvOffer6 = findViewById<ShimmerTextView>(R.id.tvOffer6)
        val tvOffer7 = findViewById<ShimmerTextView>(R.id.tvOffer7)
        tvOffer1.startShimmer()
        tvOffer2.startShimmer()
        tvOffer3.startShimmer()
        tvOffer5.startShimmer()
        tvOffer6.startShimmer()
        tvOffer7.startShimmer()
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