package com.example.simplesavings

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint.Align
import android.os.Bundle
import android.widget.TableLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.room.Database
import androidx.room.Room
import com.example.simplesavings.composable.CreateCategoryForm
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.category.Category
import com.example.simplesavings.model.group.Group
import com.example.simplesavings.ui.theme.SimpleSavingsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleSavingsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Groupsd(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Groupsd (modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "group"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    val groupList by db.groupDao().getAll().collectAsState(initial = emptyList())

    for (group in groupList) {
        val categoryListForGroup by db.categoryDao().getCategoriesForGroup(group.uid).collectAsState(initial = emptyList())

        var groupPlannedTotal = 0.0
        var groupSpentTotal = 0.0

        for (category in categoryListForGroup) {
            groupPlannedTotal += category.planned
        }

        group.plannedTotal = groupPlannedTotal
        group.spentTotal = groupSpentTotal
    }

    var showCard by remember { mutableStateOf( false) }
    var showCreateCategoryForm by remember { mutableStateOf( false) }
    val scope = rememberCoroutineScope()

    if (showCard) {
        CreateGroupCard(
            Modifier,
            db = db,
            {showCard = false})
    }

    if (showCreateCategoryForm) {
        CreateCategoryForm(
            Modifier,
            db = db,
            {showCreateCategoryForm = false},
            groupList)
    }

    Column(modifier = modifier
        .padding(10.dp, 15.dp)
        .verticalScroll(rememberScrollState()) ) {

        for (group in groupList) {
            ElevatedCard (
                elevation = CardDefaults.cardElevation (
                    defaultElevation = 6.dp
                ),
                modifier = Modifier.fillMaxSize().padding(20.dp)
            ) {
                Row (
                    modifier = Modifier.fillMaxSize().padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column (
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text (
                            text = group.name
                        )
                    }

                    Row (
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button (
                            onClick = {
                                showCreateCategoryForm = true
                            }
                        ) {
                            Text (
                                text = "Add Cat"
                            )
                        }
                        Button (
                            onClick = {
                                scope.launch {
                                    db.groupDao().delete(group)
                                }
                            }
                        ) {
                            Text (
                                text = "Del"
                            )
                        }
                    }
                }
                Row(
                    Modifier.padding(5.dp).fillMaxWidth(),

                    ) {
                    Text (
                        text = "",
                        Modifier.weight(.4F)
                    )
                    Text (
                        text = "$${group.plannedTotal.toString()}",
                        Modifier.weight(.2F).padding(end = 5.dp),
                        textAlign = TextAlign.End
                    )
                    Text (
                        text = "$${group.spentTotal.toString()}",
                        Modifier.weight(.2F).padding(end = 5.dp),
                        textAlign = TextAlign.End
                    )
                    Text (
                        text = "",
                        Modifier.weight(.2F)
                    )
                }
                HorizontalDivider(thickness = 1.dp)
                Row (
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text (
                        text = "Category",
                        modifier = Modifier.weight(.4F),
                    )
                    Text (
                        text = "Planned",
                        Modifier.weight(.2F)
                    )
                    Text (
                        text = "Spent",
                        Modifier.weight(.2F)
                    )
                    Text (
                        text = "Type",
                        Modifier.weight(.2F)
                    )
                }
                HorizontalDivider(thickness = 2.dp, color = Color.Cyan)

                Column() {
//                    val categoryList by db.categoryDao().getAll().collectAsState(initial = emptyList())
                    val categoryList by db.categoryDao().getCategoriesForGroup(group.uid).collectAsState(initial = emptyList())
                    for (category in categoryList) {
                        Row(
                            Modifier.padding(5.dp).fillMaxWidth(),

                        ) {
                            Text (
                                text = category.name,
                                Modifier.weight(.4F),
                            )
                            Text (
                                text = "$${category.planned.toString()}",
                                Modifier.weight(.2F).padding(end = 5.dp),
                                textAlign = TextAlign.End
                            )
                            Text (
                                text = "$${category.spent.toString()}",
                                Modifier.weight(.2F).padding(end = 5.dp),
                                textAlign = TextAlign.End
                            )
                            Text (
                                text = category.spendingType.toString(),
                                Modifier.weight(.2F)
                            )
                        }
                        HorizontalDivider(thickness = 1.dp)

                    }
                }

            }
        }
        Button (
            onClick = {
                showCard = true
            }
        ) {
            Text (
                text = "Add Group"
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CreateGroupCard(
    modifier: Modifier = Modifier,
    db: AppDatabase,
    onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var groupName: MutableState<String> = mutableStateOf("")

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
            TextField(
                value = groupName.value,
                onValueChange = { groupName.value = it }
            )
            Button(
                onClick = {
                    scope.launch {
                        db.groupDao().insert(Group(0, groupName.value))
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