package com.zut.chama_contribution_tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDao {

    @Insert
    suspend fun insert(contribution: Contribution): Long

    @Query("SELECT * FROM contributions ORDER BY date DESC")
    fun getAllContributions(): Flow<List<Contribution>>

    @Query("SELECT * FROM contributions WHERE memberId = :memberId ORDER BY date DESC")
    fun getContributionsByMember(memberId: Long): Flow<List<Contribution>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM contributions WHERE memberId = :memberId")
    suspend fun getTotalForMember(memberId: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM contributions")
    suspend fun getGroupTotal(): Double
}