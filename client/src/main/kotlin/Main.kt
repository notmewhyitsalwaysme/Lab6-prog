import network.NetworkManager
import input.HumanBeingBuilder
import input.IOManager
import network.Request


// BEGIN ---- Move it to CommandValidator

// Команды, которые требуют ввода HumanBeing
private val HUMAN_BEING_COMMANDS = setOf("add", "add_if_max", "add_if_min")

// Команды с аргументом (UUID) в строке команды
private val ARG_COMMANDS = setOf("update", "remove_by_id", "execute_script")

// Все команды, доступные клиенту (save — только серверная)
private val KNOWN_COMMANDS = setOf(
    "help", "info", "show", "add", "update",
    "remove_by_id", "clear", "execute_script",
    "add_if_max", "add_if_min", "history",
    "sum_of_minutes_of_waiting", "min_by_name",
    "print_field_descending_minutes_of_waiting",
    "exit"
)

// END ---- Move it to CommandValidator

fun main(args: Array<String>) {
    val host = if (args.isNotEmpty()) args[0] else "localhost"
    val port = if (args.size >= 2) args[1].toIntOrNull() ?: 8080 else 8080

    val io = IOManager()
    val client = NetworkManager(host, port)

    io.print("""
        |╔══════════════════════════════════════╗
        |║   Lab6 Client — подключение к $host:$port
        |╚══════════════════════════════════════╝
        |Введите 'help' для справки.
    """.trimMargin())

    // Shutdown hook — закрыть канал при выходе
    Runtime.getRuntime().addShutdownHook(Thread {
        client.close()
    })

    runClientRepl(io, client)
}

fun runClientRepl(io: IOManager, client: NetworkManager) {
    while (true) {
        val line = io.readLine(" ☭ ") ?: break
        if (line.isBlank()) continue

        val parts = line.trim().split(" ")
        val commandName = parts[0].lowercase()
        val args = parts.drop(1)

        // Move to CommandValidate
        if (commandName !in KNOWN_COMMANDS) {
            io.print("Неизвестная команда: '$commandName'. Введите 'help' для справки.")
            continue
        }

        if (commandName == "exit") {
            io.print("До свидания!")
            break
        }

        // In the event of using fucking script (don't)
        if (commandName == "execute_script") {
            if (args.isEmpty()) {
                io.print("[Ошибка] Укажите путь к файлу. Пример: execute_script script.txt")
                continue
            }
            io.addScriptScanner(args[0])
            continue
        }

        // MOVE TO FUCKING VALIDATE
        // 4. Для update — нужен UUID аргумент ДО ввода HumanBeing
        if (commandName == "update" && args.isEmpty()) {
            io.print("[Ошибка] Укажите id элемента. Пример: update <uuid>")
            continue
        }

        // 5. Строим HumanBeing если нужно
        val humanBeing = if (commandName in HUMAN_BEING_COMMANDS ||
            commandName == "update") {
            try {
                HumanBeingBuilder(io).build()
            } catch (e: IllegalStateException) {
                io.print("[Ошибка] Ввод прерван: ${e.message}")
                continue
            }
        } else null

        // 6. Формируем запрос и отправляем
        val request = Request(
            commandName = commandName,
            args = args,
            humanBeing = humanBeing
        )

        val response = client.sendRequest(request)

        // 7. Обрабатываем ответ
        if (response == null) {
            io.print("[!] Сервер недоступен. Попробуйте позже.")
        } else {
            io.print(response.message)
        }
    }
}
