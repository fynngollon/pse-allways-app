package com.pseteamtwo.allways.data.question.source.network

import com.pseteamtwo.allways.data.exception.IncorrectJsonFileException
import com.pseteamtwo.allways.data.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.data.network.BaseNetworkDataSource
import com.pseteamtwo.allways.data.question.QuestionType
import kotlinx.serialization.json.Json

/**
 * This class handles all network interactions for the questionnaires and converts the Json-file
 * that is stored in the database into NetworkQuestions.
 *
 * @constructor Create empty Questionnaire network data source
 */
abstract class QuestionnaireNetworkDataSource: BaseNetworkDataSource() {
    private val questionnaireFilePath = ""

    /**
     * Load questionnaire searches in the database for the questionnaire Json file and uses the
     * convertJsonToQuestions method to give back the list of NetworkQuestions.
     * It creates a connection to a MySql-server and executes the load-sql-statement.
     * The given string is then converted and the NetworkQuestions get returned.
     *
     * @return List of NetworkQuestions from the JsonFile.
     */
    @Throws(ServerConnectionFailedException::class)
    abstract suspend fun loadQuestionnaire(): List<NetworkQuestion>

    //Throws JsonSyntaxException
    /*protected fun convertJsonToQuestions(jsonQuestionnaire: String): List<NetworkQuestion> {
        val file = File(questionnaireFilePath)
        assert(file.exists() && file.isFile) { "The file is not valid or does not exist." }

        var networkQuestions = listOf<NetworkQuestion>()
        val jsonString = file.readText()
        val gson = GsonBuilder()
            .registerTypeAdapter(QuestionType::class.java, QuestionTypeDeserializer())
            .create()

        //Attempts to parse
        networkQuestions = gson.fromJson(jsonString, Array<NetworkQuestion>::class.java).toList()

        if (!areAllNetworkQuestionsCorrect(networkQuestions)) {
            throw IncorrectJsonFileException()
        }

        return networkQuestions
    }*/

    /**
     * Convert json to questions converts a Json-file into a list of NetworkQuestions by using a
     * decoder from the kotlinx-serialization library.
     *
     * @param jsonQuestionnaire Is the Json-file string.
     * @return Is the List of NetworkQuestions stored in the Json-file
     */
    @Throws(IncorrectJsonFileException::class)
    protected fun convertJsonToQuestions(jsonQuestionnaire: String): List<NetworkQuestion> {

        val format = Json {
            ignoreUnknownKeys = true // Add this line to ignore unknown keys if needed
        }

        // Attempt to parse
        val networkQuestions = try {
            format.decodeFromString<List<NetworkQuestion>>(jsonQuestionnaire)
        } catch (e: Exception) {
            throw IncorrectJsonFileException()
        }
        if (!areAllNetworkQuestionsCorrect(networkQuestions)) {
            throw IncorrectJsonFileException()
        }
        return networkQuestions
    }

    private fun areAllNetworkQuestionsCorrect(networkQuestions: List<NetworkQuestion>): Boolean {
        return networkQuestions.all { question ->
            if (question.answer != null || question.pseudonym != null) {
                return false
            }
            when (question.type) {
                QuestionType.TEXT -> {
                    // Ensure options are null for TEXT:
                    question.options == null
                }
                QuestionType.SPINNER, QuestionType.CHECKBOX, QuestionType.RADIO_BUTTON -> {
                    // Validate choices only if options are not null:
                    question.options != null
                }
                else -> {
                    false
                }
            }
        }
    }
}

/*class QuestionTypeDeserializer : JsonDeserializer<QuestionType> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): QuestionType {
        return QuestionType.valueOf(json?.asString?.uppercase(Locale.ROOT) ?: "TEXT")
    }*/