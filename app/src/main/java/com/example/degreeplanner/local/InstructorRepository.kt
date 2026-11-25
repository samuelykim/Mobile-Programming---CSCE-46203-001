package com.example.degreeplanner.local

import kotlinx.coroutines.flow.Flow

//gives instructor info to other parts of app
class InstructorRepository(
    private val instructorDao: InstructorDao
) {

    //get all instructors alphabetically
    fun getAllInstructors(): Flow<List<InstructorEntity>> {
        return instructorDao.getAllInstructors()
    }

    //get one instructor by their ID
    suspend fun getInstructorById(
        instructorId: String
    ): InstructorEntity? {
        return instructorDao.getInstructorById(instructorId)
    }

    //search instructors by name or department
    fun searchInstructors(
        search: String
    ): Flow<List<InstructorEntity>> {
        return instructorDao.searchInstructors(search)
    }

    //get total number of stored instructors
    suspend fun getInstructorCount(): Int {
        return instructorDao.getInstructorCount()
    }
}