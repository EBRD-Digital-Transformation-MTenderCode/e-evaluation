package com.procurement.evaluation.infrastructure.fail

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Validated

sealed class Fail {

    abstract val code: String
    abstract val description: String
    val message: String
        get() = "ERROR CODE: '$code', DESCRIPTION: '$description'."

    abstract fun logging(logger: Logger)

    abstract class Error(val prefix: String) : Fail() {
        companion object {
            fun <T, E : Error> E.toResult(): Result<T, E> = Result.failure(this)
            fun <E : Error> E.toValidationResult(): Validated<E> = Validated.error(this)
        }

        override fun logging(logger: Logger) {
            logger.error(message = message)
        }
    }

    sealed class Incident(val level: Level, number: String, override val description: String) : Fail() {
        override val code: String = "INC-$number"

        override fun logging(logger: Logger) {
            when (level) {
                Level.ERROR -> logger.error(message)
                Level.WARNING -> logger.warn(message)
                Level.INFO -> logger.info(message)
            }
        }

        sealed class Database(val number: String, override val description: String) :
            Incident(level = Level.ERROR, number = number, description = description) {

            abstract val exception: Exception

            class DatabaseInteractionIncident(override val exception: Exception) : Database(
                number = "1.1",
                description = "Database incident."
            ) {
                override fun logging(logger: Logger) {
                    logger.error(message = message, exception = exception)
                }
            }

            class RecordIsNotExist(override val description: String) : Database(
                number = "1.2",
                description = description
            ) {
                override val exception: Exception
                    get() = RuntimeException(description)
            }

            class DatabaseConsistencyIncident(message: String) : Incident(
                level = Level.ERROR,
                number = "1.3",
                description = "Database consistency incident. $message"
            )
        }

        sealed class Transform(val number: String, override val description: String, val exception: Exception) :
            Incident(level = Level.ERROR, number = number, description = description) {

            override fun logging(logger: Logger) {
                logger.error(message = message, exception = exception)
            }

            class ParseFromDatabaseIncident(val jsonData: String, exception: Exception) : Transform(
                number = "2.1",
                description = "Could not parse data stored in database.",
                exception = exception
            ) {
                override fun logging(logger: Logger) {
                    logger.error(message = message, mdc = mapOf("jsonData" to jsonData), exception = exception)
                }
            }

            class Parsing(className: String, exception: Exception) :
                Transform(number = "2.2", description = "Error parsing to $className.", exception = exception)
        }

        enum class Level(override val key: String) : EnumElementProvider.Key {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}




