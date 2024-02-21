package com.pseteamtwo.allways.account.source.network

import com.pseteamtwo.allways.network.BaseNetworkDataSource
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlinx.coroutines.sync.Mutex

class DefaultAccountNetworkDataSource : AccountNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadAccount(email: String): NetworkAccount {
        /*accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // 1. Check if email already exists (optimization)
            if (doesEmailExist(email)) {
                // Perform search by email if available on the platform
                return performSearchByEmail(email) // Replace with actual network interaction
            } else {
                throw Exception("Account not found")
            }
        } catch (e: ServerConnectionFailedException) {
            // Handle network connection error (throw exception, retry, etc.)
            throw Exception("Network connection failed", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }*/
        TODO("Not finished")
    }

    override suspend fun saveAccount(account: NetworkAccount) {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // 1. Connect to the MySQL database
            val connection = createConnection() // Replace with your MySQL connection logic

            try {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement("INSERT INTO tblaccounts (email, pseudonym, password_hash, password_salt) VALUES (?, ?, ?, ?)")
                statement.setString(1, account.email)
                statement.setString(2, account.pseudonym)
                statement.setString(3, account.passwordHash) // Ensure proper hashing and security best practices
                statement.setString(4, account.passwordSalt) // Ensure proper hashing and security best practices
                statement.executeUpdate()

                //3. Close the prepared statement
                statement.close()
            } finally {

                // 4. Close the prepared connection
                connection.close()
            }

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
            val connection = createConnection() // Replace with your MySQL connection logic

            try {
                // 2. Prepare and execute SQL statement
                val statement = connection.prepareStatement("DELETE FROM tblaccounts WHERE email = ?")
                statement.setString(1, account.email)
                statement.executeUpdate()
                //3. Close the prepared statement
                statement.close()
            } finally {
                // 4. Close the prepared connection
                connection.close()
            }

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
            val connection = createConnection() // Replace with your MySQL connection logic

            try {
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

            } finally {
                // 6. Close the connection (after statement)
                connection.close()
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
            val connection = createConnection() // Replace with your MySQL connection logic

            try {
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

            } finally {
                // 6. Close the connection (after statement)
                connection.close()
            }

        } catch (e: Exception) {
            // 7. Handle errors (e.g., database connection issues)
            throw Exception("Failed to check pseudonym existence", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }
}