package hu.bme.aut.android.costofliving

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.android.expenselist.databinding.ActivityGraphBinding

class GraphActivity : AppCompatActivity(){
    private lateinit var binding: ActivityGraphBinding
    private lateinit var textToShow: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textToShow = intent.getStringExtra("text").toString()
        binding.tvChartName.text = textToShow
        loadExpenses()
    }

    private fun loadExpenses(){
        val graphSP = this.getSharedPreferences("GRAPH", Context.MODE_PRIVATE)
        val entries = mutableListOf<PieEntry>()
        for (item in graphSP.all){
            entries.add(PieEntry(item.value as Float, item.key))
        }
        val dataSet = PieDataSet(entries, "Expenses")
        dataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
        dataSet.valueTextSize = 16f
        val data = PieData(dataSet)
        binding.chartExpenses.data = data
        binding.chartExpenses.invalidate()
    }
}