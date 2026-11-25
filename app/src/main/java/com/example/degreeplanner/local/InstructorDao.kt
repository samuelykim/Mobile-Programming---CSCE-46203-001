package com.example.degreeplanner.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//handles instructor database operations
@Dao
interface InstructorDao {

    //add or update list of instructors
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructors(
        instructors: List<InstructorEntity>
    )

    //get all instructors alphabetically
    @Query("SELECT * FROM instructors ORDER BY name ASC")
    fun getAllInstructors(): Flow<List<InstructorEntity>>

    //get one instructor using their id
    @Query("""
        SELECT * FROM instructors
        WHERE instructorId = :instructorId
        LIMIT 1
    """)
    suspend fun getInstructorById(
        instructorId: String
    ): InstructorEntity?

    //search instructors by name or department
    @Query("""
        SELECT * FROM instructors
        WHERE name LIKE '%' || :search || '%'
        OR department LIKE '%' || :search || '%'
        ORDER BY name ASC
    """)
    fun searchInstructors(
        search: String
    ): Flow<List<InstructorEntity>>

    //count how many instructors are stored
    @Query("SELECT COUNT(*) FROM instructors")
    suspend fun getInstructorCount(): Int
}