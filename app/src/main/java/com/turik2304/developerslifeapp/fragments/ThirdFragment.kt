package com.turik2304.developerslifeapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.turik2304.developerslifeapp.R


class ThirdFragment : MyFragment() { // hot
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        initializeGlobalConstants(view)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 40f
        circularProgressDrawable.setColorSchemeColors(Color.GRAY)
        circularProgressDrawable.start()
        val buttonBack: ImageButton = view.findViewById(R.id.button_back3) as ImageButton
        buttonBack.setOnClickListener { back() }
        val buttonNext: ImageButton = view.findViewById(R.id.button_next3) as ImageButton
        buttonNext.setOnClickListener { nextGif() }
        currGif()
        return view
    }

    private fun initializeGlobalConstants(view: View) {
        category = "hot"
        mImageView = view.findViewById(R.id.gifImageView3)
        mTextView = view.findViewById(R.id.caption3)
        circularProgressDrawable = CircularProgressDrawable(mImageView.context)
    }
}