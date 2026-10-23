package com.example.dailytasks

import java.time.LocalDate

data class Task(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val isDaily: Boolean,          // true=每日任务, false=日程任务
    var done: Boolean = false,
    val date: String = LocalDate.now().toString() // 用于每日任务跨天判断
)
