package com.demo.handkeypointdetection

import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoint
import kotlin.math.abs
import kotlin.math.atan2

class Hand {

    companion object {
        private const val ACCEPTED_RANGE_IN_DEGREE = 30
    }

    var firstFinger = arrayListOf<MLHandKeypoint>()
    var middleFinger = arrayListOf<MLHandKeypoint>()
    var ringFinger = arrayListOf<MLHandKeypoint>()
    var littleFinger = arrayListOf<MLHandKeypoint>()
    var thumb = arrayListOf<MLHandKeypoint>()
    var wrist: MLHandKeypoint? = null

    /**
     *  Creates and returns a hand with first finger, middle finger, ring finger, little finger,
     *  thumb and wrist.
     */
    fun createHand(keyPoints: MutableList<MLHandKeypoint>): Hand {

        for (keyPoint in keyPoints) {

            when (keyPoint.type) {
                MLHandKeypoint.TYPE_FOREFINGER_FIRST -> firstFinger.add(keyPoint)
                MLHandKeypoint.TYPE_FOREFINGER_SECOND -> firstFinger.add(keyPoint)
                MLHandKeypoint.TYPE_FOREFINGER_THIRD -> firstFinger.add(keyPoint)
                MLHandKeypoint.TYPE_FOREFINGER_FOURTH -> firstFinger.add(keyPoint)

                MLHandKeypoint.TYPE_MIDDLE_FINGER_FIRST -> middleFinger.add(keyPoint)
                MLHandKeypoint.TYPE_MIDDLE_FINGER_SECOND -> middleFinger.add(keyPoint)
                MLHandKeypoint.TYPE_MIDDLE_FINGER_THIRD -> middleFinger.add(keyPoint)
                MLHandKeypoint.TYPE_MIDDLE_FINGER_FOURTH -> middleFinger.add(keyPoint)

                MLHandKeypoint.TYPE_RING_FINGER_FIRST -> ringFinger.add(keyPoint)
                MLHandKeypoint.TYPE_RING_FINGER_SECOND -> ringFinger.add(keyPoint)
                MLHandKeypoint.TYPE_RING_FINGER_THIRD -> ringFinger.add(keyPoint)
                MLHandKeypoint.TYPE_RING_FINGER_FOURTH -> ringFinger.add(keyPoint)

                MLHandKeypoint.TYPE_LITTLE_FINGER_FIRST -> littleFinger.add(keyPoint)
                MLHandKeypoint.TYPE_LITTLE_FINGER_SECOND -> littleFinger.add(keyPoint)
                MLHandKeypoint.TYPE_LITTLE_FINGER_THIRD -> littleFinger.add(keyPoint)
                MLHandKeypoint.TYPE_LITTLE_FINGER_FOURTH -> littleFinger.add(keyPoint)

                MLHandKeypoint.TYPE_THUMB_FIRST -> thumb.add(keyPoint)
                MLHandKeypoint.TYPE_THUMB_SECOND -> thumb.add(keyPoint)
                MLHandKeypoint.TYPE_THUMB_THIRD -> thumb.add(keyPoint)
                MLHandKeypoint.TYPE_THUMB_FOURTH -> thumb.add(keyPoint)

                MLHandKeypoint.TYPE_WRIST -> wrist = keyPoint
            }
        }

        return this
    }

    /**
     *  Detects the number shown by hand. Counts fingers that are up.
     */
    fun getNumber(): Int {
        var number = 0

        if (wrist != null) {

            if (isFingerUp(firstFinger, wrist!!))
                number++

            if (isFingerUp(middleFinger, wrist!!))
                number++

            if (isFingerUp(ringFinger, wrist!!))
                number++

            if (isFingerUp(littleFinger, wrist!!))
                number++

            if (isFingerUp(thumb, wrist!!))
                number++
        }

        return number
    }

    /**
     *  A finger is considered up if the difference between, degree calculated between
     *  first and last points of the finger and degree between first and second points of the finger
     *  is in accepted range.
     */
    private fun isFingerUp(points: ArrayList<MLHandKeypoint>, wrist: MLHandKeypoint): Boolean {

        if (points.size == 4) {

            val degreeFirst2Last = getDegree(points.first(), points.last())
            val degreeFirst2Second = getDegree(points.first(), points[1])

            if (abs(degreeFirst2Last - degreeFirst2Second) < ACCEPTED_RANGE_IN_DEGREE)
                return true
        }

        return false
    }

    /**
     *  Finds the degree between to points.
     */
    private fun getDegree(point1: MLHandKeypoint, point2: MLHandKeypoint): Double {

        val width = point2.pointX - point1.pointX
        val height = point2.pointY - point1.pointY

        //Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta)
        var theta = atan2(height.toDouble(), width.toDouble())

        //Rotate theta angle PI/2 radian clockwise.
        theta += Math.PI/2.0

        //Convert radian to degree.
        var angle = Math.toDegrees(theta)

        if (angle < 0) {
            angle += 360
        }
        return angle
    }

}
