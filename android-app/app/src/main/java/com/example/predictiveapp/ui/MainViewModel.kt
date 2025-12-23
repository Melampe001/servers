package com.example.predictiveapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.predictiveapp.data.DataRepository
import com.example.predictiveapp.logic.StatisticalAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel for managing data and analysis state
 */
class MainViewModel : ViewModel() {
    private val repository = DataRepository()
    private val analyzer = StatisticalAnalyzer()
    
    private val _dataState = MutableStateFlow(DataState())
    val dataState: StateFlow<DataState> = _dataState
    
    /**
     * Generate sample data for demonstration
     */
    fun generateSampleData() {
        viewModelScope.launch {
            val sampleData = List(50) { Random.nextDouble(0.0, 100.0) }
            _dataState.value = _dataState.value.copy(data = sampleData)
        }
    }
    
    /**
     * Analyze the current data
     */
    fun analyzeData() {
        viewModelScope.launch {
            val data = _dataState.value.data
            if (data.isNotEmpty()) {
                val analysis = analyzer.analyze(data)
                _dataState.value = _dataState.value.copy(analysis = analysis)
            }
        }
    }
}

data class DataState(
    val data: List<Double> = emptyList(),
    val analysis: AnalysisResult? = null
)

data class AnalysisResult(
    val mean: Double,
    val median: Double,
    val stdDev: Double,
    val trend: String
)
