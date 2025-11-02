package com.example.predictiveapp.visuals

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.max

/**
 * Animated histogram visualization component
 * Features:
 * - Animated bar transitions
 * - Dynamic gradient coloring based on value intensity
 * - Auto-scaling Y-axis
 */
@Composable
fun HistogramView(data: List<Double>, modifier: Modifier = Modifier) {
    // Animation state for bar heights
    val animatedHeights = remember { mutableStateListOf<Float>() }
    
    LaunchedEffect(data) {
        animatedHeights.clear()
        animatedHeights.addAll(List(data.size) { 0f })
        
        // Animate each bar sequentially
        data.forEachIndexed { index, value ->
            val targetHeight = value.toFloat()
            animatedHeights[index] = targetHeight
            kotlinx.coroutines.delay(30) // Stagger animation
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        if (data.isEmpty()) return@Canvas
        
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barCount = data.size
        val barWidth = (canvasWidth / barCount) * 0.8f
        val spacing = (canvasWidth / barCount) * 0.2f
        
        // Find max value for scaling
        val maxValue = max(data.maxOrNull() ?: 1.0, 1.0)
        
        data.forEachIndexed { index, value ->
            val barHeight = (animatedHeights.getOrNull(index) ?: 0f) / maxValue.toFloat() * canvasHeight * 0.9f
            val x = index * (barWidth + spacing) + spacing / 2
            val y = canvasHeight - barHeight
            
            // Calculate color intensity based on value
            val intensity = (value / maxValue).toFloat()
            val startColor = Color(0xFF4CAF50).copy(alpha = 0.6f + intensity * 0.4f)
            val endColor = Color(0xFF2196F3).copy(alpha = 0.6f + intensity * 0.4f)
            
            // Draw bar with gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(startColor, endColor),
                    startY = y,
                    endY = canvasHeight
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
        
        // Draw baseline
        drawLine(
            color = Color.Gray,
            start = Offset(0f, canvasHeight),
            end = Offset(canvasWidth, canvasHeight),
            strokeWidth = 2f
        )
    }
}
