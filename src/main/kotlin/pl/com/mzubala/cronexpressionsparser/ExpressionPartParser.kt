package pl.com.mzubala.cronexpressionsparser

import kotlin.Result.Companion.success

sealed class ExpressionPartParser(regexStr: String) {

    private val supportedExpressionsRegex = Regex("^$regexStr$")

    abstract fun parse(partValue: String, part: ExpressionPart): Result<List<Int>>

    fun canParse(partValue: String) = partValue.matches(supportedExpressionsRegex)
}

class SimpleExpressionParser : ExpressionPartParser("\\d+") {
    override fun parse(partValue: String, part: ExpressionPart): Result<List<Int>> = runCatching {
        val value = partValue.toInt()
        part.requireValueInRange(value, partValue)
        listOf(value)
    }
}

class RangeExpressionParser : ExpressionPartParser("\\d+\\-\\d+") {
    override fun parse(partValue: String, part: ExpressionPart): Result<List<Int>> = runCatching {
        val partsSplit = partValue.split("-")
        val start = partsSplit.first().toInt()
        val end = partsSplit.last().toInt()
        part.requireValueInRange(start, partValue)
        part.requireValueInRange(end, partValue)
        require(start <= end) {
            "Range $partValue start must be less or equal to end"
        }
        (start..end).toList()
    }
}

class ListExpressionParser : ExpressionPartParser("\\d+,\\d+(,\\d+)*") {
    override fun parse(partValue: String, part: ExpressionPart): Result<List<Int>> =
        runCatching {
            partValue.split(",").map { it.toInt() }.map {
                part.requireValueInRange(it, partValue)
                it
            }
        }
}

class StarExpressionParser() : ExpressionPartParser("\\*") {
    override fun parse(partValue: String, part: ExpressionPart): Result<List<Int>> =
        success(part.range.toList())
}

class StarStepExpressionParser : ExpressionPartParser("\\*/\\d+") {
    override fun parse(partValue: String, part: ExpressionPart): Result<List<Int>> =
        runCatching {
            val step = partValue.replace("*/", "").toInt()
            part.requireValueInRange(part.range.start + step, partValue)
            part.range.step(step).toList()
        }
}

class StepWithStartExpressionParser : ExpressionPartParser("\\d+/\\d+") {
    override fun parse(partValue: String, part: ExpressionPart): Result<List<Int>> =
        runCatching {
            val parts = partValue.split("/")
            val start = parts.first().toInt()
            val step = parts.last().toInt()
            part.requireValueInRange(start, partValue)
            part.requireValueInRange(step, partValue)
            (start..part.range.endInclusive).step(step).toList()
        }
}