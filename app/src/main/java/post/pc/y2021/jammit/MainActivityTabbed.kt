package post.pc.y2021.jammit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.demo_viewpager2.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivityTabbed : AppCompatActivity() {

    companion object{
        const val FEED = 0
        const val LIST = 1
        const val PROFILE = 2
    }
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabbed)


        /*
        Set Tabs
        */
        val tabLayout:TabLayout = findViewById(R.id.tabLayout);

        //set viewPager2 and its adapter
        viewPager = findViewById(R.id.view_pager)


        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        // set text to tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position){
                FEED -> "Feed"
                LIST-> "Find"
                else -> "Profile"
            }
        }.attach()

        // Set icons to tabs
        tabLayout.getTabAt(FEED)?.setIcon(R.drawable.ic_baseline_article_24)
        tabLayout.getTabAt(LIST)?.setIcon(R.drawable.ic_baseline_explore_24)
        tabLayout.getTabAt(PROFILE)?.setIcon(R.drawable.ic_baseline_account_circle_24)
    }
}