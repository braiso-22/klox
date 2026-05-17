import com.braiso_22.Scanner
import com.braiso_22.TokenType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ScannerTest {

    @Test
    fun `empty text only returns EOF`() {
        val scanner = Scanner("") { _, _ -> }

        val tokens = scanner.scanTokens()
        assertEquals(1, tokens.size)
        assertEquals(EOF, tokens[0].type)
    }

    @Test
    fun `invalid text only returns EOF`() {
        val scanner = Scanner("hola") { _, _ -> }

        val tokens = scanner.scanTokens()
        assertEquals(1, tokens.size)
        assertEquals(EOF, tokens[0].type)
    }

    @Test
    fun `one character operators work`() {
        val scanner = Scanner("! ? . , ; : = > < () {}-+/ *") { _, _ -> }

        val tokens = scanner.scanTokens()
        assertEquals(
            tokens.map { it.type }.toSet().sorted(),
            listOf(
                LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
                COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
                BANG, EQUAL, GREATER, LESS,
                EOF
            ).toSet().sorted()
        )
    }

    @Test
    fun `two character operators work`() {
        val scanner = Scanner("!= <= == >=") { _, _ -> }

        val tokens = scanner.scanTokens()
        assertEquals(
            tokens.map { it.type }.toSet().sorted(),
            listOf(
                BANG_EQUAL, EQUAL_EQUAL, GREATER_EQUAL, LESS_EQUAL,
                EOF
            ).toSet().sorted()
        )
    }

    @Test
    fun `strings work`() {
        val scanner = Scanner(" \"hola\"") { _, _ -> }

        val tokens = scanner.scanTokens()
        assertEquals(
            2, tokens.size
        )
        assertTrue(tokens[0].type == STRING)
        assertTrue(tokens[0].literal == "hola")
    }

    @Test
    fun `unterminated strings return error`() {
        val scanner = Scanner(" \"hola") { _, message ->
            assertTrue { message.contains("Unterminated string") }
        }

        val tokens = scanner.scanTokens()


    }

    @Test
    fun `comment doesn't take text after`() {
        val scanner = Scanner("// \"hola\"\t\r ") { _, _ -> }

        val tokens = scanner.scanTokens()
        assertEquals(
            1, tokens.size
        )
    }

    @Test
    fun `comment continues until next line`() {
        val scanner = Scanner("// \"hola\"\n * ") { _, _ -> }

        val tokens = scanner.scanTokens()
        assertEquals(
            2, tokens.size
        )
    }

}