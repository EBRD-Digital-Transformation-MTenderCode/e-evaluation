package com.procurement.evaluation.application.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result

interface Transform {

    /**
     * Parsing
     */
    fun tryParse(value: String): Result<JsonNode, Failure.Incident.Transform.Parsing>

    /**
     * Mapping
     */
    fun <R> tryMapping(value: JsonNode, target: Class<R>): Result<R, Failure.Incident.Transform.Mapping>
    fun <R> tryMapping(value: JsonNode, typeRef: TypeReference<R>): Result<R, Failure.Incident.Transform.Mapping>

    /**
     * Deserialization
     */
    fun <R> tryDeserialization(value: String, target: Class<R>): Result<R, Failure.Incident.Transform.Deserialization>
    fun <R> tryDeserialization(
        value: String,
        typeRef: TypeReference<R>
    ): Result<R, Failure.Incident.Transform.Deserialization>

    /**
     * Serialization
     */
    fun <R> trySerialization(value: R): Result<String, Failure.Incident.Transform.Serialization>

    /**
     * ???
     */
    fun tryToJson(value: JsonNode): Result<String, Failure.Incident.Transform.Serialization>
}

inline fun <reified T> String.tryDeserialization(transform: Transform) =
    transform.tryDeserialization(this, T::class.java)