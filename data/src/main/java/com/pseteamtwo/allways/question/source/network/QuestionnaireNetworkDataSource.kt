package com.pseteamtwo.allways.question.source.network

import com.google.gson.JsonSyntaxException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import com.google.gson.Gson
import java.io.File
import kotlin.jvm.Throws

abstract class QuestionnaireNetworkDataSource {
    private val questionnaireFilePath = ""

    @Throws(ServerConnectionFailedException::class)
    abstract suspend fun loadQuestionnaire(): List<NetworkQuestion>

    //Throws JsonSyntaxException
    protected fun convertJsonToQuestions(jsonQuestionnaire: String): List<NetworkQuestion> {
        val file = File(questionnaireFilePath)
        var networkQuestions = listOf<NetworkQuestion>()

        val jsonString = file.readText()
        val gson = Gson()

        //Attempts to parse
        networkQuestions = gson.fromJson(jsonString, Array<NetworkQuestion>::class.java).toList()

        return networkQuestions
    }
}