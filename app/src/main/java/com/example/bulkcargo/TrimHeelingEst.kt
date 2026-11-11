package com.example.bulkcargo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

/**
 * Trim & Heeling Moment Estimator
 *
 * Simplified calculation of trim and heeling moments based on
 * cargo distribution across holds.
 */
class TrimHeelingEst : AppCompatActivity() {

    private var cargoDistInput: EditText? = null
    private var lcbInput: EditText? = null
    private var lcfInput: EditText? = null
    private var mtcInput: EditText? = null
    private var tpInput: EditText? = null
    private var btnCalculate: Button? = null
    private var resultText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_trim_heeling)

            cargoDistInput = findViewById(R.id.inputCargoDist)
            lcbInput = findViewById(R.id.inputLCB)
            lcfInput = findViewById(R.id.inputLCF)
            mtcInput = findViewById(R.id.inputMTC)
            tpInput = findViewById(R.id.inputTP)
            btnCalculate = findViewById(R.id.btnCalculate)
            resultText = findViewById(R.id.resultText)

            cargoDistInput?.setText(
                "No.1,10000,25.0\nNo.2,15000,65.0\nNo.3,12000,105.0\nNo.4,8000,145.0\nNo.5,5000,180.0"
            )
            lcbInput?.setText("100.0")
            lcfInput?.setText("95.0")
            mtcInput?.setText("200.0")
            tpInput?.setText("20.0")

            btnCalculate?.setOnClickListener { calculateTrimHeeling() }
        } catch (e: Throwable) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load Trim Estimator: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    data class HoldCargo(
        val name: String,
        val weightTonnes: Double,
        val lcgMeters: Double
    )

    private fun parseHoldCargo(input: String): List<HoldCargo> {
        val entries = mutableListOf<HoldCargo>()
        for (line in input.trim().lineSequence()) {
            val parts = line.trim().split(",")
            if (parts.size >= 3) {
                try {
                    val w = parts[1].trim().toDoubleOrNull()
                    val lcg = parts[2].trim().toDoubleOrNull()
                    if (w != null && lcg != null) {
                        entries.add(
                            HoldCargo(
                                name = parts[0].trim(),
                                weightTonnes = w,
                                lcgMeters = lcg
                            )
                        )
                    }
                } catch (_: Throwable) {}
            }
        }
        return entries
    }

    private fun calculateTrimHeeling() {
        try {
            val cargoInput = cargoDistInput?.text?.toString()?.trim().orEmpty()
            val lcbStr = lcbInput?.text?.toString()?.trim().orEmpty()
            val lcfStr = lcfInput?.text?.toString()?.trim().orEmpty()
            val mtcStr = mtcInput?.text?.toString()?.trim().orEmpty()
            val tpStr = tpInput?.text?.toString()?.trim().orEmpty()

            if (cargoInput.isEmpty()) {
                Toast.makeText(this, "Please enter cargo distribution", Toast.LENGTH_SHORT).show()
                return
            }

            val lcb = lcbStr.toDoubleOrNull()
            if (lcb == null) {
                Toast.makeText(this, "Enter valid LCB value", Toast.LENGTH_SHORT).show()
                return
            }

            val lcf = lcfStr.toDoubleOrNull()
            if (lcf == null) {
                Toast.makeText(this, "Enter valid LCF value", Toast.LENGTH_SHORT).show()
                return
            }

            val mtc = mtcStr.toDoubleOrNull()
            if (mtc == null || mtc <= 0) {
                Toast.makeText(this, "MTC must be > 0", Toast.LENGTH_SHORT).show()
                return
            }

            val tp = tpStr.toDoubleOrNull()
            if (tp == null || tp <= 0) {
                Toast.makeText(this, "TP must be > 0", Toast.LENGTH_SHORT).show()
                return
            }

            val cargoes = parseHoldCargo(cargoInput)
            if (cargoes.isEmpty()) {
                Toast.makeText(this, "No valid cargo entries parsed. Format: Name,Weight,LCG", Toast.LENGTH_SHORT).show()
                return
            }

            val totalWeight = cargoes.sumOf { it.weightTonnes }
            if (totalWeight <= 0) {
                Toast.makeText(this, "Total weight must be > 0", Toast.LENGTH_SHORT).show()
                return
            }

            val df = DecimalFormat("#,##0.##")
            val df4 = DecimalFormat("#,##0.####")

            val momentLCG = cargoes.sumOf { it.weightTonnes * it.lcgMeters }
            val vesselLCG = momentLCG / totalWeight

            // Trim moment about LCF
            val trimMomentTm = momentLCG - totalWeight * lcf
            // Trim in meters (MTC is t-m/cm, so x100 for m)
            val trimMeters = trimMomentTm / (mtc * 100)

            val trimByStern = trimMeters
            val sinkageMeters = totalWeight / (tp * 100)

            val cargoSummary = cargoes.joinToString("\n") { c ->
                "  • %-10s: %10s t @ LCG %6s m  (Moment: %12s t-m)".format(
                    c.name,
                    df.format(c.weightTonnes),
                    df.format(c.lcgMeters),
                    df.format(c.weightTonnes * c.lcgMeters)
                )
            }

            val trimDirection = when {
                trimByStern > 0 -> "By Stern"
                trimByStern < 0 -> "By Bow"
                else -> "Even Keel"
            }

            val outputText = """
                ⚓ TRIM & HEELING ESTIMATE

                PER-HOLD CARGO DISTRIBUTION:
                $cargoSummary

                TOTALS & VESSEL PARAMETERS:
                  • Total Cargo Weight: ${df.format(totalWeight)} tonnes
                  • Total Longitudinal Moment: ${df.format(momentLCG)} t-m
                  • Calculated Vessel LCG: ${df.format(vesselLCG)} m
                  • LCF: ${df.format(lcf)} m  |  LCB: ${df.format(lcb)} m
                  • MTC: ${df.format(mtc)} t-m/cm  |  TP: ${df.format(tp)} t/cm

                CALCULATED RESULTS:
                  • Trim Moment (about LCF): ${df.format(trimMomentTm)} t-m
                  • Estimated Trim: ${df4.format(trimByStern)} m ($trimDirection)
                  • Parallel Sinkage: ${df4.format(sinkageMeters)} m

                ⚠️ Note: This is a simplified calculation. Always verify with official ship stability book and hydrostatic tables.
            """.trimIndent()

            resultText?.text = outputText

        } catch (e: Throwable) {
            Toast.makeText(this, "Calculation error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
