package com.pseteamtwo.allways.data.account.source.network

import com.pseteamtwo.allways.data.network.BaseNetworkDataSource
import kotlinx.coroutines.sync.Mutex
import java.sql.SQLException

/**
 * This class implements the [AccountNetworkDataSource]
 *
 * @constructor Creates an instance of the class.
 */
class DefaultAccountNetworkDataSource : AccountNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadAccount(email: String): NetworkAccount {
        /// Acquire lock to ensure thread safety
        accessMutex.lock()

        try {
            // Connect to the MySQL database
            val connection = createRemoteAccountConnection()

            connection.use {
                // Prepare and execute SQL statement
                val statement = connection.prepareStatement(
                    "SELECT * FROM tblaccounts WHERE email = ?")
                statement.setString(1, email)
                val resultSet = statement.executeQuery()

                // Check if any results exist
                if (resultSet.next()) {
                    // Extract data from the result set
                    val networkAccount = NetworkAccount(
                        resultSet.getString("email"),
                        resultSet.getString("pseudonym"),
                        resultSet.getString("passwordHash"),
                        resultSet.getString("passwordSalt")
                    )

                    // Close the result set before closing the statement
                    resultSet.close()
                    statement.close()
                    return networkAccount
                } else {
                    // Handle case where no account is found (return null or throw exception)
                    throw Exception("Account not found")
                }
            }

        } catch (e: SQLException) {
            // Handle errors (e.g., database connection issues, invalid email)
            throw Exception("Failed to load account", e)
        } finally {
            // Release the lock after operation
            accessMutex.unlock()
        }
    }

    override suspend fun saveAccount(account: NetworkAccount) {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // 1. Connect to the MySQL database
            val connection = createRemoteAccountConnection() // Replace with your MySQL connection logic

            connection.use {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement(
                    "INSERT INTO `allways-app-accounts`.`tblaccounts` (`email`, `pseudonym`," +
                            " `passwordHash`, `passwordSalt`) VALUES (?, ?, ?, ?);")
                statement.setString(1, account.email)
                statement.setString(2, account.pseudonym)
                statement.setString(3, account.passwordHash)
                statement.setString(4, account.passwordSalt)
                statement.executeUpdate()
                //3. Close the prepared statement
                statement.close()

                //creates a table in the data-bank for the trips with the given pseudonym
                val tripTableString = "tbl${account.pseudonym}trips"
                val sqlTripStatement = "CREATE TABLE `allways-app`.`%s` (\n" +
                        "  `id` VARCHAR(100) NOT NULL,\n" +
                        "  `stageIds` VARCHAR(200) NULL,\n" +
                        "  `purpose` VARCHAR(100) NULL,\n" +
                        "  `startDateTime` VARCHAR(100) NULL,\n" +
                        "  `endDateTime` VARCHAR(100) NULL,\n" +
                        "  `duration` INT NULL,\n" +
                        "  `distance` INT NULL,\n" +
                        "  `startLocation` VARCHAR(100) NULL,\n" +
                        "  `endLocation` VARCHAR(100) NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);"
                val createTripsStatement = connection.prepareStatement(
                    sqlTripStatement.format(tripTableString))
                createTripsStatement.executeUpdate()
                createTripsStatement.close()

                //creates a table in the data-bank for the stages with the given pseudonym
                val stageTableString = "tbl${account.pseudonym}stages"
                val sqlStagesStatement = "CREATE TABLE `allways-app`.`%s` (\n" +
                        "  `id` VARCHAR(100) NOT NULL,\n" +
                        "  `tripId` VARCHAR(100) NULL,\n" +
                        "  `mode` VARCHAR(100) NULL,\n" +
                        "  `startDateTime` VARCHAR(100) NULL,\n" +
                        "  `endDateTime` VARCHAR(100) NULL,\n" +
                        "  `duration` INT NULL,\n" +
                        "  `distance` INT NULL,\n" +
                        "  `startLocation` VARCHAR(100) NULL,\n" +
                        "  `endLocation` VARCHAR(100) NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);"
                val createStagesStatement = connection.prepareStatement(
                    sqlStagesStatement.format(stageTableString))
                createStagesStatement.executeUpdate()
                createStagesStatement.close()

                //creates a table in the data-bank for the householdQuestions with the given pseudonym
                val householdQuestionTableString = "tbl${account.pseudonym}householdquestions"
                val sqlHouseholdQuestionsStatement = "CREATE TABLE `allways-app`.`%s` (\n" +
                        "  `id` VARCHAR(100) NOT NULL,\n" +
                        "  `title` VARCHAR(100) NOT NULL,\n" +
                        "  `type` VARCHAR(100) NOT NULL,\n" +
                        "  `options` VARCHAR(200) NULL,\n" +
                        "  `answer` VARCHAR(100) NULL,\n" +
                        "  `pseudonym` VARCHAR(100) NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);"
                val createHouseholdQuestionStatement = connection.prepareStatement(
                    sqlHouseholdQuestionsStatement.format(householdQuestionTableString))
                createHouseholdQuestionStatement.executeUpdate()
                createHouseholdQuestionStatement.close()

                //creates a table in the data-bank for the profileQuestions of the user with the given pseudonym
                val profileQuestionTableString = "tbl${account.pseudonym}profilequestions"
                val sqlProfileQuestionStatement = "CREATE TABLE `allways-app`.`%s` (\n" +
                        "  `id` VARCHAR(100) NOT NULL,\n" +
                        "  `title` VARCHAR(100) NOT NULL,\n" +
                        "  `type` VARCHAR(100) NOT NULL,\n" +
                        "  `options` VARCHAR(200) NULL,\n" +
                        "  `answer` VARCHAR(100) NULL,\n" +
                        "  `pseudonym` VARCHAR(100) NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);"
                val createProfileQuestionStatement = connection.prepareStatement(
                    sqlProfileQuestionStatement.format(profileQuestionTableString))
                createProfileQuestionStatement.executeUpdate()
                createProfileQuestionStatement.close()
            }

        } catch (sqlExc: SQLException) {
            throw sqlExc
        } catch (e: SQLException) {
            // 5. Handle errors (e.g., database connection issues, duplicate entries)
            throw Exception("Failed to save account", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun deleteAccount(account: NetworkAccount) {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // 1. Connect to the MySQL database
            val accountConnection = createRemoteAccountConnection()
            val dataConnection = createRemoteDataConnection()

            accountConnection.use {
                // 2. Prepare and execute SQL statement
                val statement = accountConnection.prepareStatement(
                    "DELETE FROM `allways-app-accounts`.`tblaccounts` WHERE (`email` = ?);")
                statement.setString(1, account.email)
                statement.executeUpdate()
                //3. Close the prepared statement
                statement.close()

                //delete the tables holding the data for the account.
                dataConnection.use {
                    // 2. Prepare and execute DROP TABLE statements
                    val tripTable = "tbl${account.pseudonym}trips"
                    val stageTable = "tbl${account.pseudonym}stages"
                    val profileQuestionTable = "tbl${account.pseudonym}householdquestions"
                    val householdQuestionTable = "tbl${account.pseudonym}profilequestions"
                    val tableNames = listOf(tripTable, stageTable, profileQuestionTable,
                        householdQuestionTable)

                    for (tableName in tableNames) {
                        val tableDropStatement = dataConnection.prepareStatement("DROP TABLE `$tableName`;")
                        tableDropStatement.executeUpdate()
                    }
                }
            }

            accountConnection.close()
        } catch (e: SQLException) {
            // 5. Handle errors (e.g., database connection issues, deletion failure)
            throw Exception("Failed to delete account", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun doesEmailExist(email: String): Boolean {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // 1. Connect to the MySQL database
            val connection = createRemoteAccountConnection() // Replace with your MySQL connection logic

            connection.use {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement(
                    "SELECT 1 FROM tblaccounts WHERE email = ?")
                statement.setString(1, email)
                val resultSet = statement.executeQuery()

                // 3. Check if any results exist (before closing statement)
                val doesExist = resultSet.next()

                // 4. Close the result set (before statement)
                resultSet.close()

                // 5. Close the statement (always after result set)
                statement.close()

                return doesExist
            }

        } catch (e: SQLException) {
            // 7. Handle errors (e.g., database connection issues)
            throw Exception("Failed to check email existence", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun doesPseudonymExist(pseudonym: String): Boolean {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // 1. Connect to the MySQL database
            val connection = createRemoteAccountConnection() // Replace with your MySQL connection logic

            connection.use {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement(
                    "SELECT 1 FROM tblaccounts WHERE pseudonym = ?")
                statement.setString(1, pseudonym)
                val resultSet = statement.executeQuery()

                // 3. Check if any results exist (before closing statement)
                val doesExist = resultSet.next()

                // 4. Close the result set (before statement)
                resultSet.close()

                // 5. Close the statement (always after result set)
                statement.close()

                return doesExist
            }

        } catch (e: SQLException) {
            // 7. Handle errors (e.g., database connection issues)
            throw Exception("Failed to check pseudonym existence", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }
}