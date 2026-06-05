package com.example.simplesavings.composable.budget

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateGroupCard(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit,
    currentMonthString: String,
    currentYearString: String) {
    val scope = rememberCoroutineScope()
    var groupName: MutableState<String> = mutableStateOf("")

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(100F),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
        ) {
            TextField(
                value = groupName.value,
                onValueChange = { groupName.value = it }
            )
            Button(
                onClick = {
                    scope.launch {
                        db.groupDao().insert(Group(0, groupName.value, month = currentMonthString, year = currentYearString))
                    }
                },
                enabled = groupName.value != ""
            ) {
                Text(
                    text = "Add Group"
                )
            }
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = "Close"
                )
            }
        }
    }
}