package com.demo.handkeypointdetection

import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.LinearLayout
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerSetting

class HandKeyPointDetector {

    companion object {
        private const val TAG = "ML_HandKeyPointDetector"
    }

    private lateinit var mSurfaceHolderCamera: SurfaceHolder
    private lateinit var mSurfaceHolderOverlay: SurfaceHolder

    private var mHandKeyPointTransactor = HandKeyPointTransactor()

    /**
     *  Responsible for camera initialization, frame obtaining, and logic control functions
     */
    private lateinit var mLensEngine: LensEngine

    /**
     *  Calls the hand keypoint API to detect MLHandKeypoints.
     */
    private lateinit var mAnalyzer: MLHandKeypointAnalyzer

    private lateinit var mContext: Context
    private lateinit var mRootLayout: ViewGroup


    /**
     *  Initialize by adding surfaceViews.
     */
    fun init(context: Context, rootLayout: ViewGroup) {
        mContext = context
        mRootLayout = rootLayout
        addSurfaceViews()
    }

    /**
     *  Creates and adds a surfaceView for camera preview and another surfaceView for drawing. Adds
     *  SurfaceHolder.Callback to mSurfaceHolderCamera.
     */
    private fun addSurfaceViews() {

        val surfaceViewCamera = SurfaceView(mContext).also {
            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            mSurfaceHolderCamera = it.holder
        }

        val surfaceViewOverlay = SurfaceView(mContext).also {
            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            mSurfaceHolderOverlay = it.holder
            mSurfaceHolderOverlay.setFormat(PixelFormat.TRANSPARENT)
            mHandKeyPointTransactor.setOverlay(mSurfaceHolderOverlay)
        }

        mRootLayout.addView(surfaceViewCamera)
        mRootLayout.addView(surfaceViewOverlay)

        mSurfaceHolderCamera.addCallback(surfaceHolderCallback)
    }

    /**
     *  Starts LensEngine which is responsible for handling camera events after
     *  mSurfaceHolderCamera gets ready.
     */
    private val surfaceHolderCallback = object : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            createAnalyzer()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            prepareLensEngine(width, height)
            mLensEngine.run(holder)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            mLensEngine.release()
        }

    }

    /**
     *  Creates MLKeyPointAnalyzer with MLHandKeypointAnalyzerSetting.
     *
     *  Scene type can be TYPE_KEYPOINT_ONLY and TYPE_RECT_ONLY, use TYPE_ALL for both.
     *
     *  Max hand results can be up to MLHandKeypointAnalyzerSetting.MAX_HANDS_NUM which
     *  is 10 currently.
     */
    private fun createAnalyzer() {
        val settings = MLHandKeypointAnalyzerSetting.Factory()
            .setSceneType(MLHandKeypointAnalyzerSetting.TYPE_ALL)
            .setMaxHandResults(2)
            .create()

        mAnalyzer = MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer(settings)
        mAnalyzer.setTransactor(mHandKeyPointTransactor)
    }

    /**
     *  Creates a LensEngine that uses back camera.
     */
    private fun prepareLensEngine(width: Int, height: Int) {

        val dimen1: Int
        val dimen2: Int

        if (mContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dimen1 = width
            dimen2 = height
        } else {
            dimen1 = height
            dimen2 = width
        }

        mLensEngine = LensEngine.Creator(mContext, mAnalyzer)
            .setLensType(LensEngine.BACK_LENS)
            .applyDisplayDimension(dimen1, dimen2)
            .applyFps(5F)
            .enableAutomaticFocus(true)
            .create()
    }

    fun stopAnalyzer() {
        mAnalyzer.stop()
    }
}