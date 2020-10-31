@file:JvmName("ParserApp")

package pl.com.mzubala.cronexpressionsparser

fun main(args: Array<String>) {
    val partParsers = listOf(
        SimpleExpressionParser(),
        RangeExpressionParser(),
        ListExpressionParser(),
        StarStepExpressionParser(),
        StepWithStartExpressionParser(),
        StarExpressionParser()
    )
    val expression = args.firstOrNull()?.trim()
    val parser = ExpressionParser(partParsers)
    val parsedExpression = parser.parse(expression)

    println(
        parsedExpression.fold(
            onSuccess = { parsedExpression -> parsedExpression.toString() },
            onFailure = { ex ->
                "There was an error parsing expression: ${errorDescription(args, ex)}"
            }
        )
    )
}

private fun errorDescription(args: Array<String>, ex: Throwable) =
    "${args.firstOrNull()}\n${ex.message}"