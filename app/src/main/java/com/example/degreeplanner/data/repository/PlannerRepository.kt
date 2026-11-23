package com.example.degreeplanner.data.repository

import android.content.Context
import com.example.degreeplanner.data.model.Course
import com.example.degreeplanner.data.model.CourseFeedbackItem
import com.example.degreeplanner.data.model.DegreeOption
import com.example.degreeplanner.data.model.FullDegreePlan
import com.example.degreeplanner.data.model.SemesterCourseItem
import com.example.degreeplanner.data.model.SemesterPlan
import com.example.degreeplanner.data.model.StudentPreferences
import org.json.JSONArray
import org.json.JSONObject

private data class CoursePlacement(
    val courseId: String,
    val semesterNumber: Int
)

private data class ElectiveSlotData(
    val requirementId: String,
    val name: String,
    val semesterNumber: Int,
    val creditHours: Int
)

private data class TemplateData(
    val degreeId: String,
    val degreeName: String,
    val placements: List<CoursePlacement>,
    val electives: List<ElectiveSlotData>,
    val totalCreditHours: Int
)

class PlannerRepository(private val context: Context) {

    private val sampleInstructorsMap = mapOf(
        "CSCE 20004" to listOf("Dr. Susan Gauch", "Prof. Dale Thompson"),
        "CSCE 21104" to listOf("Dr. Wing Ning Li"),
        "CSCE 20104" to listOf("Dr. John Gauch", "Dr. Michael Gashler"),
        "CSCE 22104" to listOf("Dr. Wing Ning Li"),
        "CSCE 31903" to listOf("Dr. Michael Gashler"),
        "CSCE 36103" to listOf("Prof. Dale Thompson"),
        "CSCE 35103" to listOf("Dr. Susan Gauch"),
        "CSCE 45203" to listOf("Dr. Xintao Wu"),
        "CSCE 41303" to listOf("Dr. Wing Ning Li"),
        "CSCE 47503" to listOf("Prof. Dale Thompson"),
        "CSCE 44303" to listOf("Dr. Xintao Wu"),
        "CSCE 46103" to listOf("Dr. Michael Gashler"),
        "CSCE 46203" to listOf("Dr. John Gauch"),
        "CSCE 47803" to listOf("Prof. Dale Thompson"),
        "CSCE 48503" to listOf("Dr. Xintao Wu"),
        "MATH 24004" to listOf("Dr. Matthew Clay", "Dr. Mark Johnson"),
        "MATH 25004" to listOf("Dr. Matthew Clay"),
        "PHYS 20304" to listOf("Dr. Reeta Vyas"),
        "ENGL 10103" to listOf("Prof. Sarah Miller")
    )

    private val defaultInstructors = listOf("Dr. John Gauch", "Prof. Department Faculty")

    private val sampleSchedulesMap = mapOf(
        "CSCE 20004" to listOf(
            "Fall 2025: Mon/Wed/Fri 10:45 AM – 11:35 AM (JBHT 0216)",
            "Spring 2026: Tue/Thu 11:00 AM – 12:15 PM (BELL 2286)"
        ),
        "CSCE 20104" to listOf(
            "Fall 2025: Mon/Wed/Fri 11:50 AM – 12:40 PM (JBHT 0216)",
            "Spring 2026: Mon/Wed/Fri 09:40 AM – 10:30 AM (JBHT 0147)"
        ),
        "CSCE 31903" to listOf(
            "Fall 2025: Tue/Thu 02:00 PM – 03:15 PM (JBHT 0224)",
            "Spring 2026: Tue/Thu 09:30 AM – 10:45 AM (JBHT 0224)"
        ),
        "CSCE 36103" to listOf(
            "Fall 2025: Mon/Wed 03:05 PM – 04:20 PM (BELL 2286)"
        ),
        "CSCE 45203" to listOf(
            "Spring 2026: Tue/Thu 12:30 PM – 01:45 PM (JBHT 0216)"
        )
    )

    private val defaultSchedules = listOf(
        "Fall 2025: Mon/Wed/Fri 10:45 AM – 11:35 AM (JBHT Hall)",
        "Spring 2026: Tue/Thu 11:00 AM – 12:15 PM (BELL Engineering)"
    )

    private val sampleFeedbackMap = mapOf(
        "CSCE 20004" to listOf(
            CourseFeedbackItem("Alex M.", "Fall 2025", 5.0f, "Great introduction to C++ programming! Projects were fun and rewarding."),
            CourseFeedbackItem("Jordan P.", "Spring 2025", 4.5f, "Challenging labs, but teaching assistants and professor offer great office hours support.")
        ),
        "CSCE 20104" to listOf(
            CourseFeedbackItem("Taylor S.", "Fall 2025", 4.8f, "Deep dive into data structures and OOP in C++. Essential for technical interviews."),
            CourseFeedbackItem("Morgan K.", "Spring 2025", 4.0f, "Heavy coding workload, start projects early!")
        ),
        "CSCE 31903" to listOf(
            CourseFeedbackItem("Chris B.", "Fall 2025", 4.5f, "Learned functional and concurrent programming paradigms. Excellent course."),
            CourseFeedbackItem("Sam H.", "Spring 2025", 4.2f, "Very useful coverage of modern programming concepts.")
        ),
        "CSCE 36103" to listOf(
            CourseFeedbackItem("David L.", "Fall 2025", 4.7f, "Fascinating concepts in OS kernel design, process synchronization, and memory management.")
        ),
        "CSCE 45203" to listOf(
            CourseFeedbackItem("Emma W.", "Spring 2025", 5.0f, "Loved the SQL and relational database design projects!")
        )
    )

    fun loadInstructors(): List<com.example.degreeplanner.data.model.Instructor> {
        val courses = loadCourses()
        val instructorCoursesMap = mutableMapOf<String, MutableList<String>>()

        courses.forEach { course ->
            course.instructors.forEach { inst ->
                instructorCoursesMap.getOrPut(inst) { mutableListOf() }.add("${course.id}: ${course.name}")
            }
        }

        return instructorCoursesMap.entries.mapIndexed { index, entry ->
            com.example.degreeplanner.data.model.Instructor(
                id = "inst_$index",
                name = entry.key,
                title = "Faculty of Computer Science & Engineering",
                email = "${entry.key.lowercase().replace(" ", ".").replace("dr.", "").replace("prof.", "")}@uark.edu",
                taughtCourses = entry.value.distinct()
            )
        }.sortedBy { it.name }
    }

    fun loadDegreeOptions(): List<DegreeOption> {
        val optionsList = mutableListOf<DegreeOption>()
        try {
            val jsonString = context.assets.open("CS_Degree_Planner_Template.json")
                .bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val degreeOptionsArray = jsonObject.optJSONArray("degreeOptions") ?: JSONArray()
            for (i in 0 until degreeOptionsArray.length()) {
                val item = degreeOptionsArray.getJSONObject(i)
                optionsList.add(
                    DegreeOption(
                        degreeId = item.optString("degreeId"),
                        name = item.optString("name"),
                        planStatus = item.optString("planStatus"),
                        totalCreditHours = item.optInt("totalCreditHours", 120),
                        sourceUrl = item.optString("sourceUrl")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return listOf(
                DegreeOption("BSCS_2026_27", "Computer Science B.S."),
                DegreeOption("BSCS_CYBR_2026_27", "Computer Science B.S. with Cybersecurity Concentration"),
                DegreeOption("BACS_2026_27", "Computer Science B.A."),
                DegreeOption("BSCMPE_2026_27", "Computer Engineering B.S.Cmp.E."),
                DegreeOption("BSCMPE_CYBR_2026_27", "Computer Engineering B.S.Cmp.E. with Cybersecurity Concentration")
            )
        }
        return optionsList
    }

    fun loadInterestOptions(): List<String> {
        val interests = mutableListOf<String>()
        try {
            val jsonString = context.assets.open("CS_Degree_Planner_Template.json")
                .bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val interestArray = jsonObject.optJSONArray("interestOptions") ?: JSONArray()
            for (i in 0 until interestArray.length()) {
                interests.add(interestArray.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return listOf(
                "Cybersecurity",
                "Artificial Intelligence",
                "Software Engineering",
                "Data Science",
                "Networking / Cloud",
                "Systems / Programming"
            )
        }
        return interests
    }

    fun loadCourses(): List<Course> {
        val courses = mutableListOf<Course>()
        try {
            val jsonString = context.assets.open("CS_Degree_Planner_Courses.json")
                .bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val courseId = item.optString("id")

                val prereqArray = item.optJSONArray("prerequisiteCourseIds") ?: JSONArray()
                val prereqList = mutableListOf<String>()
                for (j in 0 until prereqArray.length()) {
                    prereqList.add(prereqArray.getString(j))
                }
                val termsArray = item.optJSONArray("typicalTerms") ?: JSONArray()
                val termsList = mutableListOf<String>()
                for (j in 0 until termsArray.length()) {
                    termsList.add(termsArray.getString(j))
                }
                val tagsArray = item.optJSONArray("interestTags") ?: JSONArray()
                val tagsList = mutableListOf<String>()
                for (j in 0 until tagsArray.length()) {
                    tagsList.add(tagsArray.getString(j))
                }

                val instructors = sampleInstructorsMap[courseId] ?: defaultInstructors
                val schedules = sampleSchedulesMap[courseId] ?: defaultSchedules
                val feedback = sampleFeedbackMap[courseId] ?: listOf(
                    CourseFeedbackItem("Student User", "Fall 2025", 4.5f, "Solid course with clear course objectives and practical assignments.")
                )

                courses.add(
                    Course(
                        id = courseId,
                        name = item.optString("name"),
                        creditHours = item.optInt("creditHours", 3),
                        description = item.optString("description"),
                        prerequisiteText = item.optString("prerequisiteText"),
                        prerequisiteCourseIds = prereqList,
                        prerequisiteLogicComplete = item.optBoolean("prerequisiteLogicComplete", false),
                        typicalTerms = termsList,
                        interestTags = tagsList,
                        instructors = instructors,
                        previousSchedules = schedules,
                        averageRating = 4.6f,
                        difficultyRating = 3.5f,
                        studentFeedback = feedback
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return courses
    }

    fun getCourseById(courseId: String): Course? {
        return loadCourses().find { it.id.equals(courseId, ignoreCase = true) }
    }

    private fun loadTemplateDataForDegree(targetDegreeId: String): TemplateData {
        try {
            val jsonString = context.assets.open("CS_Degree_Planner_Template.json")
                .bufferedReader().use { it.readText() }
            val rootObject = JSONObject(jsonString)

            val templates = mutableListOf<JSONObject>()
            templates.add(rootObject)

            val additionalTemplates = rootObject.optJSONArray("additionalDegreeTemplates") ?: JSONArray()
            for (i in 0 until additionalTemplates.length()) {
                templates.add(additionalTemplates.getJSONObject(i))
            }

            val matchingObj = templates.find { it.optString("degreeId") == targetDegreeId } ?: rootObject

            val degreeId = matchingObj.optString("degreeId", targetDegreeId)
            val degreeName = matchingObj.optString("degreeName", "Computer Science Program")
            val totalCreditHours = matchingObj.optInt("totalCreditHours", 120)

            val placementsArray = matchingObj.optJSONArray("coursePlacements") ?: JSONArray()
            val placements = mutableListOf<CoursePlacement>()
            for (i in 0 until placementsArray.length()) {
                val p = placementsArray.getJSONObject(i)
                placements.add(
                    CoursePlacement(
                        courseId = p.getString("courseId"),
                        semesterNumber = p.getInt("semesterNumber")
                    )
                )
            }

            val electivesArray = matchingObj.optJSONArray("electiveSlots") ?: JSONArray()
            val electives = mutableListOf<ElectiveSlotData>()
            for (i in 0 until electivesArray.length()) {
                val e = electivesArray.getJSONObject(i)
                electives.add(
                    ElectiveSlotData(
                        requirementId = e.optString("requirementId", "ELECTIVE_$i"),
                        name = e.optString("name", "Elective Slot"),
                        semesterNumber = e.getInt("semesterNumber"),
                        creditHours = e.optInt("creditHours", 3)
                    )
                )
            }

            return TemplateData(degreeId, degreeName, placements, electives, totalCreditHours)
        } catch (e: Exception) {
            e.printStackTrace()
            return TemplateData(targetDegreeId, "Computer Science B.S.", emptyList(), emptyList(), 122)
        }
    }

    fun generateDegreePlan(preferences: StudentPreferences): FullDegreePlan {
        val coursesMap = loadCourses().associateBy { it.id }
        val templateData = loadTemplateDataForDegree(preferences.degreeId)

        val semesterNames = listOf(
            "Semester 1 (Freshman Fall)",
            "Semester 2 (Freshman Spring)",
            "Semester 3 (Sophomore Fall)",
            "Semester 4 (Sophomore Spring)",
            "Semester 5 (Junior Fall)",
            "Semester 6 (Junior Spring)",
            "Semester 7 (Senior Fall)",
            "Semester 8 (Senior Spring)"
        )

        val semesters = (1..8).map { semNum ->
            val semCourses = mutableListOf<SemesterCourseItem>()

            templateData.placements.filter { it.semesterNumber == semNum }.forEach { placement ->
                val course = coursesMap[placement.courseId]
                if (course != null) {
                    semCourses.add(
                        SemesterCourseItem(
                            courseId = course.id,
                            courseName = course.name,
                            creditHours = course.creditHours,
                            isElective = false,
                            description = course.description
                        )
                    )
                } else {
                    semCourses.add(
                        SemesterCourseItem(
                            courseId = placement.courseId,
                            courseName = placement.courseId,
                            creditHours = 3,
                            isElective = false
                        )
                    )
                }
            }

            templateData.electives.filter { it.semesterNumber == semNum }.forEach { slot ->
                semCourses.add(
                    SemesterCourseItem(
                        courseId = slot.requirementId,
                        courseName = slot.name,
                        creditHours = slot.creditHours,
                        isElective = true
                    )
                )
            }

            val semTotalCredits = semCourses.sumOf { it.creditHours }
            SemesterPlan(
                semesterNumber = semNum,
                semesterName = semesterNames.getOrElse(semNum - 1) { "Semester $semNum" },
                courses = semCourses,
                totalCreditHours = semTotalCredits
            )
        }

        val explanation = buildExplanation(preferences, templateData.degreeName, templateData.totalCreditHours)

        return FullDegreePlan(
            degreeId = templateData.degreeId,
            degreeName = templateData.degreeName,
            totalDegreeCredits = templateData.totalCreditHours,
            semesters = semesters,
            recommendationExplanation = explanation
        )
    }

    private fun buildExplanation(
        preferences: StudentPreferences,
        degreeName: String,
        totalCredits: Int
    ): String {
        val interestsText = if (preferences.selectedInterests.isNotEmpty()) {
            "your selected focus areas in ${preferences.selectedInterests.joinToString(", ")}"
        } else {
            "a comprehensive computer science focus"
        }

        val uarkText = if (preferences.attendsUark) {
            "tailored for University of Arkansas (UARK) catalog standards"
        } else {
            "based on standard transferable computer science degree requirements"
        }

        return "We recommend the $degreeName program ($totalCredits total credit hours) $uarkText. " +
                "This eight-semester schedule integrates $interestsText with your preferred ${preferences.workloadPreference.label.lowercase()} " +
                "(${preferences.targetCreditHoursPerSemester} target credit hours/semester) to ensure balanced workload distribution and timely prerequisite completion."
    }
}
