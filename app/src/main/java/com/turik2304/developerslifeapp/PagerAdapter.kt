package com.turik2304.developerslifeapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.turik2304.developerslifeapp.fragments.FirstFragment
import com.turik2304.developerslifeapp.fragments.SecondFragment
import com.turik2304.developerslifeapp.fragments.ThirdFragment

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FirstFragment()
            }
            1 -> SecondFragment()
            else -> {
                return ThirdFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Последние"
            1 -> "Лучшие"
            else -> {
                return "Горячие"
            }
        }
    }

}