package com.pseteamtwo.allways.account.source.network

import com.pseteamtwo.allways.network.BaseNetworkDataSource
import kotlinx.coroutines.sync.Mutex
import java.sql.SQLException

class DefaultAccountNetworkDataSource : AccountNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadAccount(email: String): NetworkAccount {
        /// Acquire lock to ensure thread safety
        accessMutex.lock()

        try {
            // Connect to the MySQL database
            val connection = createAccountConnection()

            connection.use {
                // Prepare and execute SQL statement
                val statement = connection.prepareStatement("SELECT * FROM tblaccounts WHERE email = ?")
                statement.setString(1, email)
                val resultSet = statement.executeQuery()

                // Check if any results exist
                if (resultSet.next()) {
                    // Extract data from the result set
                    val networkAccount = NetworkAccount(
                        resultSet.getString("email"),
                        resultSet.getString("pseudonym"),
                        resultSet.getString("passwordHash"), // Avoid storing or transmitting plaintext passwords
                        resultSet.getString("passwordSalt") // Avoid storing or transmitting plaintext passwords
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

        } catch (e: Exception) {
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
            val connection = createAccountConnection() // Replace with your MySQL connection logic

            connection.use {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement(
                    "INSERT INTO `allways-app-accounts`.`tblaccounts` (`email`, `pseudonym`, `passwordHash`, `passwordSalt`) VALUES (?, ?, ?, ?);")
                statement.setString(1, account.email)
                statement.setString(2, account.pseudonym)
                statement.setString(3, account.passwordHash) // Ensure proper hashing and security best practices
                statement.setString(4, account.passwordSalt) // Ensure proper hashing and security best practices
                statement.executeUpdate()
                //3. Close the prepared statement
                statement.close()

                //creates a table in the data-bank for the trips of the user with the given pseudonym
                val tripTableString = "tbl${account.pseudonym}trips"
                val sqlTripStatement = "CREATE TABLE `allways-app`.`%s` (\n" +
                        "  `id` VARCHAR(100) NOT NULL,\n" +
                        "  `stageIds` VARCHAR(200) NULL,\n" +
                        "  `purpose` VARCHAR(100) NULL,\n" +
                        "  `startDateTime` DATETIME NULL,\n" +
                        "  `endDateTime` DATETIME NULL,\n" +
                        "  `duration` INT NULL,\n" +
                        "  `distance` INT NULL,\n" +
                        "  `startLocation` VARCHAR(100) NULL,\n" +
                        "  `endLocation` VARCHAR(100) NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);"
                val createTripsStatement = connection.prepareStatement(sqlTripStatement.format(tripTableString))
                createTripsStatement.executeUpdate()
                createTripsStatement.close()

                //creates a table in the data-bank for the stages of the user with the given pseudonym
                val stageTableString = "tbl${account.pseudonym}stages"
                val sqlStagesStatement = "CREATE TABLE `allways-app`.`%s` (\n" +
                        "  `id` VARCHAR(100) NOT NULL,\n" +
                        "  `tripId` VARCHAR(100) NULL,\n" +
                        "  `mode` VARCHAR(100) NULL,\n" +
                        "  `startDateTime` DATETIME NULL,\n" +
                        "  `endDateTime` DATETIME NULL,\n" +
                        "  `duration` INT NULL,\n" +
                        "  `distance` INT NULL,\n" +
                        "  `startLocation` VARCHAR(100) NULL,\n" +
                        "  `endLocation` VARCHAR(100) NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);"
                val createStagesStatement = connection.prepareStatement(sqlStagesStatement.format(stageTableString))
                createStagesStatement.executeUpdate()
                createStagesStatement.close()

                //creates a table in the data-bank for the householdQuestions of the user with the given pseudonym
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
                val createHouseholdQuestionStatement = connection.prepareStatement(sqlHouseholdQuestionsStatement.format(householdQuestionTableString))
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
                val createProfileQuestionStatement = connection.prepareStatement(sqlProfileQuestionStatement.format(profileQuestionTableString))
                createProfileQuestionStatement.executeUpdate()
                createProfileQuestionStatement.close()
            }

        } catch (sqlExc: SQLException) {
            throw sqlExc
        } catch (e: Exception) {
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
            val connection = createAccountConnection() // Replace with your MySQL connection logic

            connection.use {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement(
                    "CREATE TABLE `allways-app`.`?` (\n" +
                            "  `id` VARCHAR(100) NOT NULL,\n" +
                            "  `stageIds` TEXT,\n" +
                            "  `purpose` VARCHAR(100) NULL,\n" +
                            "  `startDateTime` DATETIME(6) NULL,\n" +
                            "  `endDateTime` DATETIME(6) NULL,\n" +
                            "  `duration` INT UNSIGNED NULL,\n" +
                            "  `distance` INT UNSIGNED NULL,\n" +
                            "  `startLocation` VARCHAR(100) NULL,\n" +
                            "  `endLocation` VARCHAR(100) NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ");")
                statement.setString(1, account.email)
                statement.executeUpdate()
                //3. Close the prepared statement
                statement.close()
            }
            connection.close()
        } catch (e: Exception) {
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
            val connection = createAccountConnection() // Replace with your MySQL connection logic

            connection.use {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement("SELECT 1 FROM tblaccounts WHERE email = ?")
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

        } catch (e: Exception) {
            // 7. Handle errors (e.g., database connection issues)
            throw Exception("Failed to check pseudonym existence", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun doesPseudonymExist(pseudonym: String): Boolean {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // 1. Connect to the MySQL database
            val connection = createAccountConnection() // Replace with your MySQL connection logic

            connection.use {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement("SELECT 1 FROM tblaccounts WHERE pseudonym = ?")
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

        } catch (e: Exception) {
            // 7. Handle errors (e.g., database connection issues)
            throw Exception("Failed to check pseudonym existence", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }
}