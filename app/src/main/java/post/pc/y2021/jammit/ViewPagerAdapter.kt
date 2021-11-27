package com.example.demo_viewpager2

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import post.pc.y2021.jammit.FeedFragment
import post.pc.y2021.jammit.ListFragment
import post.pc.y2021.jammit.MainActivityTabbed
import post.pc.y2021.jammit.ProfileFragment

/**
 * Adapter for the viewPager2.
 * Responsible for creating the fragments in the tabs.
 */
class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    @NonNull
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            MainActivityTabbed.FEED -> FeedFragment.newInstance()
            MainActivityTabbed.LIST -> ListFragment.newInstance()
            else -> ProfileFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}