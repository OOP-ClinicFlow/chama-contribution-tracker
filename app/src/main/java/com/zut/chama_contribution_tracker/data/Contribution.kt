package com.zut.chama_contribution_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contributions")
data class Contribution(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val amount: Double,
    val date: Long
)