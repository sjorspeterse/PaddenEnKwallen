package com.sjorscode.paddenenkwallen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun masterButtonClicked(view: View) {
        Toast.makeText(applicationContext,"Master",Toast.LENGTH_SHORT).show()
    }

    fun slaveButtonClicked(view: View) {
        Toast.makeText(applicationContext,"Slave",Toast.LENGTH_SHORT).show()
    }
}
