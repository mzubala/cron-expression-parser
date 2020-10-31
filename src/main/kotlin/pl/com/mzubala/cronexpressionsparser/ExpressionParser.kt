package pl.com.mzubala.cronexpressionsparser

import java.lang.IllegalArgumentException

class ExpressionParser(private val partParsers: List<ExpressionPartParser>) {
    fun parse(expression: String?): Result<ParsedExpression> {
        if(expression == null) {
            return Result.failure(IllegalArgumentException(USAGE_INFO))
        }
        val partValues = expression.split(" ")
        return parseAllExpressionParts(partValues.toTypedArray())
    }

    private fun parseAllExpressionParts(partValues: Array<String>): Result<ParsedExpression> =
        runCatching {
            validateUsage(partValues)
            val parsedParts = ExpressionPart.values().map { part ->
                Pair(part, parseSingleExpressionPart(partValues.get(part.ordinal), part).getOrThrow())
            }
            val command = partValues.last()
            ParsedExpression(
                mapOf(*parsedParts.toTypedArray()),
                command
            )
        }

    fun parseSingleExpressionPart(partValue: String, part: ExpressionPart): Result<List<Int>> {
        val parser = partParsers.firstOrNull { it.canParse(partValue) }
            ?: return Result.failure(
                IllegalArgumentException(
                    "Unsupported expression part for $part.name part: $partValue"
                )
            )
        return parser.parse(partValue, part)
    }

    private fun validateUsage(expressionParts: Array<String>) {
        require(expressionParts.size == ExpressionPart.partsCount(), { USAGE_INFO })
    }

    companion object {
        private val USAGE_INFO =
            "Usage: \"MINUTE_EXPRESSION HOUR_EXPRESSION DAY_OF_MONTH_EXPRESSION MONTH_EXPRESSION DAY_OF_WEEK_EXPRESSION COMMAND\""
    }
}