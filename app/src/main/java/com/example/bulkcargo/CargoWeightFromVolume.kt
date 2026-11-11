package com.example.bulkcargo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

/**
 * Weight = Volume / Stowage Factor
 *
 * Given a hold volume (or available volume) and the SF of the cargo,
 * calculate how much weight can be loaded.
 */
class CargoWeightFromVolume : AppCompatActivity() {

    private lateinit var volumeInput: EditText
    private lateinit var sfInput: EditText
    private lateinit var volumeUnitSpinner: Spinner
    private lateinit var sfUnitSpinner: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_volume)

        volumeInput = findViewById(R.id.inputVolume)
        sfInput = findViewById(R.id.inputSF)
        volumeUnitSpinner = findViewById(R.id.volumeUnitSpinner)
        sfUnitSpinner = findViewById(R.id.sfUnitSpinner)
        btnCalculate = findViewById(R.id.btnCalculate)
        resultText = findViewById(R.id.resultText)

        val volumeUnits = arrayOf("m³", "ft³")
        val sfUnits = arrayOf("m³/tonne", "ft³/long ton", "ft³/short ton")

        volumeUnitSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, volumeUnits)
        sfUnitSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sfUnits)

        volumeUnitSpinner.setSelection(0)
        sfUnitSpinner.setSelection(0)

        btnCalculate.setOnClickListener {
            calculateWeight()
        }
    }

    private fun calculateWeight() {
        try {
            val volumeStr = volumeInput.text.toString().trim()
            val sfStr = sfInput.text.toString().trim()

            if (volumeStr.isEmpty() || sfStr.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return
            }

            val volume = volumeStr.toDouble()
            val sf = sfStr.toDouble()

            if (sf <= 0) {
                Toast.makeText(this, "SF must be > 0", Toast.LENGTH_SHORT).show()
                return
            }

            val volUnitIdx = volumeUnitSpinner.selectedItemPosition
            val sfUnitIdx = sfUnitSpinner.selectedItemPosition

            // Normalize volume to m³
            val volumeM3 = if (volUnitIdx == 0) volume else volume / 35.3147

            // Normalize SF to m³/tonne
            val sfM3T = when (sfUnitIdx) {
                0 -> sf
                1 -> sf / 34.767
                2 -> sf / 38.928
                else -> sf
            }

            // Weight in metric tonnes
            val weightTonnes = volumeM3 / sfM3T

            // Also calculate in long tons and short tons
            val weightLT = weightTonnes * 1.016046
            val weightST = weightTonnes * 0.907185

            val df = DecimalFormat("#,###.##")

            resultText.text = """
                ┌─────────────────────────────────────────┐
                │  WEIGHT FROM VOLUME CALCULATION          │
                ├─────────────────────────────────────────┤
                │ Volume Available: ${df.format(volumeM3)} m³
                │ Stowage Factor:   ${df.format(sfM3T)} m³/tonne
                │
                │ ─── RESULTS ───
                │ Weight: ${df.format(weightTonnes)} metric tonnes
                │ Weight: ${df.format(weightLT)} long tons (UK)
                │ Weight: ${df.format(weightST)} short tons (US)
                │
                │ ─── INVERSE CHECK ───
                │ Volume needed: ${df.format(weightTonnes * sfM3T)} m³
                │ Match: ${if (Math.abs(weightTonnes * sfM3T - volumeM3) < 0.01) "✓ OK" else "✗ Mismatch"}
                └─────────────────────────────────────────┘
            """.trimIndent()

        } catch (e: Exception) {
            Toast.makeText(this, "Invalid input: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
