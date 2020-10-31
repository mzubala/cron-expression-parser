package pl.com.mzubala.cronexpressionsparser

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import pl.com.mzubala.cronexpressionsparser.ExpressionPart.*
import java.lang.IllegalArgumentException
import kotlin.Result.Companion.success

class ExpressionParserTest {

    val parser = ExpressionParser(
        listOf(
            SimpleExpressionParser(),
            RangeExpressionParser(),
            ListExpressionParser(),
            StarStepExpressionParser(),
            StepWithStartExpressionParser(),
            StarExpressionParser()
        )
    )

    @Test
    fun `parses basic cron expression`() {
        val expression = "15 0 1 10 1 /usr/bin/find"

        val parsedExpression = parser.parse(expression)

        assertThat(parsedExpression).isEqualTo(
            success(
                ParsedExpression(
                    parts = mapOf(
                        MINUTE to listOf(15),
                        HOUR to listOf(0),
                        DAY_OF_MONTH to listOf(1),
                        MONTH to listOf(10),
                        DAY_OF_WEEK to listOf(1)
                    ),
                    command = "/usr/bin/find"
                )
            )
        )
    }

    @Test
    fun `parses complex cron expression`() {
        val expression = "*/15 0 1,15 * 1-5 /usr/bin/find"

        val parsedExpression = parser.parse(expression)

        assertThat(parsedExpression).isEqualTo(
            success(
                ParsedExpression(
                    parts = mapOf(
                        MINUTE to listOf(0, 15, 30, 45),
                        HOUR to listOf(0),
                        DAY_OF_MONTH to listOf(1, 15),
                        MONTH to listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                        DAY_OF_WEEK to listOf(1, 2, 3, 4, 5)
                    ),
                    command = "/usr/bin/find"
                )
            )
        )
    }

    @Test
    fun `fails when one of the expression parts is invalid`() {
        val expression = "*/15 0 20,15 * 1-10 /usr/bin/find"

        val parsedExpression = parser.parse(expression)

        assertThat(parsedExpression.isFailure).isTrue()
        assertThatThrownBy { parsedExpression.getOrThrow() }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `fails when expression contains less than 6 parts`() {
        val expression = "*/15 0"

        val parsedExpression = parser.parse(expression)

        assertThat(parsedExpression.isFailure).isTrue()
        assertThatThrownBy { parsedExpression.getOrThrow() }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `fails when expression contains more than 6 parts`() {
        val expression = "*/15 0 20,15 * 1-10 /usr/bin/find xxx"

        val parsedExpression = parser.parse(expression)

        assertThat(parsedExpression.isFailure).isTrue()
        assertThatThrownBy { parsedExpression.getOrThrow() }.isInstanceOf(IllegalArgumentException::class.java)
    }
}