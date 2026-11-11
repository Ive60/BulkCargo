package com.example.bulkcargo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

/**
 * Stowage Factor (SF) Unit Converter
 *
 * Key relationships:
 *   1 m³ = 35.3147 ft³
 *   1 long ton = 1.016046 tonnes (metric)
 *   1 short ton = 0.907185 tonnes (metric)
 *
 * Conversion formulas:
 *   SF_m3_t = SF_ft3_lt / (35.3147 / 1.016046) = SF_ft3_lt / 34.767
 *   SF_m3_t = SF_ft3_st / (35.3147 / 0.907185)  = SF_ft3_st / 38.928
 *   SF_ft3_lt = SF_m3_t * 34.767
 *   SF_ft3_st = SF_m3_t * 38.928
 */
class SfConverter : AppCompatActivity() {

    private lateinit var inputSF: EditText
    private lateinit var fromUnitSpinner: Spinner
    private lateinit var toUnitSpinner: Spinner
    private lateinit var btnConvert: Button
    private lateinit var resultText: TextView

    private val unitLabels = arrayOf(
        "m³/tonne (metric)",
        "ft³/long ton (UK)",
        "ft³/short ton (US)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sf_converter)

        inputSF = findViewById(R.id.inputSF)
        fromUnitSpinner = findViewById(R.id.fromUnitSpinner)
        toUnitSpinner = findViewById(R.id.toUnitSpinner)
        btnConvert = findViewById(R.id.btnConvert)
        resultText = findViewById(R.id.resultText)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unitLabels)
        fromUnitSpinner.adapter = adapter
        toUnitSpinner.adapter = adapter

        // Default: m³/tonne -> ft³/long ton
        fromUnitSpinner.setSelection(0)
        toUnitSpinner.setSelection(1)

        btnConvert.setOnClickListener {
            val input = inputSF.text.toString().trim()
            if (input.isEmpty()) {
                inputSF.error = "Enter a value"
                return@setOnClickListener
            }

            try {
                val value = input.toDouble()
                val fromIdx = fromUnitSpinner.selectedItemPosition
                val toIdx = toUnitSpinner.selectedItemPosition

                val result = convertSF(value, fromIdx, toIdx)
                val df = DecimalFormat("#.####")

                resultText.text = """
                    ┌─────────────────────────────────┐
                    │   STOWAGE FACTOR CONVERSION     │
                    ├─────────────────────────────────┤
                    │ Input:  ${df.format(value)} ${unitLabels[fromIdx]}
                    │ 
                    │ Output: ${df.format(result)} ${unitLabels[toIdx]}
                    ├─────────────────────────────────┤
                    │ All three equivalents:
                    │ • m³/tonne:    ${df.format(toM3T(result, toIdx))}
                    │ • ft³/long t:  ${df.format(toFt3LT(result, toIdx))}
                    │ • ft³/short t: ${df.format(toFt3ST(result, toIdx))}
                    └─────────────────────────────────┘
                """.trimIndent()

            } catch (e: Exception) {
                resultText.text = "Error: Invalid input"
            }
        }
    }

    /**
     * Convert SF from one unit index to another.
     * Index 0 = m³/tonne, 1 = ft³/long ton, 2 = ft³/short ton
     */
    private fun convertSF(value: Double, from: Int, to: Int): Double {
        if (from == to) return value

        // First convert to m³/tonne (index 0)
        val inM3T = toM3T(value, from)
        // Then convert from m³/tonne to target
        return fromM3T(inM3T, to)
    }

    private fun toM3T(value: Double, fromIdx: Int): Double {
        return when (fromIdx) {
            0 -> value                          // already m³/tonne
            1 -> value / 34.767                 // ft³/long ton -> m³/tonne
            2 -> value / 38.928                 // ft³/short ton -> m³/tonne
            else -> 0.0
        }
    }

    private fun fromM3T(value: Double, toIdx: Int): Double {
        return when (toIdx) {
            0 -> value                          // already m³/tonne
            1 -> value * 34.767                 // m³/tonne -> ft³/long ton
            2 -> value * 38.928                 // m³/tonne -> ft³/short ton
            else -> 0.0
        }
    }

    private fun toFt3LT(value: Double, fromIdx: Int): Double {
        val m3t = toM3T(value, fromIdx)
        return fromM3T(m3t, 1)
    }

    private fun toFt3ST(value: Double, fromIdx: Int): Double {
        val m3t = toM3T(value, fromIdx)
        return fromM3T(m3t, 2)
    }
}
