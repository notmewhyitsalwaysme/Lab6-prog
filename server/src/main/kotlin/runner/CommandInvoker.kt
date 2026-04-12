package runner

import commands.Command
import network.Request
import network.Response

/**
 * Серверный инвокер.
 *
 * Принимает Request, находит команду, выполняет,
 * возвращает Response готовый для отправки клиенту.
 */
class CommandInvoker {

    private val serverOnlyCommands = setOf("save")
    private val commands = mutableMapOf<String, Command>()
    private val history  = ArrayDeque<String>()

    fun register(command: Command) {
        commands[command.name] = command
    }

    fun execute(request: Request): Response {
        if (request.commandName in serverOnlyCommands) {
            return Response(false, "Команда '${request.commandName}' недоступна клиенту.")
        }

        val command = commands[request.commandName]
            ?: return Response(false, "Неизвестная команда: '${request.commandName}'")

        addToHistory(request.commandName)

        return try {
            val result = command.execute(request.args, request.humanBeing)
            Response(true, result)
        } catch (e: Exception) {
            Response(false, "[Ошибка выполнения] ${e.message}")
        }
    }

    fun getHistory(): List<String> = history.toList()
    fun getCommands(): Map<String, Command> = commands.toMap()

    private fun addToHistory(name: String) {
        if (history.size >= 12) history.removeFirst()
        history.addLast(name)
    }
}
