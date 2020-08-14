package com.supcoder.example

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.supcoder.curtain.CurtainView
import com.supcoder.curtain.bridge.OnProgressChangeListener
import com.supcoder.curtain.config.CurtainType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        seekBar1.progress = 100
        seekBar.progress = 0

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val seekBarProgress = 100 - p1
                if (seekBar1.progress != seekBarProgress) {
                    seekBar1.progress = seekBarProgress
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                curtainView.setProgress(p0.progress)
            }
        })
        seekBar1.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val seekBarProgress = 100 - p1
                if (seekBar.progress != seekBarProgress) {
                    seekBar.progress = seekBarProgress
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                val curProgress = 100 - p0.progress
                curtainView.setProgress(curProgress)
            }
        })
    }


}