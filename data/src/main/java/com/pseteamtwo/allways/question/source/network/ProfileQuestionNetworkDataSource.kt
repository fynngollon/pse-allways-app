package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.network.BaseNetworkDataSource
import com.pseteamtwo.allways.question.QuestionType
import kotlinx.coroutines.sync.Mutex

class ProfileQuestionNetworkDataSource : QuestionNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadQuestions(pseudonym: String): List<NetworkQuestion> {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            try {
                // Prepare and execute SQL statement to retrieve all questions
                val statement = connection.prepareStatement("SELECT * FROM ?")
                statement.setString(1, "tbl${pseudonym}profilequestions")
                val resultSet = statement.executeQuery()

                // Extract data from the result set and convert to NetworkQuestion objects
                val questions = mutableListOf<NetworkQuestion>()
                while (resultSet.next()) {
                    val question = NetworkQuestion(
                        resultSet.getString("id"),
                        resultSet.getString("title"),
                        QuestionType.valueOf(resultSet.getString("type")),
                        (resultSet.getString("options").toList()),
                        resultSet.getString("answer"), // Handle securely if necessary
                        resultSet.getString("pseudonym")
                    )
                    questions.add(question)
                }

                // Close the result set before closing the statement
                resultSet.close()
                statement.close()

                return questions
            } finally {
                // Close the connection
                connection.close()
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

            try {
                // Prepare and execute SQL statement for each question
                for (question in questions) {
                    // Build the SQL statement with parameters
                    val statement = connection.prepareStatement("INSERT INTO ? (id, title, type, options, answer, pseudonym) VALUES (?, ?, ?, ?, ?, ?)")
                    statement.setString(1, "tbl${pseudonym}profilequestions")
                    statement.setString(2, question.id)
                    statement.setString(3, question.title)
                    statement.setString(4, question.type.toString())
                    statement.setString(5, question.options?.joinToString(","))
                    statement.setString(6, question.answer)
                    statement.setString(7, question.pseudonym)

                    // Execute the prepared statement for this question
                    statement.executeUpdate()
                    statement.close() // Close statement after each execution
                }

            } finally {
                // Close the connection
                connection.close()
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

            try {
                // Prepare and execute SQL statement to delete the question
                val statement = connection.prepareStatement("DELETE FROM ? WHERE id = ?")
                statement.setString(1, "tbl${pseudonym}profilequestions")
                statement.setString(2, id)
                statement.close()
            } finally {
                // Close the connection
                connection.close()
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