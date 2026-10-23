package com.example.dailytasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class TaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _dark = MutableStateFlow(false)
    val dark: StateFlow<Boolean> = _dark

    private val _fontScale = MutableStateFlow(1.0f)
    val fontScale: StateFlow<Float> = _fontScale

    init { refreshDailyTasks() }

    /** 每日任务跨天自动重置 */
    fun refreshDailyTasks() {
        val today = LocalDate.now().toString()
        _tasks.value = _tasks.value.map { t ->
            if (t.isDaily && t.date != today) t.copy(done = false, date = today) else t
        }
    }

    fun addTask(title: String, isDaily: Boolean) {
        if (title.isBlank()) return
        _tasks.value = _tasks.value + Task(title = title.trim(), isDaily = isDaily)
    }

    fun toggleDone(id: Long) {
        _tasks.value = _tasks.value.map { if (it.id == id) it.copy(done = !it.done) else it }
    }

    fun deleteTask(id: Long) {
        _tasks.value = _tasks.value.filterNot { it.id == id }
    }

    fun toggleDark() { _dark.value = !_dark.value }
    fun setFontScale(s: Float) { _fontScale.value = s.coerceIn(0.8f, 1.6f) }
}
