package com.procurement.evaluation.infrastructure.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import com.procurement.evaluation.domain.model.enums.EnumElementProvider.Companion.keysAsStrings
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.Result.Companion.success
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.functional.flatMap
import java.math.BigDecimal

fun JsonNode.getOrNull(name: String): JsonNode? = if (this.has(name)) this.get(name) else null

fun JsonNode.tryGetAttribute(name: String): Result<JsonNode, DataErrors.Validation> {
    val node = get(name)
        ?: return failure(
            DataErrors.Validation.MissingRequiredAttribute(name)
        )
    if (node is NullNode)
        return failure(
            DataErrors.Validation.DataTypeMismatch(
                name = name, actualType = "null", expectedType = "not null"
            )
        )

    return success(node)
}

fun JsonNode.tryGetAttribute(name: String, type: JsonNodeType): Result<JsonNode, DataErrors.Validation> =
    tryGetAttribute(name = name)
        .flatMap { node ->
            if (node.nodeType == type)
                success(node)
            else
                failure(
                    DataErrors.Validation.DataTypeMismatch(
                        name = name,
                        expectedType = type.name,
                        actualType = node.nodeType.name
                    )
                )
        }

fun JsonNode.tryGetTextAttribute(name: String): Result<String, DataErrors.Validation> =
    tryGetAttribute(name = name, type = JsonNodeType.STRING)
        .map {
            it.asText()
        }

fun JsonNode.tryGetBigDecimalAttribute(name: String): Result<BigDecimal, DataErrors.Validation> =
    tryGetAttribute(name = name, type = JsonNodeType.NUMBER)
        .map {
            it.decimalValue()
        }

fun <T> JsonNode.tryGetAttributeAsEnum(name: String, enumProvider: EnumElementProvider<T>):
    Result<T, DataErrors.Validation> where T : Enum<T>,
                                           T : EnumElementProvider.Key = this.tryGetTextAttribute(name)
    .flatMap { text ->
        enumProvider.orNull(text)
            ?.asSuccess<T, DataErrors.Validation>()
            ?: failure(
                DataErrors.Validation.UnknownValue(
                    name = name,
                    expectedValues = enumProvider.allowedElements.keysAsStrings(),
                    actualValue = text
                )
            )
    }

fun <E> JsonNode.getOrErrorResult(name: String, error: (String) -> E): Result<JsonNode, E> = this.getOrNull(name)
    ?.let { success(it) }
    ?: failure(error(name))
