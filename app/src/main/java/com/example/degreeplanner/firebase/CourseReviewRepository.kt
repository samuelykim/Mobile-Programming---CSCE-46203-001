package com.example.degreeplanner.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//saves and get course ratings using firebase
class CourseReviewRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //save students ratings for one course
    suspend fun saveRating(
        courseId: String,
        courseRating: Int,
        difficultyRating: Int,
        assignmentsRating: Int,
        instructorExperienceRating: Int
    ) {
        require(courseId.isNotBlank()) {
            "Course ID cannot be empty"
        }

        //all four ratings must be between 0 and 5
        val ratings = listOf(
            courseRating,
            difficultyRating,
            assignmentsRating,
            instructorExperienceRating
        )

        require(ratings.all { it in 0..5 }) {
            "Ratings must be between 0 and 5"
        }

        // Student must already be signed into Firebase
        val userId = auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Student is not signed in"
            )

        val review = CourseReview(
            courseId = courseId,
            userId = userId,
            courseRating = courseRating,
            difficultyRating = difficultyRating,
            assignmentsRating = assignmentsRating,
            instructorExperienceRating = instructorExperienceRating
        )

        //store ratings under selected course
        suspendCancellableCoroutine<Unit> { continuation ->

            firestore.collection("courseReviews")
                .document(courseId)
                .collection("reviews")
                .document(userId)
                .set(review)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
        }
    }

    //get ratings for specific course
    suspend fun getRatingsForCourse(
        courseId: String
    ): List<CourseReview> {

        require(courseId.isNotBlank()) {
            "Course ID cannot be empty"
        }

        return suspendCancellableCoroutine { continuation ->

            firestore.collection("courseReviews")
                .document(courseId)
                .collection("reviews")
                .get()
                .addOnSuccessListener { snapshot ->

                    if (continuation.isActive) {
                        try {
                            val reviews = snapshot.documents.mapNotNull {
                                it.toObject(CourseReview::class.java)
                            }

                            continuation.resume(reviews)

                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
        }
    }
    //calc average ratings for a specific course
    suspend fun getRatingSummary(
        courseId: String
    ): CourseRatingSummary {

        //get all ratings for this course
        val ratings = getRatingsForCourse(courseId)

        //handle courses that have no ratings yet
        if (ratings.isEmpty()) {
            return CourseRatingSummary()
        }

        //calc average for each category
        return CourseRatingSummary(
            ratingCount = ratings.size,

            courseAverage = ratings
                .map { it.courseRating }
                .average(),

            difficultyAverage = ratings
                .map { it.difficultyRating }
                .average(),

            assignmentsAverage = ratings
                .map { it.assignmentsRating }
                .average(),

            instructorExperienceAverage = ratings
                .map { it.instructorExperienceRating }
                .average()
        )
    }
}

//holds the average ratings for one course
data class CourseRatingSummary(
    val ratingCount: Int = 0,
    val courseAverage: Double = 0.0,
    val difficultyAverage: Double = 0.0,
    val assignmentsAverage: Double = 0.0,
    val instructorExperienceAverage: Double = 0.0
)