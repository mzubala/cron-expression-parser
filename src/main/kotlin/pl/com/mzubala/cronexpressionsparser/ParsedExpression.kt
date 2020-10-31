package pl.com.mzubala.cronexpressionsparser

data class ParsedExpression(
    val parts: Map<ExpressionPart, List<Int>>,
    val command: String
) {
    companion object {
       private const val PART_NAME_DISPLAY_LENGTH = 14
    }

    override fun toString() =
        parts.entries.fold(StringBuilder()) { result, entry ->
            result.append(entry.key.humanName())
            entry.value.joinTo(result)
            result.append("\n")
            result
        }.append(command).toString()

    private fun ExpressionPart.humanName() =
        name.toLowerCase().replace("_", " ").padEnd(PART_NAME_DISPLAY_LENGTH)

}

