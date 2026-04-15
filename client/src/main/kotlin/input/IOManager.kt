package input

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.history.DefaultHistory
import org.jline.terminal.TerminalBuilder
import org.jline.reader.impl.completer.StringsCompleter
import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayDeque
import java.util.Scanner
import runner.CommandValidator

class IOManager {
    val isInteractive: Boolean = true
    val commandValidator: CommandValidator = CommandValidator()

    private val terminal = TerminalBuilder.builder()
        .system(true)
        .build()

    private val history = DefaultHistory()

    private val lineReader: LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .history(history)
        .completer(StringsCompleter(commandValidator.getKnownCommands()))
        .option(LineReader.Option.HISTORY_IGNORE_DUPS, true)
        .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
        .build()

    private val scriptQueue = ArrayDeque<Scanner>()
    private val fileHistory = ArrayDeque<String>()

    /**
     * Читает строку.
     * Если есть активный скрипт — читает из него.
     * Иначе — интерактивный ввод через JLine.
     */
    fun readLine(prompt: String): String? {
        // Режим скрипта
        while (scriptQueue.isNotEmpty()) {
            val scanner = scriptQueue.peek()
            if (scanner.hasNextLine()) {
                return scanner.nextLine()
            }
            scanner.close()
            scriptQueue.pop()
            fileHistory.pop()
        }

        return try {
            lineReader.readLine(prompt)
        } catch (e: UserInterruptException) {
            ""
        } catch (e: EndOfFileException) {
            null
        }
    }

    fun addScriptScanner(filePath: String) {
        if (fileHistory.contains(filePath)) {
            printErrConsole("Скрипт уже был выполнен: $filePath")
            return
        }
        try {
            fileHistory.push(filePath)
            scriptQueue.push(Scanner(File(filePath), Charsets.UTF_8))
        } catch (e: FileNotFoundException) {
            printErrConsole("Файл не найден: $filePath")
        }
    }

    fun print(text: String) = println(text)

    fun printErrConsole(text: String) = System.err.println(text)
}