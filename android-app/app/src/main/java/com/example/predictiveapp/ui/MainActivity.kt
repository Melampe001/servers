package com.example.predictiveapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.predictiveapp.visuals.HistogramView

/**
 * Main Activity for the Predictive Analytics App
 * Displays data visualization and analysis results
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PredictiveAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val dataState by viewModel.dataState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Predictive Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Histogram visualization
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Data Histogram",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HistogramView(data = dataState.data)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Statistical analysis
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Statistical Analysis",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    dataState.analysis?.let { analysis ->
                        Text("Mean: ${String.format("%.2f", analysis.mean)}")
                        Text("Median: ${String.format("%.2f", analysis.median)}")
                        Text("Std Dev: ${String.format("%.2f", analysis.stdDev)}")
                        Text("Trend: ${analysis.trend}")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.generateSampleData() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Generate Data")
                }
                Button(
                    onClick = { viewModel.analyzeData() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Analyze")
                }
            }
        }
    }
}

@Composable
fun PredictiveAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}
