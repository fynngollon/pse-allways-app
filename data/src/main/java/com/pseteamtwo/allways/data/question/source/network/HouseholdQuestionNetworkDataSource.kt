package com.pseteamtwo.allways.data.question.source.network

import com.pseteamtwo.allways.data.network.BaseNetworkDataSource
import com.pseteamtwo.allways.data.question.QuestionType
import kotlinx.coroutines.sync.Mutex

/**
 * This class implements the [QuestionNetworkDataSource]
 *
 * @constructor Creates an instance of the class
 */
class HouseholdQuestionNetworkDataSource :
    QuestionNetworkDataSource,
    BaseNetworkDataSource()
{
    private val accessMutex = Mutex()

    override suspend fun loadQuestions(pseudonym: String): List<NetworkQuestion> {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            connection.use {
                // Prepare and execute SQL statement to retrieve all questions
                val sqlLoadStatement = "SELECT * FROM `allways-app`.`%s`;"
                val statement =connection.prepareStatement(sqlLoadStatement.format(
                    "tbl${pseudonym}householdquestions"))
                val resultSet = statement.executeQuery()

                // Extract data from the result set and convert to NetworkQuestion objects
                val questions = mutableListOf<NetworkQuestion>()
                while (resultSet.next()) {
                    val optionsString = resultSet.getString("options")
                    val optionsList: List<String> = optionsString?.toList() ?: emptyList()
                    val question = NetworkQuestion(
                        resultSet.getString("id"),
                        resultSet.getString("title"),
                        QuestionType.valueOf(resultSet.getString("type")),
                        optionsList,
                        resultSet.getString("answer"),
                        resultSet.getString("pseudonym")
                    )
                    questions.add(question)
                }

                // Close the result set before closing the statement
                resultSet.close()
                statement.close()

                return questions
            }

        } catch (e: Exception) {
            // Handle database errors (e.g., connection issues)
            throw Exception("Failed to load all questions", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun saveQuestions(pseudonym: String, questions: List<NetworkQuestion>) {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            connection.use {
                // Prepare and execute SQL statement for each question
                for (question in questions) {
                    // Build the SQL statement with parameters
                    val sqlSaveStatement = "INSERT INTO `allways-app`.`%s` (`id`, `title`," +
                            " `type`, `options`, `answer`, `pseudonym`) VALUES (?, ?, ?, ?, ?, ?);"
                    val statement = connection.prepareStatement(sqlSaveStatement.format(
                        "tbl${pseudonym}householdquestions"))
                    statement.setString(1, question.id)
                    statement.setString(2, question.title)
                    statement.setString(3, question.type.toString())
                    statement.setString(4, question.options?.joinToString(
                        ","))
                    statement.setString(5, question.answer)
                    statement.setString(6, question.pseudonym)

                    // Execute the prepared statement for this question
                    statement.executeUpdate()
                    statement.close() // Close statement after each execution
                }
            }

        } catch (e: Exception) {
            // Handle database errors (e.g., connection issues, invalid data)
            throw Exception("Failed to save questions", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun deleteQuestion(pseudonym: String, id: String) {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            connection.use {
                // Prepare and execute SQL statement to delete the question
                val sqlDeleteStatement = "DELETE FROM `allways-app`.`%s` WHERE (`id` = ?);"
                val statement = connection.prepareStatement(sqlDeleteStatement.format(
                    "tbl${pseudonym}householdquestions"))
                statement.setString(1, id)
                statement.executeUpdate()
                statement.close()
            }

        } catch (e: Exception) {
            // Handle database errors (e.g., connection issues, invalid id)
            throw Exception("Failed to delete question with id: $id", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    private fun String.toList(): List<String> {
        return split(",")
    }
}