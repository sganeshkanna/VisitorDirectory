package com.mytown.sd

import android.content.Intent
import android.os.Bundle
import android.os.Handler

class LauncherActivity : VisitorActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            startActivity(Intent(this@LauncherActivity, HomeActivity::class.java))
            finish()
        },500)
    }
}