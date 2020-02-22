package com.sjorscode.paddenenkwallen

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val REQUEST_ENABLE_BT = 1
    private val paddenBtn by lazy {
        findViewById<Button>(R.id.pad_btn)
    }
    private val broadcastReceiver by lazy {
        val br = BluetoothNameReceiver()
        br.setMainActivity(this)
        br
    }
    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val toastObserver = Observer<String> { text ->
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.toastNotifier.observe(this, toastObserver)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(ACTION_LOCAL_NAME_CHANGED)
        }
        registerReceiver(broadcastReceiver, filter)
    }

    fun enableBluetooth() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.w(TAG, "masterButtonClicked: No bluetooth available")
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    fun paddenButtonClicked(view: View) {
        enableBluetooth()
        padSelected()
    }

    fun kwallenButtonClicked(view: View) {
        if (paddenBtn.isPressed) {
            dungeonMasterSelected()
        } else {
            enableBluetooth()
            kwalSelected()
        }
    }

    private fun padSelected() {
        viewModel.connectToMaster()
    }



    private fun kwalSelected() {
        viewModel.toast("Toast!")
    }

    private fun dungeonMasterSelected() {
        //Tip: Enabling discoverability automatically enables Bluetooth.
        // If you plan to consistently enable device discoverability before performing
        // Bluetooth activity, you can skip step 2 above. For more information, read the
        // enabling discoverability, section on this page.

        val adapter = BluetoothAdapter.getDefaultAdapter()
        adapter.name = "Dungeon Master"

//        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
//            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//        }
//        startActivity(discoverableIntent)
//        val acceptThread = AcceptThread()
//        thread(start = true) {
//            Log.i(TAG, "starting thread...")
//            acceptThread.run()
//        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Dan niet joh", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Braaf.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
