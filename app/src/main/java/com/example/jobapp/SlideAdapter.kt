package com.example.jobapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

class SliderAdapter(var context: Context) : PagerAdapter() {
    var slide_headings = arrayOf(
        "Candidate Handling", "Interview Scheduling"
    )
    var slide_descs = arrayOf(
        "Simple and smart way to handle all the candidate through this application",
        "Easy to make a schedule of particular candidate using simple form"
    )
    var layoutInflater: LayoutInflater? = null
    override fun getCount(): Int {
        return slide_headings.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slide_layout, container, false)
        val slideHeading = view.findViewById<View>(R.id.heading_id) as TextView
        val slideDesc = view.findViewById<View>(R.id.desc_id) as TextView
        slideHeading.text = slide_headings[position]
        slideDesc.text = slide_descs[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}