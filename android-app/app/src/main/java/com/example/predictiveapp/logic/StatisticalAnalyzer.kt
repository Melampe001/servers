package com.example.predictiveapp.logic

import com.example.predictiveapp.ui.AnalysisResult
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Statistical analysis for numerical data series
 * Provides basic statistical measures and trend detection
 */
class StatisticalAnalyzer {
    
    /**
     * Perform comprehensive statistical analysis on data
     */
    fun analyze(data: List<Double>): AnalysisResult {
        val mean = calculateMean(data)
        val median = calculateMedian(data)
        val stdDev = calculateStdDev(data, mean)
        val trend = detectTrend(data)
        
        return AnalysisResult(
            mean = mean,
            median = median,
            stdDev = stdDev,
            trend = trend
        )
    }
    
    /**
     * Calculate the arithmetic mean of the data
     */
    private fun calculateMean(data: List<Double>): Double {
        return if (data.isEmpty()) 0.0 else data.sum() / data.size
    }
    
    /**
     * Calculate the median of the data
     */
    private fun calculateMedian(data: List<Double>): Double {
        if (data.isEmpty()) return 0.0
        val sorted = data.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 0) {
            (sorted[middle - 1] + sorted[middle]) / 2.0
        } else {
            sorted[middle]
        }
    }
    
    /**
     * Calculate the standard deviation of the data
     */
    private fun calculateStdDev(data: List<Double>, mean: Double): Double {
        if (data.size < 2) return 0.0
        val variance = data.map { (it - mean).pow(2) }.sum() / (data.size - 1)
        return sqrt(variance)
    }
    
    /**
     * Detect trend in the data using simple linear regression
     */
    private fun detectTrend(data: List<Double>): String {
        if (data.size < 2) return "Insufficient data"
        
        // Simple moving average approach
        val windowSize = minOf(5, data.size / 2)
        if (windowSize < 2) return "Neutral"
        
        val firstHalf = data.take(windowSize).average()
        val lastHalf = data.takeLast(windowSize).average()
        
        return when {
            lastHalf > firstHalf * 1.05 -> "Upward"
            lastHalf < firstHalf * 0.95 -> "Downward"
            else -> "Stable"
        }
    }
    
    /**
     * Calculate moving average for smoothing
     */
    fun calculateMovingAverage(data: List<Double>, windowSize: Int): List<Double> {
        if (data.size < windowSize) return emptyList()
        
        return data.windowed(windowSize) { window ->
            window.average()
        }
    }
}
