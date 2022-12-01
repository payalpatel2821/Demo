package com.unoapp.demo


import android.content.Context
import android.graphics.Typeface
import android.net.TrafficStats
import androidx.multidex.MultiDexApplication


class App : MultiDexApplication(){

    companion object {

        @JvmField
        var appInstance: App? = null
//        lateinit var fastSave: FastSave

        @JvmStatic
        fun getAppInstance(): App {
            return appInstance as App
        }


        fun setFont(context: Context, str: String?): Typeface? {
            return Typeface.createFromAsset(context.assets, str)
        }


    }

    val TAG = App::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        TrafficStats.setThreadStatsTag(1000)


        //FastSave pref lib initialization
//        FastSave.init(this)
//        fastSave = FastSave.getInstance()


    }



    fun getAppContext(): Context? {
        return this
    }






}