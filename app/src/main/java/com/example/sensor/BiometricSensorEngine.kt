package com.example.sensor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

data class HardwareTelemetry(
    val somaticStabilityPct: Int = 94,
    val motionMagnitudeG: Float = 1.00f,
    val gyroDriftRad: Float = 0.02f,
    val sensorActive: Boolean = false,
    val bleScanState: String = "IDLE // SY4-BLE READY",
    val discoveredBleDevices: List<String> = emptyList()
)

class BiometricSensorEngine(
    private val context: Context,
    private val onUpdate: (HardwareTelemetry) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var currentTelemetry = HardwareTelemetry()
    private val discoveredDevices = mutableSetOf<String>()
    private var isBleScanning = false

    fun startSensors() {
        var registered = false
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            registered = true
        }
        gyroscope?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            registered = true
        }
        currentTelemetry = currentTelemetry.copy(sensorActive = registered)
        onUpdate(currentTelemetry)
    }

    fun stopSensors() {
        sensorManager?.unregisterListener(this)
        stopBleScan()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magG = sqrt(x * x + y * y + z * z) / 9.81f
                val deltaFromOneG = kotlin.math.abs(magG - 1.0f)
                val stability = ((1.0f - (deltaFromOneG * 1.6f).coerceIn(0f, 0.95f)) * 100f).toInt()
                currentTelemetry = currentTelemetry.copy(
                    somaticStabilityPct = stability,
                    motionMagnitudeG = magG,
                    sensorActive = true
                )
                onUpdate(currentTelemetry)
            }
            Sensor.TYPE_GYROSCOPE -> {
                val rx = event.values[0]
                val ry = event.values[1]
                val rz = event.values[2]
                val omega = sqrt(rx * rx + ry * ry + rz * rz)
                currentTelemetry = currentTelemetry.copy(
                    gyroDriftRad = omega
                )
                onUpdate(currentTelemetry)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    fun hasBlePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private val bleScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val device = result?.device ?: return
            val name = device.name ?: "BLE-Node-${device.address.takeLast(5)}"
            val rssi = result.rssi
            val entry = "$name ($rssi dBm)"
            if (discoveredDevices.add(entry)) {
                currentTelemetry = currentTelemetry.copy(
                    bleScanState = "LOCKED: ${discoveredDevices.size} NEURO-NODES",
                    discoveredBleDevices = discoveredDevices.toList()
                )
                onUpdate(currentTelemetry)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            currentTelemetry = currentTelemetry.copy(
                bleScanState = "SIM-BRIDGE ACTIVE (BLE ERR $errorCode)"
            )
            onUpdate(currentTelemetry)
        }
    }

    @SuppressLint("MissingPermission")
    fun toggleBleNeuroScan() {
        if (!hasBlePermissions()) {
            currentTelemetry = currentTelemetry.copy(
                bleScanState = "PERMISSION REQUIRED FOR BLE SCAN"
            )
            onUpdate(currentTelemetry)
            return
        }
        val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = btManager?.adapter
        val scanner = adapter?.bluetoothLeScanner
        if (adapter == null || !adapter.isEnabled || scanner == null) {
            // Provide deterministic SY4-EEG virtual BLE bridge fallback when Bluetooth adapter is off in emulator
            val virtualNodes = listOf(
                "SY4-EEG-MuseS-04A (-52 dBm)",
                "AXIOM-HexBand-v2 (-61 dBm)"
            )
            currentTelemetry = currentTelemetry.copy(
                bleScanState = "VIRTUAL BLE BRIDGE // 2 NODES SYNCED",
                discoveredBleDevices = virtualNodes
            )
            onUpdate(currentTelemetry)
            return
        }

        if (isBleScanning) {
            stopBleScan()
        } else {
            discoveredDevices.clear()
            isBleScanning = true
            currentTelemetry = currentTelemetry.copy(
                bleScanState = "SCANNING BLE EEG CHANNELS..."
            )
            onUpdate(currentTelemetry)
            try {
                scanner.startScan(bleScanCallback)
            } catch (e: Exception) {
                isBleScanning = false
                currentTelemetry = currentTelemetry.copy(
                    bleScanState = "VIRTUAL BLE BRIDGE ACTIVE"
                )
                onUpdate(currentTelemetry)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopBleScan() {
        if (!isBleScanning) return
        isBleScanning = false
        try {
            val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            btManager?.adapter?.bluetoothLeScanner?.stopScan(bleScanCallback)
        } catch (_: Exception) {
        }
        currentTelemetry = currentTelemetry.copy(
            bleScanState = "BLE STANDBY (${discoveredDevices.size} NODES)"
        )
        onUpdate(currentTelemetry)
    }
}
