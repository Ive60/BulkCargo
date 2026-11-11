package com.example.bulkcargo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.Locale

/**
 * Hold Capacity Utilisation Calculator
 *
 * For each hold, compare available volume (grains or bale capacity)
 * against required volume (weight × SF) to determine utilisation.
 */
class HoldCapacityUtil : AppCompatActivity() {

    private lateinit var holdsInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var sfInput: EditText
    private lateinit var btnCalculate: Button
    private lateinit var resultText: TextView

    /**
     * Expected input format for holds (one per line):
     * Hold1,GrainsVol_m3,BaleVol_m3
     * Hold2,GrainsVol_m3,BaleVol_m3
     * ...
     *
     * Example:
     * No.1,15000,13500
     * No.2,18000,16200
     * No.3,20000,18000
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hold_capacity)

        holdsInput = findViewById(R.id.inputHolds)
        weightInput = findViewById(R.id.inputWeight)
        sfInput = findViewById(R.id.inputSF)
        btnCalculate = findViewById(R.id.btnCalculate)
        resultText = findViewById(R.id.resultText)

        // Pre-fill example
        holdsInput.setText(
            "No.1,15000,13500\nNo.2,18000,16200\nNo.3,20000,18000\nNo.4,18000,16200\nNo.5,15000,13500"
        )

        btnCalculate.setOnClickListener { calculateHoldUtilisation() }
    }

    data class HoldInfo(
        val name: String,
        val grainsVolM3: Double,
        val baleVolM3: Double
    )

    private fun parseHolds(input: String): List<HoldInfo> {
        val holds = mutableListOf<HoldInfo>()
        for (line in input.trim().lineSequence()) {
            val parts = line.trim().split(",")
            if (parts.size >= 3) {
                try {
                    holds.add(HoldInfo(
                        name = parts[0].trim(),
                        grainsVolM3 = parts[1].trim().toDouble(),
                        baleVolM3 = parts[2].trim().toDouble()
                    ))
                } catch (_: Exception) {
                    // skip invalid lines
                }
            }
        }
        return holds
    }

    private fun calculateHoldUtilisation() {
        try {
            val weightStr = weightInput.text.toString().trim()
            val sfStr = sfInput.text.toString().trim()
            val holdsStr = holdsInput.text.toString().trim()

            if (weightStr.isEmpty() || sfStr.isEmpty() || holdsStr.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }

            val totalWeight = weightStr.toDouble()
            val sf = sfStr.toDouble()

            if (sf <= 0) {
                Toast.makeText(this, "SF must be > 0", Toast.LENGTH_SHORT).show()
                return
            }

            val holds = parseHolds(holdsStr)
            if (holds.isEmpty()) {
                Toast.makeText(this, "No valid holds parsed", Toast.LENGTH_SHORT).show()
                return
            }

            val requiredVolumeM3 = totalWeight * sf
            val totalGrainsCap = holds.sumOf { it.grainsVolM3 }
            val totalBaleCap = holds.sumOf { it.baleVolM3 }

            val df = DecimalFormat("#,###.##")
            val pctF = DecimalFormat("###.##")

            // Distribute cargo proportionally by grain capacity
            val sb = StringBuilder()
            sb.append("┌───────────────────────────────────────────────────┐\n")
            sb.append("│    HOLD CAPACITY UTILISATION CALCULATION          │\n")
            sb.append("├───────────────────────────────────────────────────┤\n")
            sb.append(String.format(Locale.US, "│ Total Weight:       %,12.2f tonnes%n", totalWeight))
            sb.append(String.format(Locale.US, "│ Stowage Factor:     %,12.4f m³/tonne%n", sf))
            sb.append(String.format(Locale.US, "│ Required Volume:    %,12.2f m³%n", requiredVolumeM3))
            sb.append(String.format(Locale.US, "│ Total Grains Cap:   %,12.2f m³%n", totalGrainsCap))
            sb.append(String.format(Locale.US, "│ Total Bale Cap:     %,12.2f m³%n\n", totalBaleCap))

            sb.append("│ ─── PER-HOLD DISTRIBUTION (by grain %) ─── │\n")
            sb.append(String.format(Locale.US, "│ %-8s %12s %12s %10s %10s%n",
                "Hold", "GrainsCap", "BaleCap", "Weight", "Volume"))

            var distributedWeight = 0.0
            var distributedVolume = 0.0

            for (hold in holds) {
                val grainPct = hold.grainsVolM3 / totalGrainsCap
                val holdWeight = totalWeight * grainPct
                val holdVolume = holdWeight * sf

                distributedWeight += holdWeight
                distributedVolume += holdVolume

                val grainUtil = (holdVolume / hold.grainsVolM3) * 100
                val baleUtil = if (holdVolume > hold.baleVolM3) {
                    "OVERFLOW"
                } else {
                    "${pctF.format((holdVolume / hold.baleVolM3) * 100)}%"
                }

                sb.append(String.format(Locale.US, "│ %-8s %12s %12s %10s %10s%n",
                    hold.name,
                    df.format(hold.grainsVolM3),
                    df.format(hold.baleVolM3),
                    df.format(holdWeight),
                    df.format(holdVolume)))

                sb.append(String.format(Locale.US, "│        Grains: %8s  Bale: %8s%n",
                    if (grainUtil > 100) "⚠ OVER" else "${pctF.format(grainUtil)}%",
                    baleUtil))
            }

            sb.append("│                                               │\n")
            sb.append(String.format(Locale.US, "│ Total Distributed: %,12.2f tonnes / %,12.2f m³%n",
                distributedWeight, distributedVolume))

            val overallGrainUtil = (requiredVolumeM3 / totalGrainsCap) * 100
            val overallBaleUtil = if (requiredVolumeM3 > totalBaleCap) "⚠ OVER"
            else "${pctF.format((requiredVolumeM3 / totalBaleCap) * 100)}%"

            sb.append(String.format(Locale.US, "│ Overall Grain Util: %10s%n", pctF.format(overallGrainUtil) + "%"))
            sb.append(String.format(Locale.US, "│ Overall Bale Util:  %10s%n", overallBaleUtil))
            sb.append("└───────────────────────────────────────────────────┘")

            resultText.text = sb.toString()

        } catch (e: Throwable) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
