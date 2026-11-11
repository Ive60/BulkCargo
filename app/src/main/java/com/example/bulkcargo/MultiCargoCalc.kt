package com.example.bulkcargo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.Locale

/**
 * Multi-Cargo Loading Calculator
 *
 * Calculate total weight and volume when loading multiple
 * different bulk cargo types on the same vessel.
 */
class MultiCargoCalc : AppCompatActivity() {

    private lateinit var cargoListInput: EditText
    private lateinit var btnCalculate: Button
    private lateinit var resultText: TextView

    /**
     * Input format (one cargo per line):
     * CargoName,Weight_tonnes,SF_m3_per_tonne
     *
     * Example:
     * Iron Ore,50000,0.55
     * Coal,30000,1.20
     * Grain,20000,1.45
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_cargo)

        cargoListInput = findViewById(R.id.inputCargoList)
        btnCalculate = findViewById(R.id.btnCalculate)
        resultText = findViewById(R.id.resultText)

        cargoListInput.setText(
            "Iron Ore,50000,0.55\nCoal,30000,1.20\nGrain,20000,1.45"
        )

        btnCalculate.setOnClickListener { calculateMultiCargo() }
    }

    data class CargoEntry(
        val name: String,
        val weightTonnes: Double,
        val sfM3T: Double
    )

    private fun parseCargoList(input: String): List<CargoEntry> {
        val entries = mutableListOf<CargoEntry>()
        for (line in input.trim().lineSequence()) {
            val parts = line.trim().split(",")
            if (parts.size >= 3) {
                try {
                    val w = parts[1].trim().toDoubleOrNull()
                    val sf = parts[2].trim().toDoubleOrNull()
                    if (w != null && sf != null) {
                        entries.add(CargoEntry(
                            name = parts[0].trim(),
                            weightTonnes = w,
                            sfM3T = sf
                        ))
                    }
                } catch (_: Throwable) {
                    // skip
                }
            }
        }
        return entries
    }

    private fun calculateMultiCargo() {
        try {
            val input = cargoListInput.text.toString().trim()
            val cargoes = parseCargoList(input)

            if (cargoes.isEmpty()) {
                Toast.makeText(this, "No valid cargo entries", Toast.LENGTH_SHORT).show()
                return
            }

            val df = DecimalFormat("#,###.##")
            val pctF = DecimalFormat("###.##")

            val totalWeight = cargoes.sumOf { it.weightTonnes }
            if (totalWeight <= 0) {
                Toast.makeText(this, "Total weight must be > 0", Toast.LENGTH_SHORT).show()
                return
            }

            val totalVolume = cargoes.sumOf { it.weightTonnes * it.sfM3T }
            val avgSF = totalVolume / totalWeight

            val sb = StringBuilder()
            sb.append("┌─────────────────────────────────────────────────────┐\n")
            sb.append("│       MULTI-CARGO LOADING CALCULATION               │\n")
            sb.append("├─────────────────────────────────────────────────────┤\n")
            sb.append(String.format(Locale.US, "│ %-15s %12s %10s %12s %10s%n",
                "Cargo", "Weight", "SF", "Volume", "Weight%"))

            for (cargo in cargoes) {
                val vol = cargo.weightTonnes * cargo.sfM3T
                val wtPct = (cargo.weightTonnes / totalWeight) * 100

                sb.append(String.format(Locale.US, "│ %-15s %12s %10s %12s %9s%%%n",
                    cargo.name,
                    df.format(cargo.weightTonnes),
                    df.format(cargo.sfM3T),
                    df.format(vol),
                    pctF.format(wtPct)))
            }

            sb.append("│                                                   │\n")
            sb.append(String.format(Locale.US, "│ TOTAL:       %12s %10s %12s %10s%n",
                df.format(totalWeight),
                df.format(avgSF),
                df.format(totalVolume),
                "100.00"))

            sb.append("│                                                   │\n")
            sb.append(String.format(Locale.US, "│ Average SF:  %12.4f m³/tonne%n", avgSF))
            sb.append(String.format(Locale.US, "│ Total Volume:%10s m³  (%s ft³)%n",
                df.format(totalVolume),
                df.format(totalVolume * 35.3147)))
            sb.append(String.format(Locale.US, "│ Total Weight:%10s tonnes%n", df.format(totalWeight)))
            sb.append("└─────────────────────────────────────────────────────┘")

            resultText.text = sb.toString()

        } catch (e: Throwable) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
