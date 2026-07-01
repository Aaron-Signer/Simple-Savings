package com.example.simplesavings.composable.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simplesavings.config.database.AppDatabase

@Composable
fun SavingsSummaryCard(
    db: AppDatabase,
    currentMonthString: String,
    currentYearString: String
) {

    val projectedSavings by db.groupDao().getBudgetSummary(currentMonthString, currentYearString).collectAsState(initial = 0.0)

    val plannedTotal by db.groupDao().getPlannedBudget(currentMonthString, currentYearString).collectAsState(initial = 0.0)

    val plannedProjectedBudget by db.groupDao().getPlannedProjectedBudget(currentMonthString, currentYearString).collectAsState(initial = 0.0)

    var a = 0.0
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF664B47), // Set your background color here
            contentColor = Color.White         // Optional: Set default text color
        ),
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Monthly Income"
                )
                Text(
                    text = "$6,134.70"
                )
            }

            HorizontalDivider(
                color = Color.Cyan,
                thickness = 1.dp
            )

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Planned Savings"
                )
                Text(
                    text = "$${"%.2f".format(6134.70 - plannedTotal)}"
                )
            }

            HorizontalDivider(
                color = Color.Cyan,
                thickness = 1.dp
            )

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Planned Savings"
                )
                Text(
                    text = "$${"%.2f".format(6134.70 - plannedProjectedBudget)}"
                )
            }

            HorizontalDivider(
                color = Color.Cyan,
                thickness = 1.dp
            )

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Projected Savings"
                )
                Text(
                    text = "$${"%.2f".format(6134.70 - projectedSavings)}"
                )
            }

            HorizontalDivider(
                color = Color.Cyan,
                thickness = 1.dp
            )

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Projected New Savings"
                )
                Text(
                    text = "$${"%.2f".format(14969.14 + (6134.70 - plannedProjectedBudget))}"
                )
            }

        }
    }
}