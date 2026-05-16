package com.braiso_22

import com.braiso_22.TokenType.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

class Scanner(
    val source: String,
    val logError: (Int, String) -> Unit,
) {
    private val tokens: PersistentList<Token> = persistentListOf()
    fun scanTokens(): PersistentList<Token> {
        var start = 0
        var current = 0
        var line = 1
        val builder = tokens.builder()
        fun isAtEnd(): Boolean {
            return current >= source.length;
        }

        fun advance(): Char {
            return source[current++]
        }

        fun addToken(type: TokenType, literal: Any?) {
            val text = source.substring(start, current)
            builder.add(Token(type, text, literal, line))
        }

        fun addToken(type: TokenType) {
            addToken(type, null)
        }

        fun match(expected: Char): Boolean {
            if (isAtEnd()) return false
            if (source[current] != expected) return false

            current++
            return true
        }

        fun peek(): Char {
            if (isAtEnd()) return '\u0000'
            return source[current]
        }

        fun string() {
            while (peek() != '"' && !isAtEnd()) {
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

        fun scanToken() {
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

        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        builder.add(Token(EOF, "", null, line))
        return builder.build()
    }

}
