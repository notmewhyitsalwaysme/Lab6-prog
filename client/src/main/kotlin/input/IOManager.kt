package input

import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayDeque
import java.util.Scanner

/**
 * Реализация [input.IOManager] для чтения потока ввода/файла.
 * Поддерживает корректную обработку Ctrl+C и Ctrl+D.
 */
class IOManager() {
    val isInteractive = true
    val inputQueue = ArrayDeque<Scanner>()
    val fileHistory = ArrayDeque<String>()

    init {
        inputQueue.push(Scanner(System.`in`))
    }

    fun addScriptScanner(filePath: String) {
        if (fileHistory.contains(filePath)) {
            printErrConsole("Скрипт уже был выполнен: $filePath")
            return
        }

        try {
            fileHistory.push(filePath)
            inputQueue.push(Scanner(File(filePath), Charsets.UTF_8))
        } catch (e: FileNotFoundException) {
            printErrConsole("Файл не найден: $filePath")
        }
    }

    // сделать основной Scanner
    // при инициализации стандартный сканер добавляется
    // стек input, один (console) открыт постоянно
    // если файл - в стек кидается Scanner

    /**
     * Читает строку с промптом ☭.
     */
    fun readLine(prompt: String): String? {
        while (inputQueue.isNotEmpty()) {
            if (inputQueue.size == 1) kotlin.io.print(prompt)
            val topInDeque = inputQueue.peek()
            if (topInDeque.hasNextLine()) {

                return topInDeque.nextLine().toString()
            }
            if (inputQueue.size > 1) {
                topInDeque.close()
                inputQueue.pop()
                fileHistory.pop()

                // snap back to reality
                continue
            }
            inputQueue.pop()
            print("Ввод закончен.")
            break
        }
        return null
    }

    fun print(text: String) {
        println(text)
    }

    fun printErrConsole(text: String) {
        System.err.println(text)
    }


}
