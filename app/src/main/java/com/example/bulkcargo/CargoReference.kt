package com.example.bulkcargo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

/**
 * Reference table of typical stowage factors for common
 * bulk carrier commodities.
 */
class CargoReference : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reference)

        val resultText: TextView = findViewById(R.id.resultText)
        resultText.text = buildReferenceTable()
    }

    private fun buildReferenceTable(): String {
        val sb = StringBuilder()
        sb.append("┌──────────────────────────────────────────────────┐\n")
        sb.append("│   BULK CARGO STOWAGE FACTOR REFERENCE TABLE      │\n")
        sb.append("│   (Typical values - actual SF varies by grade)   │\n")
        sb.append("├──────────────────────────────────────────────────┤\n")
        sb.append(String.format(Locale.US, "│ %-25s %10s %12s %12s%n",
            "Commodity", "SF m³/t", "SF ft³/LT", "SF ft³/ST"))
        sb.append("│                                                   │\n")

        data class CargoSF(val name: String, val sfM3T: Double)

        val cargoes = listOf(
            CargoSF("Iron Ore (lump)", 0.45),
            CargoSF("Iron Ore (fines)", 0.55),
            CargoSF("Pelletized Iron Ore", 0.60),
            CargoSF("Manganese Ore", 0.65),
            CargoSF("Bauxite", 0.70),
            CargoSF("Coal (bituminous)", 1.00),
            CargoSF("Coal (anthracite)", 0.80),
            CargoSF("Coke", 1.35),
            CargoSF("Wheat", 1.45),
            CargoSF("Corn (Maize)", 1.35),
            CargoSF("Soybeans", 1.30),
            CargoSF("Rice", 1.40),
            CargoSF("Barley", 1.35),
            CargoSF("Oats", 1.60),
            CargoSF("Rapeseed", 1.30),
            CargoSF("Sunflower Seeds", 1.40),
            CargoSF("Soybean Meal", 1.40),
            CargoSF("Rice Bran", 1.90),
            CargoSF("Cement (bulk)", 1.20),
            CargoSF("Cement (bagged)", 2.50),
            CargoSF("Urea Fertilizer", 0.90),
            CargoSF("DAP Fertilizer", 0.95),
            CargoSF("Sulfur (lump)", 0.70),
            CargoSF("Sulfur (powder)", 1.40),
            CargoSF("Salt", 0.70),
            CargoSF("Phosphate Rock", 0.75),
            CargoSF("Potash", 0.80),
            CargoSF("Nickel Ore", 0.60),
            CargoSF("Copper Ore", 0.85),
            CargoSF("Zinc Ore", 0.90),
            CargoSF("Chromite", 0.65),
            CargoSF("Alumina", 0.80),
            CargoSF("Woodchips", 2.50),
            CargoSF("Logs", 4.50),
            CargoSF("Pulp (bales)", 2.80),
            CargoSF("Paper (rolls)", 3.50),
            CargoSF("Sugar (bagged)", 1.00),
            CargoSF("Sugar (bulk)", 0.70),
            CargoSF("Cocoa Beans", 1.80),
            CargoSF("Coffee Beans", 1.40),
            CargoSF("Cotton (bales)", 4.00),
            CargoSF("Limestone", 0.65),
            CargoSF("Dolomite", 0.70),
            CargoSF("Gypsum", 0.80),
            CargoSF("Slag (granulated)", 0.75),
            CargoSF("Petroleum Coke", 1.10),
            CargoSF("Copper Concentrate", 0.65),
            CargoSF("Zinc Concentrate", 0.65),
            CargoSF("Lead Concentrate", 0.60)
        )

        for (cargo in cargoes) {
            val sfFt3LT = cargo.sfM3T * 34.767
            val sfFt3ST = cargo.sfM3T * 38.928

            sb.append(String.format(Locale.US, "│ %-25s %10.2f %12.2f %12.2f%n",
                cargo.name, cargo.sfM3T, sfFt3LT, sfFt3ST))
        }

        sb.append("│                                                   │\n")
        sb.append("│ ─── DENSITY REFERENCE ───                        │\n")
        sb.append(String.format(Locale.US, "│ Density (t/m³) = 1 / SF (m³/t)%n"))
        sb.append(String.format(Locale.US, "│ Density (kg/m³) = 1000 / SF (m³/t)%n"))
        sb.append("│                                                   │\n")
        sb.append("│ ─── CONVERSION FACTORS ───                       │\n")
        sb.append("│ 1 m³/tonne = 34.767 ft³/long ton                 │\n")
        sb.append("│ 1 m³/tonne = 38.928 ft³/short ton                │\n")
        sb.append("│ 1 long ton = 1.016046 metric tonnes              │\n")
        sb.append("│ 1 short ton = 0.907185 metric tonnes             │\n")
        sb.append("│ 1 m³ = 35.3147 ft³                               │\n")
        sb.append("│                                                   │\n")
        sb.append("│ ⚠ VALUES ARE APPROXIMATE. ALWAYS verify with    │\n")
        sb.append("│   supplier/shipper data for actual cargo.       │\n")
        sb.append("└──────────────────────────────────────────────────┘")

        return sb.toString()
    }
}
