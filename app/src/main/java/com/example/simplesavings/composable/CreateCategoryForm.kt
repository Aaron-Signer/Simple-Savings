package com.example.simplesavings.composable

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateCategoryForm(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit,
    groupList: List<Group>
) {
    val scope = rememberCoroutineScope()
    var categoryName: MutableState<String> = mutableStateOf("")
    var selectedGroup by remember { mutableStateOf( null) }


    Box(
        modifier = modifier.fillMaxSize().zIndex(100F),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier.height(200.dp).width(200.dp)
        ) {
            Text(
                text = "Category Form"
            )
            TextField(
                value = categoryName.value,
                onValueChange = { categoryName.value = it }
            )
            Button(
                onClick = {
                    scope.launch {
                        db.categoryDao().insert(Category(0, selectedGroup.uid,categoryName.value))
                    }
                },
                enabled = categoryName.value != ""
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
