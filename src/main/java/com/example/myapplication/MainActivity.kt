package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var textLastEntry: TextView
    private lateinit var buttonPickDate: Button
    private lateinit var textPickedDate: TextView
    private lateinit var editWeight: EditText
    private lateinit var buttonSave: Button
    private lateinit var recyclerViewWeights: RecyclerView
    private lateinit var adapter: WeightAdapter

    private val PREFS_NAME = "udaje_vahy"
    private val KEY_WEIGHT_HISTORY = "historia_vahy"

    private val gson = Gson()
    private var weightHistory = mutableListOf<WeightEntry>()

    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textLastEntry = findViewById(R.id.textLastEntry)
        buttonPickDate = findViewById(R.id.buttonPickDate)
        textPickedDate = findViewById(R.id.textPickedDate)
        editWeight = findViewById(R.id.editWeight)
        buttonSave = findViewById(R.id.buttonSave)
        recyclerViewWeights = findViewById(R.id.recyclerViewWeights)

        recyclerViewWeights.layoutManager = LinearLayoutManager(this)

        loadWeightHistory()
        updateUI()

        buttonPickDate.setOnClickListener {
            showDatePicker()
        }

        buttonSave.setOnClickListener {
            val weightText = editWeight.text.toString()
            if (weightText.isBlank()) {
                Toast.makeText(this, "Zadaj váhu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = weightText.toFloatOrNull()
            if (weight == null || weight <= 0) {
                Toast.makeText(this, "Neplatná váha!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedDate == null) {
                Toast.makeText(this, "Vyber dátum!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addWeightEntry(selectedDate!!, weight)
            editWeight.text.clear()
            selectedDate = null
            textPickedDate.text = "Dátum nie je vybraný"
            Toast.makeText(this, "Záznam uložený", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = sdf.format(cal.time)
                textPickedDate.text = "Vybraný dátum: $selectedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    private fun loadWeightHistory() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_WEIGHT_HISTORY, null)
        if (json != null) {
            try {
                val type = object : TypeToken<MutableList<WeightEntry>>() {}.type
                weightHistory = gson.fromJson(json, type)
            } catch (e: Exception) {
                e.printStackTrace()
                prefs.edit().remove(KEY_WEIGHT_HISTORY).apply()
                weightHistory = mutableListOf()
            }
        }
    }

    private fun saveWeightHistory() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(weightHistory)
        prefs.edit().putString(KEY_WEIGHT_HISTORY, json).apply()
    }

    private fun addWeightEntry(date: String, weight: Float) {
        weightHistory.add(WeightEntry(date, weight))
        saveWeightHistory()
        updateUI()
    }

    private fun updateUI() {
        if (weightHistory.isNotEmpty()) {
            val lastEntry = weightHistory.last()
            textLastEntry.text = "Posledný záznam: ${lastEntry.date} - ${lastEntry.weight} kg"
        } else {
            textLastEntry.text = "Posledný záznam: -"
        }

        adapter = WeightAdapter(weightHistory)
        recyclerViewWeights.adapter = adapter
    }
}
