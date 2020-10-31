package pl.com.mzubala.cronexpressionsparser

enum class ExpressionPart(min: Int, max: Int) {
    MINUTE(0, 59),
    HOUR(0, 23),
    DAY_OF_MONTH(1, 31),
    MONTH(1, 12),
    DAY_OF_WEEK(1, 7);

    val range = min..max

    fun requireValueInRange(value: Int, expressionPart: String) {
        require(
            range.contains(value)
        ) { "Expression part $expressionPart for $name part produces a value, which is out of range $range" }
    }

    companion object {
        fun partsCount() = values().size + 1
    }
}
