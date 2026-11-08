package com.zut.chama_contribution_tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Insert
    suspend fun insert(member: Member): Long

    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): Member?
}