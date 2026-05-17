package com.braiso_22

import com.braiso_22.TokenType.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

class Scanner(
    val source: String,
    val logError: (Int, String) -> Unit,
) {
    private var start = 0
    private var current = 0
    private var line = 1
    private val tokens = mutableListOf<Token>()
    fun scanTokens(): PersistentList<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens += (Token(EOF, "", null, line))
        return tokens.toPersistentList()
    }

    private fun scanToken() {
        when (val c: Char = advance()) {
            // Single chars
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            // Two chars
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            // Skippable
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(SLASH)
                }
            }

            ' ', '\r', '\t' -> {
                /* ignored */
            }

            '\n' -> line++
            '"' -> string()
            // Exception
            else -> logError(line, "Unexpected character: $c")
        }
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length;
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens += Token(type, text, literal, line)
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char? = source.getOrNull(current)

    private fun string() {
        while (peek() != '"' && peek() != null) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            logError(line, "Unterminated string.")
            return
        }
        advance()

        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }
}
