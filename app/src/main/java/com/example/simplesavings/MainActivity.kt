package com.example.simplesavings

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.room.Database
import androidx.room.Room
import com.example.simplesavings.config.database.AppDatabase
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
    var showCard by remember { mutableStateOf( false) }

    if (showCard) {
        CreateGroupCard(db = db)
    }

    Column(modifier = modifier
        .padding(10.dp, 15.dp)
        .verticalScroll(rememberScrollState()) ) {
        var categoryList = listOf(Category("Food"), Category("Sarah Shared Activities"))


        var group1 = Group(1, "Test")
        var groupList = listOf(group1)

        val groupDao = db.groupDao()

//        if (groupDao != null) {
            val groupList2 by db.groupDao().getAll().collectAsState(initial = emptyList())

//            var groupList2 = groupDao.getAll()
//        }

//        var groupList = categoryListFromDb


        Button (
            onClick = {
//                scope.launch {
//                    db.groupDao().insert(Group(0,  "New Group"))
//                }
                showCard = true
            }
        ) {
            Text (
                text = "Click Me"
            )
        }

        for (group in groupList2) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = group.name,
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )

//                for (category in group.categoryList) {
//                    ElevatedCard(
//                        elevation = CardDefaults.cardElevation(
//                            defaultElevation = 6.dp
//                        ),
//                        modifier = Modifier
//                            .size(width = 240.dp, height = 100.dp)
//                    ) {
//                        Text(
//                            text = category.name,
//                            modifier = Modifier
//                                .padding(16.dp),
//                            textAlign = TextAlign.Center,
//                        )
//                    }
//                }
            }
        }
    }
}

@Composable
fun CreateGroupCard(modifier: Modifier = Modifier, db: AppDatabase) {
    val scope = rememberCoroutineScope()

    Box(
        Modifier.fillMaxSize().zIndex(100F),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier.height(100.dp).width(100.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        db.groupDao().insert(Group(0,  "New Group"))
                    }
                }
            ) {
                Text (
                    text = "Add Group"
                )
            }
        }
    }
}

class Category(name: String = "") {
    var name = name
}