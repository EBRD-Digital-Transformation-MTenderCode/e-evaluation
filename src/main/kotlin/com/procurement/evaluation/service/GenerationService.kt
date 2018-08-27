package com.procurement.evaluation.service

import com.datastax.driver.core.utils.UUIDs
import org.springframework.stereotype.Service
import java.util.*

interface GenerationService {


    fun generateRandomUUID(): UUID

    fun generateTimeBasedUUID(): UUID

    fun getRandomUUID(): String

    fun getTimeBasedUUID(): String
}

@Service
class GenerationServiceImpl : GenerationService {

    override fun generateRandomUUID(): UUID {
        return UUIDs.random()
    }

    override fun generateTimeBasedUUID(): UUID {
        return UUIDs.timeBased()
    }

    override fun getRandomUUID(): String {
        return generateRandomUUID().toString()
    }

    override fun getTimeBasedUUID(): String {
        return generateTimeBasedUUID().toString()
    }
}