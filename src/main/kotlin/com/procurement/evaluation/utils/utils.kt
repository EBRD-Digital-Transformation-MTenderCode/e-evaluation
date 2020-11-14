package com.procurement.evaluation.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.evaluation.infrastructure.bind.configuration
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

private object JsonMapper {

    val mapper: ObjectMapper = ObjectMapper().apply { configuration() }

    var dateTimeFormatter: DateTimeFormatter

    init {
        dateTimeFormatter = DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendLiteral('Z')
                .toFormatter()
    }
}

/*Date utils*/

fun String.toLocal(): LocalDateTime {
    return LocalDateTime.parse(this, JsonMapper.dateTimeFormatter)
}

fun LocalDateTime.toDate(): Date {
    return Date.from(this.toInstant(ZoneOffset.UTC))
}

fun Date.toLocal(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneOffset.UTC)
}

fun localNowUTC(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
}

fun milliNowUTC(): Long {
    return localNowUTC().toInstant(ZoneOffset.UTC).toEpochMilli()
}

/*Json utils*/
fun <Any> toJson(obj: Any): String {
    try {
        return JsonMapper.mapper.writeValueAsString(obj)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

fun <T> toObject(clazz: Class<T>, json: String): T {
    try {
        return JsonMapper.mapper.readValue(json, clazz)
    } catch (e: IOException) {
        throw IllegalArgumentException(e)
    }
}


fun <T> toObject(clazz: Class<T>, json: JsonNode): T {
    try {
        return JsonMapper.mapper.treeToValue(json, clazz)
    } catch (e: IOException) {
        throw IllegalArgumentException(e)
    }
}

fun <T : Any> JsonNode.tryToObject(target: Class<T>): Result<T, Fail.Incident.Transform.Parsing> = try {
    Result.success(JsonMapper.mapper.treeToValue(this, target))
} catch (expected: Exception) {
    Result.failure(Fail.Incident.Transform.Parsing(target.canonicalName, expected))
}

fun <T : Any> String.tryToObject(target: Class<T>): Result<T, Fail.Incident.Transform.Parsing> = try {
    Result.success(JsonMapper.mapper.readValue(this, target))
} catch (expected: Exception) {
    Result.failure(Fail.Incident.Transform.Parsing(target.canonicalName, expected))
}

fun String.tryToNode(): Result<JsonNode, Fail.Incident.Transform.Parsing> = try {
    Result.success(JsonMapper.mapper.readTree(this))
} catch (exception: JsonProcessingException) {
    Result.failure(Fail.Incident.Transform.Parsing(JsonNode::class.java.canonicalName, exception))
}

/*Collection*/
fun <T> Collection<T>.containsAny(dest: Collection<T>): Boolean {
    for (value in dest) {
        if (this.contains(value)) {
            return true
        }
    }
    return false
}