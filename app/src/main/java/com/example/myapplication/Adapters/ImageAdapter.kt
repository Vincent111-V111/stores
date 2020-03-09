//package com.example.myapplication.Adapters
//
//import android.content.Context
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.viewpager.widget.PagerAdapter
//import okhttp3.MultipartBody
//
//class ImageAdapter (private val mContext : Context) : PagerAdapter(){
//    companion object{
//        val images: ArrayList<MultipartBody.Part> = ArrayList()
//    }
//
//    override fun isViewFromObject(view: View, any : Any): Boolean {
//        return view == any
//    }
//
//    override fun getCount(): Int {
//        return images.size
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//
//        val mImageView = ImageView(mContext)
//        mImageView.scaleType = ImageView.ScaleType.CENTER_CROP
//        mImageView.setImageResource(images.size[position])
//        container.addView(mImageView,0)
//
//        return mImageView
//    }
//    override fun destroyItem(container: ViewGroup, position: Int, any: Any){
//        container.removeView(any as View?)
//    }
//}
//
//private operator fun Int.get(position: Int): Int {
//    return position
//}
//
//
