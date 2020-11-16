package com.procurement.evaluation.application.service

import com.datastax.driver.core.utils.UUIDs
import org.springframework.stereotype.Service
import java.util.*

@Service
class GenerationService {

    fun generateRandomUUID(): UUID {
        return UUIDs.random()
    }

    fun generateTimeBasedUUID(): UUID {
        return UUIDs.timeBased()
    }

    fun getRandomUUID(): String {
        return generateRandomUUID().toString()
    }

    fun getTimeBasedUUID(): String {
        return generateTimeBasedUUID().toString()
    }

    fun awardId(): UUID = UUID.randomUUID()

    fun token(): UUID = UUID.randomUUID()
}
