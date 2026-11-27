package com.example.degreeplanner.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

//stores a students course rating and review
data class CourseReview(

    //course being reviewed
    var courseId: String = "",

    //anonymous firebase user id
    var userId: String = "",

    //overall course rating (0-5)
    var courseRating: Int = 0,

    //course difficulty rating (0-5)
    var difficultyRating: Int = 0,

    //assignments and workload rating (0-5)
    var assignmentsRating: Int = 0,

    //experience with the instructor (0-5)
    var instructorExperienceRating: Int = 0,

    //date and time the review was submitted
    @ServerTimestamp
    var createdAt: Date? = null
)