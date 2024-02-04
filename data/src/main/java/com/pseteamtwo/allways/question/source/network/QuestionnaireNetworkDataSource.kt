package com.pseteamtwo.allways.question.source.network

import com.google.gson.JsonSyntaxException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.pseteamtwo.allways.exception.IncorrectJsonFileException
import com.pseteamtwo.allways.question.QuestionType
import java.io.File
import java.lang.reflect.Type
import java.util.Locale
import kotlin.jvm.Throws

abstract class QuestionnaireNetworkDataSource {
    private val questionnaireFilePath = ""

    @Throws(ServerConnectionFailedException::class)
    abstract suspend fun loadQuestionnaire(): List<NetworkQuestion>

    //Throws JsonSyntaxException
    protected fun convertJsonToQuestions(jsonQuestionnaire: String): List<NetworkQuestion> {
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

class QuestionTypeDeserializer : JsonDeserializer<QuestionType> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): QuestionType {
        return QuestionType.valueOf(json?.asString?.uppercase(Locale.ROOT) ?: "TEXT")
    }
}