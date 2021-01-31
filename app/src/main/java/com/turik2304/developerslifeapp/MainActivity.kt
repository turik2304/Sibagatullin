package com.turik2304.developerslifeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentAdapter = PagerAdapter(supportFragmentManager)
        viewpager.adapter = fragmentAdapter

        tabs.setupWithViewPager(viewpager)

    }

}