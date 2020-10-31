package pl.com.mzubala.cronexpressionsparser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.com.mzubala.cronexpressionsparser.ExpressionPart.*

class SimpleExpressionPartParserTest {

    val parser = SimpleExpressionParser()

    @Test
    fun `parses simple expression`() {
        val expressionPart = "10"

        val parsedExpressionPart = parser.parse(expressionPart, HOUR)

        assertThat(parser.canParse(expressionPart)).isTrue()
        assertThat(parsedExpressionPart)
            .isEqualTo(Result.success(listOf(10)))
    }

    @Test
    fun `fails when simple expression is out of context range`() {
        val expressionPart = "10"

        val parsedExpressionPart = parser.parse(expressionPart, DAY_OF_WEEK)

        assertThat(parser.canParse(expressionPart)).isTrue()
        assertThat(parsedExpressionPart.isFailure).isTrue()
    }

    @Test
    fun `cannot parse other expression types`() {
        assertThat(parser.canParse("*")).isFalse()
        assertThat(parser.canParse("1-7")).isFalse()
        assertThat(parser.canParse("1,2,3")).isFalse()
        assertThat(parser.canParse("1/2")).isFalse()
        assertThat(parser.canParse("*/15")).isFalse()
    }
}

class RangeExpressionParserTest {

    val parser = RangeExpressionParser()

    @Test
    fun `parses range expression`() {
        val expressionPart = "10-13"

        val parsedExpressionPart = parser.parse(expressionPart, HOUR)

        assertThat(parsedExpressionPart)
            .isEqualTo(Result.success(listOf(10, 11, 12, 13)))
    }

    @Test
    fun `parses single value range expression`() {
        val expressionPart = "10-10"

        val parsedExpressionPart = parser.parse(expressionPart, HOUR)

        assertThat(parsedExpressionPart)
            .isEqualTo(Result.success(listOf(10)))
    }

    @Test
    fun `fails when range expression is invalid`() {
        val invalidRangeExpression = "12-10"

        val parsedExpressionPart = parser.parse(invalidRangeExpression, HOUR)

        assertThat(parsedExpressionPart.isFailure).isTrue()
    }

    @Test
    fun `fails when range expression is out of context range`() {
        val invalidRangeExpression = "10-12"

        val parsedExpressionPart = parser.parse(invalidRangeExpression, DAY_OF_WEEK)

        assertThat(parsedExpressionPart.isFailure).isTrue()
    }

    @Test
    fun `cannot parse other expression types`() {
        assertThat(parser.canParse("1")).isFalse()
        assertThat(parser.canParse("*")).isFalse()
        assertThat(parser.canParse("1,2,3")).isFalse()
        assertThat(parser.canParse("1/2")).isFalse()
        assertThat(parser.canParse("*/15")).isFalse()
    }
}

class ListExpressionParserTest {

    val parser = ListExpressionParser()

    @Test
    fun `parses list expression`() {
        val expressionPart = "10,11,12"

        val parsedExpressionPart = parser.parse(expressionPart, HOUR)

        assertThat(parsedExpressionPart)
            .isEqualTo(Result.success(listOf(10, 11, 12)))
    }

    @Test
    fun `fails when list expression is out of context range`() {
        val expressionPart = "10,11,12"

        val parsedExpressionPart = parser.parse(expressionPart, DAY_OF_WEEK)

        assertThat(parsedExpressionPart.isFailure).isTrue()
    }

    @Test
    fun `cannot parse other expression types`() {
        assertThat(parser.canParse("1")).isFalse()
        assertThat(parser.canParse("*")).isFalse()
        assertThat(parser.canParse("1-3")).isFalse()
        assertThat(parser.canParse("1/2")).isFalse()
        assertThat(parser.canParse("*/15")).isFalse()
    }
}

class StarExpressionParserTest {

    val parser = StarExpressionParser()

    @Test
    fun `parses start expression in all contexts`() {
        assertThat(parser.parse("*", MINUTE)).isEqualTo(Result.success((0..59).toList()))
        assertThat(parser.parse("*", HOUR)).isEqualTo(Result.success((0..23).toList()))
        assertThat(parser.parse("*", DAY_OF_WEEK)).isEqualTo(Result.success((1..7).toList()))
        assertThat(parser.parse("*", DAY_OF_MONTH)).isEqualTo(Result.success((1..31).toList()))
    }

    @Test
    fun `cannot parse other expression types`() {
        assertThat(parser.canParse("1")).isFalse()
        assertThat(parser.canParse("1-3")).isFalse()
        assertThat(parser.canParse("1,2,3")).isFalse()
        assertThat(parser.canParse("*/15")).isFalse()
        assertThat(parser.canParse("1/2")).isFalse()
    }

}

class StarStepExpressionParserTest {

    val parser = StarStepExpressionParser()

    @Test
    fun `parses star step expression`() {
        assertThat(parser.parse("*/15", MINUTE)).isEqualTo(Result.success(listOf(0, 15, 30, 45)))
        assertThat(parser.parse("*/3", HOUR)).isEqualTo(Result.success(listOf(0, 3, 6, 9, 12, 15, 18, 21)))
        assertThat(parser.parse("*/1", DAY_OF_WEEK)).isEqualTo(Result.success(listOf(1, 2, 3, 4, 5, 6, 7).toList()))
        assertThat(parser.parse("*/9", MONTH)).isEqualTo(Result.success(listOf(1, 10)))
        assertThat(parser.parse("*/9", DAY_OF_MONTH)).isEqualTo(Result.success(listOf(1, 10, 19, 28)))
    }

    @Test
    fun `fails when star step is too big`() {
        assertThat(parser.parse("*/60", MINUTE).isFailure).isTrue()
        assertThat(parser.parse("*/60", HOUR).isFailure).isTrue()
        assertThat(parser.parse("*/7", DAY_OF_WEEK).isFailure).isTrue()
        assertThat(parser.parse("*/12", MONTH).isFailure).isTrue()
        assertThat(parser.parse("*/31", DAY_OF_MONTH).isFailure).isTrue()
    }

    @Test
    fun `cannot parse other expression types`() {
        assertThat(parser.canParse("1")).isFalse()
        assertThat(parser.canParse("1-3")).isFalse()
        assertThat(parser.canParse("1,2,3")).isFalse()
        assertThat(parser.canParse("*")).isFalse()
        assertThat(parser.canParse("1/2")).isFalse()
    }
}

class StepWithStartExpressionParserTest {
    val parser = StepWithStartExpressionParser()

    @Test
    fun `parses step with start expression`() {
        assertThat(parser.parse("15/15", MINUTE)).isEqualTo(Result.success(listOf(15, 30, 45)))
        assertThat(parser.parse("3/10", HOUR)).isEqualTo(Result.success(listOf(3, 13, 23)))
        assertThat(parser.parse("7/1", DAY_OF_WEEK)).isEqualTo(Result.success(listOf(7).toList()))
        assertThat(parser.parse("3/9", MONTH)).isEqualTo(Result.success(listOf(3, 12)))
        assertThat(parser.parse("4/9", DAY_OF_MONTH)).isEqualTo(Result.success(listOf(4, 13, 22, 31)))
    }

    @Test
    fun `fails when step expression start or step are out of range`() {
        assertThat(parser.parse("60/15", MINUTE).isFailure).isTrue()
        assertThat(parser.parse("30/60", HOUR).isFailure).isTrue()
        assertThat(parser.parse("0/1", DAY_OF_WEEK).isFailure).isTrue()
        assertThat(parser.parse("3/13", MONTH).isFailure).isTrue()
        assertThat(parser.parse("0/9", DAY_OF_MONTH).isFailure).isTrue()
    }

    @Test
    fun `cannot parse other expression types`() {
        assertThat(parser.canParse("1")).isFalse()
        assertThat(parser.canParse("1-3")).isFalse()
        assertThat(parser.canParse("1,2,3")).isFalse()
        assertThat(parser.canParse("*")).isFalse()
        assertThat(parser.canParse("*/2")).isFalse()
    }
}