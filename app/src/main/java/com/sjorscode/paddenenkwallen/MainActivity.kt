package com.sjorscode.paddenenkwallen

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val REQUEST_ENABLE_BT = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun masterButtonClicked(view: View) {
        Toast.makeText(applicationContext,"Master",Toast.LENGTH_SHORT).show()
        //Tip: Enabling discoverability automatically enables Bluetooth.
        // If you plan to consistently enable device discoverability before performing
        // Bluetooth activity, you can skip step 2 above. For more information, read the
        // enabling discoverability, section on this page.
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.w(TAG, "masterButtonClicked: No bluetooth available")
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    fun slaveButtonClicked(view: View) {
        Toast.makeText(applicationContext,"Slave",Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                // User did not enable Bluetooth.
                Toast.makeText(applicationContext, "Dan niet joh", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
