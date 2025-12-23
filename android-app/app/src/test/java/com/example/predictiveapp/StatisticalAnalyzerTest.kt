package com.example.predictiveapp

import com.example.predictiveapp.logic.StatisticalAnalyzer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Statistical Analyzer
 */
class StatisticalAnalyzerTest {
    private lateinit var analyzer: StatisticalAnalyzer
    
    @Before
    fun setup() {
        analyzer = StatisticalAnalyzer()
    }
    
    @Test
    fun testAnalyzeEmptyData() {
        val result = analyzer.analyze(emptyList())
        assertEquals(0.0, result.mean, 0.001)
        assertEquals(0.0, result.median, 0.001)
        assertEquals(0.0, result.stdDev, 0.001)
    }
    
    @Test
    fun testAnalyzeSimpleData() {
        val data = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val result = analyzer.analyze(data)
        
        assertEquals(3.0, result.mean, 0.001)
        assertEquals(3.0, result.median, 0.001)
        assertTrue(result.stdDev > 0)
    }
    
    @Test
    fun testAnalyzeEvenCountData() {
        val data = listOf(1.0, 2.0, 3.0, 4.0)
        val result = analyzer.analyze(data)
        
        assertEquals(2.5, result.mean, 0.001)
        assertEquals(2.5, result.median, 0.001)
    }
    
    @Test
    fun testMovingAverage() {
        val data = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val movingAvg = analyzer.calculateMovingAverage(data, 3)
        
        assertEquals(3, movingAvg.size)
        assertEquals(2.0, movingAvg[0], 0.001)
        assertEquals(3.0, movingAvg[1], 0.001)
        assertEquals(4.0, movingAvg[2], 0.001)
    }
    
    @Test
    fun testTrendDetection() {
        val upwardData = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
        val upResult = analyzer.analyze(upwardData)
        assertEquals("Upward", upResult.trend)
        
        val downwardData = listOf(10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0)
        val downResult = analyzer.analyze(downwardData)
        assertEquals("Downward", downResult.trend)
        
        val stableData = listOf(5.0, 5.1, 4.9, 5.0, 5.1, 4.9, 5.0, 5.1, 4.9, 5.0)
        val stableResult = analyzer.analyze(stableData)
        assertEquals("Stable", stableResult.trend)
    }
}
