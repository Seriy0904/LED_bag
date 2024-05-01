package com.example.led_bag

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.slider.Slider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnimationActivity : AppCompatActivity() {
    private val animationDropdown: Spinner by lazy { findViewById(R.id.animation_dropdown) }
    private val picsDropdown: Spinner by lazy { findViewById(R.id.pics_dropdown) }
    private val sendTextField: EditText by lazy { findViewById(R.id.send_text_field) }
    private val sendTextButton: Button by lazy { findViewById(R.id.send_text_button) }
    private val animationSpeedSlider: Slider by lazy { findViewById(R.id.animation_speed_slider) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        picsDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitInstance.api.showPixels()
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        ArrayAdapter.createFromResource(this, R.array.animation_array, R.layout.dropdownmenu_item)
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                animationDropdown.adapter = it
            }
        animationDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitInstance.api.showAnimation(position - 1,)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        animationSpeedSlider.addOnSliderTouchListener(object :Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(p0: Slider) {
            }

            override fun onStopTrackingTouch(p0: Slider) {
                Log.d("MyTag", "Pos: ${(animationSpeedSlider.value*100).toInt()}")
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitInstance.api.setAnimationSpeed((animationSpeedSlider.value*100).toInt())
                    } catch (e: Exception) {
                    }
                }
            }
        })
        sendTextButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitInstance.api.printText(sendTextField.text.toString())
                } catch (e: Exception) {
                }
            }
        }
    }
}