package com.example.simplesavings.composable.budget

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.simplesavings.composable.CreateCategoryForm
import com.example.simplesavings.config.database.AppDatabase
import com.example.simplesavings.model.group.Group
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BudgetScreen (
    modifier: Modifier = Modifier,
    db: AppDatabase,
    currentMonthAndYear: Instant,
    monthFormatter: DateTimeFormatter,
    yearFormatter: DateTimeFormatter
) {

    var currentYearString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(yearFormatter)

    var currentMonthString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(monthFormatter)

    val groupList by db.groupDao().getAll(currentMonthString, currentYearString).collectAsState(initial = emptyList())

    var showCard by remember { mutableStateOf( false) }
    var showCreateCategoryForm by remember { mutableStateOf( false) }
    val scope = rememberCoroutineScope()

    var currentGroupList by remember { mutableStateOf<List<Group>>(emptyList()) }
    var draggedItemId by remember { mutableStateOf<Int?>(null) }
    var initialTouchY by remember { mutableStateOf(0f) }
    var totalDragY by remember { mutableStateOf(0f) }
    val listState = rememberLazyListState()

    LaunchedEffect(groupList) {
        if (draggedItemId == null) {
            currentGroupList = groupList
        }
    }

    // Helper to check for and perform swaps
    fun checkAndPerformSwap() {
        val fingerY = initialTouchY + totalDragY
        val id = draggedItemId ?: return
        
        val currentIdx = currentGroupList.indexOfFirst { it.uid == id }
        if (currentIdx == -1) return

        // Check neighbor above
        if (currentIdx > 0) {
            val prevItem = currentGroupList[currentIdx - 1]
            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == prevItem.uid }?.let { info ->
                if (fingerY < info.offset + info.size / 2) {
                    currentGroupList = currentGroupList.toMutableList().apply {
                        add(currentIdx - 1, removeAt(currentIdx))
                    }
                    return
                }
            }
        }

        // Check neighbor below
        if (currentIdx < currentGroupList.size - 1) {
            val nextItem = currentGroupList[currentIdx + 1]
            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == nextItem.uid }?.let { info ->
                if (fingerY > info.offset + info.size / 2) {
                    currentGroupList = currentGroupList.toMutableList().apply {
                        add(currentIdx + 1, removeAt(currentIdx))
                    }
                    return
                }
            }
        }
    }

    // Auto-scroll and swap logic
    LaunchedEffect(draggedItemId) {
        while (draggedItemId != null) {
            val layoutInfo = listState.layoutInfo
            val currentFingerY = initialTouchY + totalDragY
            val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            
            val scrollThreshold = 250f
            val maxScrollSpeed = 30f

            if (currentFingerY < scrollThreshold) {
                val progress = ((scrollThreshold - currentFingerY) / scrollThreshold).coerceIn(0f, 1f)
                listState.scrollBy(-maxScrollSpeed * progress)
            } else if (currentFingerY > viewportHeight - scrollThreshold) {
                val progress = ((currentFingerY - (viewportHeight - scrollThreshold)) / scrollThreshold).coerceIn(0f, 1f)
                listState.scrollBy(maxScrollSpeed * progress)
            }
            
            // Check for swaps even if finger is stationary because list moved
            checkAndPerformSwap()
            
            delay(16)
        }
    }

    if (showCard) {
        CreateGroupCard(
            Modifier,
            db = db,
            {showCard = false},
            currentMonthString = currentMonthString,
            currentYearString = currentYearString)
    }

    if (showCreateCategoryForm) {
        CreateCategoryForm(
            Modifier,
            db = db,
            {showCreateCategoryForm = false},
            groupList,
            currentMonthString,
            currentYearString)
    }
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        SavingsSummaryCard(db, currentMonthString, currentYearString)

        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                val item = listState.layoutInfo.visibleItemsInfo.firstOrNull {
                                    offset.y.toInt() in it.offset..(it.offset + it.size)
                                }
                                val group = currentGroupList.find { it.uid == item?.key }
                                if (group != null) {
                                    draggedItemId = group.uid
                                    initialTouchY = offset.y
                                    totalDragY = 0f
                                }
                            },
                            onDragEnd = {
                                scope.launch {
                                    db.groupDao().updateAll(currentGroupList.mapIndexed { i, g ->
                                        g.copy(groupOrder = i)
                                    })
                                }
                                draggedItemId = null
                            },
                            onDragCancel = { draggedItemId = null },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragY += dragAmount.y
                                checkAndPerformSwap()
                            }
                        )
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                item { Box(modifier = Modifier.padding(top = 15.dp)) }

                if (currentGroupList.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        copyGroupsAndCategories(
                                            db,
                                            currentMonthAndYear,
                                            monthFormatter,
                                            yearFormatter
                                        )
                                    }
                                }
                            ) {
                                Text(text = "Copy Last Month's Budget")
                            }
                        }
                    }
                } else {
                    item {
                    }

                    itemsIndexed(currentGroupList, key = { _, group -> group.uid }) { _, group ->
                        val isDragging = draggedItemId == group.uid

                        GroupCard(
                            modifier = Modifier
                                .graphicsLayer {
                                    if (isDragging) {
                                        val itemInfo =
                                            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == group.uid }
                                        translationY = if (itemInfo != null) {
                                            (initialTouchY + totalDragY) - (itemInfo.offset + itemInfo.size / 2f)
                                        } else 0f
                                        scaleX = 1.05f
                                        scaleY = 1.05f
                                        alpha = 0.9f
                                    } else {
                                        translationY = 0f
                                        scaleX = 1f
                                        scaleY = 1f
                                        alpha = 1f
                                    }
                                }
                                .zIndex(if (isDragging) 1f else 0f),
                            group = group,
                            db = db,
                            showCreateCategoryForm = { showCreateCategoryForm = true }
                        )
                    }
                }
                item {
                    Box(modifier = Modifier.padding(bottom = 70.dp))
                }
            }

            Box(
                modifier = modifier
                    .align(Alignment.BottomEnd)
                    .zIndex(100F)
                    .padding(bottom = 50.dp, end = 16.dp),
            ) {
                Button(onClick = { showCard = true }) {
                    Text(text = "Add Group")
                }
            }
        }
    }
}

suspend fun copyGroupsAndCategories(
    db: AppDatabase,
    currentMonthAndYear: Instant,
    monthFormatter: DateTimeFormatter,
    yearFormatter: DateTimeFormatter
) {
    val previousMonthAndYear = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .minusMonths(1)
        .toInstant()

    val prevMonthString = previousMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(monthFormatter)

    val prevYearString = previousMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(yearFormatter)

    val currentMonthString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(monthFormatter)

    val currentYearString = currentMonthAndYear
        .atZone(ZoneId.systemDefault())
        .format(yearFormatter)

    val previousMonthGroupList = db.groupDao().getAll(prevMonthString, prevYearString).first()

    for (group in previousMonthGroupList) {
        val categoryList = db.categoryDao().getCategoriesForGroup(group.uid).first()

        val newGroup = group.copy(
            uid = 0,
            month = currentMonthString,
            year = currentYearString,
            plannedTotal = 0.0,
            spentTotal = 0.0
        )
        val insertedGroupUid = db.groupDao().insert(newGroup)

        for (category in categoryList) {
            val newCategory = category.copy(
                uid = 0,
                groupUid = insertedGroupUid.toInt(),
                spent = 0.0,
                categoryMonth = currentMonthString,
                categoryYear = currentYearString
            )
            db.categoryDao().insert(newCategory)
        }
    }
}
