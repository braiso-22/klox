package com.braiso_22

import java.nio.charset.Charset
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: jlox [script]");
        exitProcess(64);
    } else if (args.size == 1) {
        if (!runFile(args[0])) {
            exitProcess(65);
        }
    } else {
        runPrompt();
    }
}


private fun runFile(path: String): Boolean {
    val source = Path(path).readText(Charset.defaultCharset())
    return run(source)
}

private fun runPrompt() {
    while (true) {
        print("> ")

        val line = readlnOrNull() ?: break
        // here we don't care about return value because
        // we continue until ^D
        run(line)
    }
}

fun logError(line: Int, message: String) {
    report(line, "", message)
}

private fun run(source: String): Boolean {
    var hasErrors = false
    val scanner = Scanner(source) { line, message ->
        logError(line, message)
        hasErrors = true
    }
    val tokens = scanner.scanTokens()

    for (token in tokens) {
        println(token)
    }
    return hasErrors
}


private fun report(
    line: Int, where: String,
    message: String
) {
    System.err.println(
        "[line $line] Error$where: $message"
    )
}
