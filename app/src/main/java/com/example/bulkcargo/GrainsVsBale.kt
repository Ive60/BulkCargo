package com.example.bulkcargo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

/**
 * Grains vs Bale Capacity Conversion
 *
 * Grains capacity = maximum volume with no dunnage
 * Bale capacity = volume with dunnage (typically 3-7% less)
 *
 * Dunnage factor typically 3-7% depending on hold shape
 */
class GrainsVsBale : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grains_bale)

        val grainsInput: EditText = findViewById(R.id.inputGrains)
        val dunnageInput: EditText = findViewById(R.id.inputDunnage)
        val baleInput: EditText = findViewById(R.id.inputBale)
        val btnGrainsToBale: Button = findViewById(R.id.btnGrainsToBale)
        val btnBaleToGrains: Button = findViewById(R.id.btnBaleToGrains)
        val resultText: TextView = findViewById(R.id.resultText)

        dunnageInput.setText("5.0")

        val df = DecimalFormat("#,###.##")

        btnGrainsToBale.setOnClickListener {
            try {
                val grainsVol = grainsInput.text.toString().trim().toDoubleOrNull()
                    ?: throw Exception("Enter grains volume")
                val dunnagePct = dunnageInput.text.toString().trim().toDoubleOrNull()
                    ?: throw Exception("Enter dunnage %")

                val baleVol = grainsVol * (1 - dunnagePct / 100)

                resultText.text = """
                    ┌──────────────────────────────────────┐
                    │  GRAINS → BALE CAPACITY              │
                    ├──────────────────────────────────────┤
                    │ Grains Capacity: ${df.format(grainsVol)} m³
                    │ Dunnage Factor:  ${dunnagePct}%
                    │
                    │ Bale Capacity:   ${df.format(baleVol)} m³
                    │ Volume Lost:     ${df.format(grainsVol - baleVol)} m³
                    │
                    │ Ratio (Bale/Grains): ${df.format(baleVol / grainsVol * 100)}%
                    └──────────────────────────────────────┘
                """.trimIndent()
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        btnBaleToGrains.setOnClickListener {
            try {
                val baleVol = baleInput.text.toString().trim().toDoubleOrNull()
                    ?: throw Exception("Enter bale volume")
                val dunnagePct = dunnageInput.text.toString().trim().toDoubleOrNull()
                    ?: throw Exception("Enter dunnage %")

                val grainsVol = baleVol / (1 - dunnagePct / 100)

                resultText.text = """
                    ┌──────────────────────────────────────┐
                    │  BALE → GRAINS CAPACITY              │
                    ├──────────────────────────────────────┤
                    │ Bale Capacity:    ${df.format(baleVol)} m³
                    │ Dunnage Factor:   ${dunnagePct}%
                    │
                    │ Grains Capacity:  ${df.format(grainsVol)} m³
                    │ Volume Difference: ${df.format(grainsVol - baleVol)} m³
                    │
                    │ Ratio (Bale/Grains): ${df.format(baleVol / grainsVol * 100)}%
                    └──────────────────────────────────────┘
                """.trimIndent()
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
