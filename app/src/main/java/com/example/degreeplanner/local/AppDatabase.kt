package com.example.degreeplanner.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//main database for the planner
@Database(
    entities = [
        CourseEntity::class,
        InstructorEntity::class,
        CourseInstructorCrossRef::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(CourseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    //get course information
    abstract fun courseDao(): CourseDao

    //get instructor information
    abstract fun instructorDao(): InstructorDao

    //get relationships between courses and instructors
    abstract fun courseInstructorDao(): CourseInstructorDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        //version 1 to 2: add instructors
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS instructors (
                        instructorId TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        department TEXT NOT NULL,
                        email TEXT,
                        office TEXT
                    )
                    """.trimIndent()
                )
            }
        }

        //version 2 to 3: connect courses and instructors
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {

                //create relationship table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS course_instructors (
                        courseId TEXT NOT NULL,
                        instructorId TEXT NOT NULL,
                        PRIMARY KEY(courseId, instructorId),
                        FOREIGN KEY(courseId)
                            REFERENCES courses(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE,
                        FOREIGN KEY(instructorId)
                            REFERENCES instructors(instructorId)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                //speed up searches by instructor ID
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_course_instructors_instructorId
                    ON course_instructors(instructorId)
                    """.trimIndent()
                )
            }
        }

        //get or create the database
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cs_planner_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}