package com.pseteamtwo.allways.question.source.network

import kotlinx.coroutines.sync.Mutex

/**
 * This class implements the [QuestionnaireNetworkDataSource] and loads a HouseholdQuestionnaire
 * from the database.
 *
 * @constructor Creates an instance of the class
 */
class HouseholdQuestionnaireNetworkDataSource : QuestionnaireNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadQuestionnaire(): List<NetworkQuestion> {
        var jsonQuestionnaire = ""
        // Connect to the MySQL database
        val connection = createAccountConnection()

        connection.use {
            // Prepare and execute SQL statement to retrieve the question string
            val statement = connection.prepareStatement("SELECT * FROM `allways-app-accounts`.`tblhouseholdquestionnaire`;")
            val resultSet = statement.executeQuery()

            // Check if a result is found
            if (resultSet.next()) {
                // Extract the question string
                val questionString = resultSet.getString("jsonString") // Replace with actual column name
                jsonQuestionnaire = questionString
            } else {
                throw Exception("No Json-String found")
            }
        }
        return convertJsonToQuestions(jsonQuestionnaire)
    }
}