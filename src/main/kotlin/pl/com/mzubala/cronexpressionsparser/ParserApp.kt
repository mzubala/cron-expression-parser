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
    val expression = args.firstOrNull()
    val parser = ExpressionParser(partParsers)
    val parsedExpression = parser.parse(expression)

    println(
        parsedExpression.fold(
            onSuccess = { parsedExpression -> parsedExpression.toString() },
            onFailure = { ex ->
                "There was an error parsing expression: ${args.first()}\n${ex.message}"
            }
        )
    )
}