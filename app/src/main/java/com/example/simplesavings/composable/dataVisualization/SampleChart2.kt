package com.example.simplesavings.composable.dataVisualization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.example.simplesavings.config.database.AppDatabase
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

private val BottomAxisLabelKey = ExtraStore.Key<List<String>>()

private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore.getOrNull(BottomAxisLabelKey)?.getOrNull(x.toInt()) ?: ""
}
@Composable
fun SampleChart2(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    currentMonthString: String,
    currentYearString: String) {
    val categoryListFlow = remember(
        db,
        currentYearString,
        currentMonthString) {
            db.transactionDao().getTransactionSummaryForMonth(currentYearString, currentMonthString)
    }

    val categoryList by categoryListFlow.collectAsState(initial = emptyList())

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(categoryList) {
        if (categoryList.isNotEmpty()) {
            modelProducer.runTransaction {
                columnModel {
                    series(categoryList.map { it.totalDebit })
                }
                extras { it[BottomAxisLabelKey] = categoryList.map { it.day } }
            }
        }
    }
    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis =
                HorizontalAxis.rememberBottom(
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
                    valueFormatter = BottomAxisValueFormatter,
                ),
        ),
        modelProducer,
        modifier = modifier.rotate(-90f),
    )
}

//@Preview
//@Composable
//fun SampleChart2Preview() {
//    SampleChart2()
//}