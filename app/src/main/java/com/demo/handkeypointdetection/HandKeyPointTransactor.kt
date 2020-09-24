package com.demo.handkeypointdetection

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import androidx.core.util.keyIterator
import androidx.core.util.valueIterator
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoint
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints

class HandKeyPointTransactor(surfaceHolder: SurfaceHolder? = null): MLAnalyzer.MLTransactor<MLHandKeypoints> {

    companion object {
        private const val TAG = "ML_HandKeyPntTransactor"
    }

    /**
     *  SurfaceHolder above camera preview's surfaceHolder. Used to draw paintings on.
     */
    private var mOverlay = surfaceHolder

    fun setOverlay(surfaceHolder: SurfaceHolder) {
        mOverlay = surfaceHolder
    }

    override fun transactResult(result: MLAnalyzer.Result<MLHandKeypoints>?) {

        if (result == null)
            return

        val canvas = mOverlay?.lockCanvas() ?: return

        canvas.drawColor(0, PorterDuff.Mode.CLEAR)


        val numberString = analyzeHandsAndGetNumber(result)

        //Find the middle of the canvas
        val centerX = canvas.width / 2F
        val centerY = canvas.height / 2F

        canvas.drawText(numberString, centerX, centerY, Paint().also {
            it.style = Paint.Style.FILL
            it.textSize = 100F
            it.color = Color.GREEN
        })

        mOverlay?.unlockCanvasAndPost(canvas)
    }

    /**
     *  Creates hands and checks if fingers are up or not. Counts fingers that are up and returns as
     *  String.
     */
    private fun analyzeHandsAndGetNumber(result: MLAnalyzer.Result<MLHandKeypoints>): String {
        val hands = ArrayList<Hand>()
        var number = 0

        for (key in result.analyseList.keyIterator()) {
            hands.add(Hand())

            for (value in result.analyseList.valueIterator()) {
                number += hands.last().createHand(value.handKeypoints).getNumber()
            }
        }

        return number.toString()
    }

    override fun destroy() {
        Log.d(TAG, "destroy()")
    }
}