package com.example.simplesavings.composable.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.simplesavings.enums.Navigation

@Composable
fun NavigationRow(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF664B47), // Set your background color here
            contentColor = Color.White         // Optional: Set default text color
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NavigationItem(
                modifier = Modifier.weight(1F),
                navController = navController,
                currentRoute = currentRoute,
                Navigation.TransactionSummary
            )

            NavigationItem(
                modifier = Modifier.weight(1F),
                navController = navController,
                currentRoute = currentRoute,
                Navigation.Income
            )

            NavigationItem(
                modifier = Modifier.weight(1F),
                navController = navController,
                currentRoute = currentRoute,
                Navigation.Budget
            )

            NavigationItem(
                modifier = Modifier.weight(1F),
                navController = navController,
                currentRoute = currentRoute,
                Navigation.Transactions
            )
        }
    }
}

@Composable
fun NavigationItem(
    modifier: Modifier,
    navController: NavHostController,
    currentRoute: String?,
    navigation: Navigation
) {
    Column(
        modifier = modifier
            .clickable {
                navController.navigate(navigation.name)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Summarize,
            contentDescription = "Backward",
            tint = getNavigationElementColor(currentRoute, navigation),
        )
        Text(
            text = "Summary",
            color = getNavigationElementColor(currentRoute, navigation),
            fontSize = 10.sp
        )
    }
}

fun getNavigationElementColor(
    currentRoute: String?,
    navigation: Navigation
): Color {
    if (currentRoute == navigation.name)
        return Color.Cyan
    else
        return Color.White
}
