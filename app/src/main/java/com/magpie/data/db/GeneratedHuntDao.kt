package com.magpie.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GeneratedHuntDao {
    @Insert
    suspend fun insertHunt(hunt: GeneratedHunt): Long

    @Update
    suspend fun updateHunt(hunt: GeneratedHunt)

    @Query("SELECT * FROM generated_hunts ORDER BY createdAt DESC")
    suspend fun getAllHunts(): List<GeneratedHunt>

    @Query("SELECT * FROM generated_hunts WHERE location = :location ORDER BY createdAt DESC")
    suspend fun getHuntsByLocation(location: String): List<GeneratedHunt>

    @Query("SELECT * FROM generated_hunts WHERE isComplete = 0 ORDER BY lastPlayedAt DESC LIMIT 1")
    suspend fun getLatestIncompleteHunt(): GeneratedHunt?

    @Query("SELECT * FROM generated_hunts WHERE id = :id")
    suspend fun getHuntById(id: Int): GeneratedHunt?

    @Query("SELECT * FROM generated_hunts WHERE isComplete = 0")
    suspend fun getIncompleteHunts(): List<GeneratedHunt>

    @Query("DELETE FROM generated_hunts WHERE id = :id")
    suspend fun deleteHunt(id: Int)
}
