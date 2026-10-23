package com.example.dailytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: TaskViewModel = viewModel()
            val dark by vm.dark.collectAsState()
            val fontScale by vm.fontScale.collectAsState()

            MaterialTheme(
                colorScheme = if (dark) darkColorScheme() else lightColorScheme()
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TaskScreen(vm, dark, fontScale)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(vm: TaskViewModel, dark: Boolean, fontScale: Float) {
    val tasks by vm.tasks.collectAsState()
    var input by remember { mutableStateOf("") }
    var isDailyInput by remember { mutableStateOf(true) }

    val dailyTasks = tasks.filter { it.isDaily }
    val scheduleTasks = tasks.filter { !it.isDaily }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("每日任务", fontSize = (20 * fontScale).sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { vm.toggleDark() }) {
                        Icon(if (dark) Icons.Filled.LightMode else Icons.Filled.DarkMode, "切换主题")
                    }
                    Text("A", fontSize = (14 * fontScale).sp)
                    Slider(
                        value = fontScale,
                        onValueChange = { vm.setFontScale(it) },
                        valueRange = 0.8f..1.6f,
                        modifier = Modifier.width(100.dp)
                    )
                    Text("A", fontSize = (22 * fontScale).sp)
                }
            )
        },
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("新任务…", fontSize = (14 * fontScale).sp) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                FilterChip(
                    selected = isDailyInput,
                    onClick = { isDailyInput = !isDailyInput },
                    label = { Text(if (isDailyInput) "每日" else "日程", fontSize = (12 * fontScale).sp) }
                )
                IconButton(onClick = {
                    vm.addTask(input, isDailyInput)
                    input = ""
                }) { Icon(Icons.Filled.Add, "添加") }
            }
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).padding(horizontal = 12.dp)) {
            item {
                SectionTitle("每日任务（每天自动刷新）", fontScale)
            }
            items(dailyTasks) { t ->
                TaskRow(t, fontScale, vm)
            }
            item {
                Spacer(Modifier.height(16.dp))
                SectionTitle("日程任务", fontScale)
            }
            items(scheduleTasks) { t ->
                TaskRow(t, fontScale, vm)
            }
        }
    }
}

@Composable
fun SectionTitle(text: String, fontScale: Float) {
    Text(
        text,
        fontSize = (16 * fontScale).sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TaskRow(t: Task, fontScale: Float, vm: TaskViewModel) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = t.done, onCheckedChange = { vm.toggleDone(t.id) })
        Text(
            t.title,
            fontSize = (16 * fontScale).sp,
            modifier = Modifier.weight(1f),
            fontWeight = if (t.done) FontWeight.Light else FontWeight.Normal
        )
        IconButton(onClick = { vm.deleteTask(t.id) }) {
            Icon(Icons.Filled.Delete, "删除", tint = MaterialTheme.colorScheme.error)
        }
    }
}
