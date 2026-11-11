package com.example.bulkcargo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

/**
 * Volume = Weight × Stowage Factor
 *
 * Given a cargo weight and its SF, calculate the required volume.
 */
class CargoVolumeFromWeight : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_volume) // Reuse same layout structure

        val weightInput: EditText = findViewById(R.id.inputVolume)
        weightInput.hint = "Cargo Weight"

        val sfInput: EditText = findViewById(R.id.inputSF)
        val volUnitSpinner: Spinner = findViewById(R.id.volumeUnitSpinner)
        val sfUnitSpinner: Spinner = findViewById(R.id.sfUnitSpinner)
        val btnCalculate: Button = findViewById(R.id.btnCalculate)
        btnCalculate.text = "Calculate Volume"
        val resultText: TextView = findViewById(R.id.resultText)

        val weightUnits = arrayOf("tonne (metric)", "long ton (UK)", "short ton (US)")
        val sfUnits = arrayOf("m³/tonne", "ft³/long ton", "ft³/short ton")

        volUnitSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, weightUnits)
        sfUnitSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sfUnits)

        btnCalculate.setOnClickListener {
            try {
                val weightStr = weightInput.text.toString().trim()
                val sfStr = sfInput.text.toString().trim()

                if (weightStr.isEmpty() || sfStr.isEmpty()) {
                    Toast.makeText(this, "Fill in both fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val weight = weightStr.toDouble()
                val sf = sfStr.toDouble()

                if (sf <= 0) {
                    Toast.makeText(this, "SF must be > 0", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val wtUnitIdx = volUnitSpinner.selectedItemPosition // repurposed as weight unit
                val sfUnitIdx = sfUnitSpinner.selectedItemPosition

                // Normalize weight to metric tonnes
                val weightTonnes = when (wtUnitIdx) {
                    0 -> weight
                    1 -> weight / 1.016046
                    2 -> weight / 0.907185
                    else -> weight
                }

                // Normalize SF to m³/tonne
                val sfM3T = when (sfUnitIdx) {
                    0 -> sf
                    1 -> sf / 34.767
                    2 -> sf / 38.928
                    else -> sf
                }

                val volumeM3 = weightTonnes * sfM3T
                val volumeFt3 = volumeM3 * 35.3147

                val df = DecimalFormat("#,###.##")

                resultText.text = """
                    ┌─────────────────────────────────────────┐
                    │  VOLUME FROM WEIGHT CALCULATION          │
                    ├─────────────────────────────────────────┤
                    │ Cargo Weight: ${df.format(weightTonnes)} metric tonnes
                    │ Stowage Factor: ${df.format(sfM3T)} m³/tonne
                    │
                    │ ─── RESULTS ───
                    │ Volume Required: ${df.format(volumeM3)} m³
                    │ Volume Required: ${df.format(volumeFt3)} ft³
                    │
                    │ ─── INVERSE CHECK ───
                    │ Weight verified: ${df.format(volumeM3 / sfM3T)} tonnes
                    │ Match: ${if (Math.abs(volumeM3 / sfM3T - weightTonnes) < 0.01) "✓ OK" else "✗ Mismatch"}
                    └─────────────────────────────────────────┘
                """.trimIndent()

            } catch (e: Exception) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
