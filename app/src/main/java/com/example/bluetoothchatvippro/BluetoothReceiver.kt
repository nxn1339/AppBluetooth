package com.example.bluetoothchatvippro

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    Toast.makeText(context, "Bluetooth is off", Toast.LENGTH_SHORT).show()
                }
                BluetoothAdapter.STATE_ON -> {
                    Toast.makeText(context, "Bluetooth is on", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
