package com.example.led_bag

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.setMargins
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Query

private val MATRIX_MARGINS = 50;

class MainActivity : AppCompatActivity() {
    data class PixelProperties(
        @Query("xPos") val x: Int,
        @Query("yPos") val y: Int,
        @Query("red") val r: Int,
        @Query("green") val g: Int,
        @Query("blue") val b: Int
    )

    private val matrixLayout: LinearLayout by lazy { findViewById(R.id.matrix_layout) }
    private val colorPicker: ColorPickerView by lazy { findViewById(R.id.color_picker) }
    private val brightnessSlide: BrightnessSlideBar by lazy { findViewById(R.id.brightnessSlide) }
    private val fillMatrixButton: Button by lazy { findViewById(R.id.fill_matrix_button) }
    private val animationOpenButton: Button by lazy { findViewById(R.id.animation_open_button) }


    private val cellsColorArray: ArrayList<ArrayList<View>> = arrayListOf()
    private var lastPixel = PixelProperties(0, 0, 0, 0, 0);
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorPicker.attachBrightnessSlider(brightnessSlide)
        fillMatrixButton.setOnClickListener {
            for (i in cellsColorArray) {
                for (j in i) {
                    j.setBackgroundColor(colorPicker.color)
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitInstance.api.fillBack(
                        colorPicker.color.red, colorPicker.color.green, colorPicker.color.blue
                    )
                } catch (e: Exception) {

                }
            }
        }
        animationOpenButton.setOnClickListener {
            startActivity(Intent(this,AnimationActivity::class.java))
        }
        createMatrix()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createMatrix() {
        val displayMetrics: DisplayMetrics = getResources().displayMetrics
        val matrixLinearLayout = LinearLayout(this)
        matrixLinearLayout.orientation = LinearLayout.VERTICAL
        matrixLinearLayout.setOnTouchListener { v, event ->
            for (i in 0 until 16) {
                val view = matrixLinearLayout.getChildAt(i) as LinearLayout
                val outRect = Rect(view.left, view.top, view.right, view.bottom)
                if (outRect.contains(event.x.toInt(), event.y.toInt())) {
                    for (j in 0 until 16) {
                        val childView: View = view.getChildAt(j)
                        val childOutRect =
                            Rect(childView.left, view.top, childView.right, view.bottom)
                        if (childOutRect.contains(event.x.toInt(), event.y.toInt())) {
                            childView.setBackgroundColor(colorPicker.color)
                            val current = PixelProperties(
                                i,
                                j,
                                colorPicker.color.red,
                                colorPicker.color.green,
                                colorPicker.color.blue
                            )
                            if (current != lastPixel) {
                                Log.d("MyTag", "${current.r}")
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        RetrofitInstance.api.setPixel(
                                            current.x,
                                            current.y,
                                            current.r,
                                            current.g,
                                            current.b
                                        )
                                    } catch (e: Exception) {
                                        Log.d("MyTag", "${e.message}")
                                    }
                                }
                            }
                            lastPixel = current
                        }
                    }
                }
            }
            false
        }
        matrixLinearLayout.setOnClickListener {
        }
        val matrixLayoutParams = LinearLayout.LayoutParams(
            displayMetrics.widthPixels - MATRIX_MARGINS * 2,
            displayMetrics.widthPixels - MATRIX_MARGINS * 2
        )
        matrixLayoutParams.setMargins(MATRIX_MARGINS)
        matrixLinearLayout.layoutParams = matrixLayoutParams
        matrixLayout.addView(matrixLinearLayout)
        for (i in 0 until 16) {
            val childLinearLayout = LinearLayout(this)
            childLinearLayout.orientation = LinearLayout.HORIZONTAL
            childLinearLayout.layoutParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
            matrixLinearLayout.addView(childLinearLayout)
            cellsColorArray.add(arrayListOf())
            for (j in 0 until 16) {
                val cellView = View(this)
                cellView.layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
                cellView.setBackgroundColor(Color.BLACK)
                childLinearLayout.addView(cellView)
                cellsColorArray[i].add(cellView)
            }
        }
    }
}