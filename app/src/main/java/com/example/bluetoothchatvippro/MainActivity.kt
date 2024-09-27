package com.example.bluetoothchatvippro

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothchatvippro.Adapter.ListAdapter
import com.example.bluetoothchatvippro.Model.Device
import com.example.bluetoothchatvippro.databinding.ActivityMainBinding
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
private lateinit var socket: BluetoothSocket

class MainActivity : AppCompatActivity() {
    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var binding: ActivityMainBinding
    private lateinit var referrer: BluetoothReceiver
    private var listDevice: ArrayList<Device> = arrayListOf()
    private lateinit var listAdapter: ListAdapter
    private var selectedDevice: BluetoothDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listAdapter = ListAdapter(listDevice) { s ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@ListAdapter
            }
            selectedDevice = bluetoothAdapter.bondedDevices.find { it.address == s }
            Toast.makeText(this, "Clicked on device: $s", Toast.LENGTH_SHORT).show()
        }

        binding.LvDevice.layoutManager = LinearLayoutManager(this)
        binding.LvDevice.adapter = listAdapter

        referrer = BluetoothReceiver()

        checkBluetoothPermissions()
        setupButtonClickListener()
        registerBluetoothReceiver()
        AcceptThread().start()
        binding.btnConnect.setOnClickListener {
            if (selectedDevice != null) {
                if (selectedDevice?.bondState == BluetoothDevice.BOND_BONDED) {
                    ConnectThread(selectedDevice!!).start()
                } else {
                    Toast.makeText(this, "Device is not paired", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun checkBluetoothPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 101)
        }
    }

    private fun setupButtonClickListener() {
        binding.btnSearch.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
            } else {
                bluetoothAdapter.disable()
            }

            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            listDevice.clear()
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address
                listDevice.add(Device(deviceName, deviceHardwareAddress))
            }
            listAdapter.notifyDataSetChanged()
        }
    }

    private fun registerBluetoothReceiver() {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(referrer, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(referrer)
    }

    private inner class AcceptThread : Thread() {
        private val serverSocket: BluetoothServerSocket? = bluetoothAdapter.listenUsingRfcommWithServiceRecord("test", SPP_UUID)
        private var cancelled = false

        override fun run() {
            while (!cancelled) {
                try {
                    val socket = serverSocket?.accept()
                    if (socket != null) {
                        Log.d("AcceptThread", "Connecting")
                        ConnectedThread(socket).start()
                    }
                } catch (e: IOException) {
                    if (!cancelled) {
                        Log.e("AcceptThread", "Error accepting connection: ${e.message}")
                    }
                    break
                }
            }
        }

        fun cancel() {
            cancelled = true
            try {
                serverSocket?.close()
            } catch (e: IOException) {
                Log.e("AcceptThread", "Could not close the server socket: ${e.message}")
            }
        }
    }


    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(SPP_UUID)
        }

        override fun run() {
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                try {
                    socket?.connect()
                    Log.d("ConnectThread", "Connecting to socket")

                    Log.d("ConnectThread", "Socket connected")
                    manageMyConnectedSocket(socket)
                } catch (e: IOException) {
                    Log.e("ConnectThread", "Error connecting socket: ${e.message}")
                    try {
                        socket.close()
                    } catch (closeException: IOException) {
                        Log.e("ConnectThread", "Could not close the client socket: ${closeException.message}")
                    }
                }
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("ConnectThread", "Could not close the client socket", e)
            }
        }
    }

    private fun manageMyConnectedSocket(socket: BluetoothSocket) {
        // Implement your logic for managing the connected socket here
        // For example, you can start a new thread to handle the communication
        ConnectedThread(socket).start()
    }


    private inner class ConnectedThread(private val socket: BluetoothSocket) : Thread() {
        override fun run() {
            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    bytes = inputStream.read(buffer)
                    val receivedMessage = String(buffer, 0, bytes)
                    Log.d("Other phone", "New received message: $receivedMessage")
                } catch (e: IOException) {
                    Log.e("Other phone", "Error reading: ${e.message}")
                    break
                }
            }
        }
    }
}
