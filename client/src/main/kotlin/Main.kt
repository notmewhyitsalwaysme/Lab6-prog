import network.NetworkManager
import input.HumanBeingBuilder
import input.IOManager
import network.Request
import runner.CommandValidator

fun main(args: Array<String>) {
    val host = if (args.isNotEmpty()) args[0] else "localhost"
    val port = if (args.size >= 2) args[1].toIntOrNull() ?: 8080 else 8080

    val io = IOManager()                        // Name by Boris Bosenko
    val client = NetworkManager(host, port)
    val validator = CommandValidator()

    // Shutdown hook
    Runtime.getRuntime().addShutdownHook(Thread {
        client.close()
    })

    io.print("""
        |════════════════════════════════════════════
        |Lab6 Client — подключение к $host:$port  
        |════════════════════════════════════════════
            """.trimMargin())

    runClientRepl(io, client, validator)
}

fun runClientRepl(io: IOManager, client: NetworkManager, validator: CommandValidator) {

    val initialRequest = Request(
        commandName = "get_commands",
        humanBeing = null
    )
    val res = client.sendRequest(initialRequest)

    if (res == null) {
        io.print("[!] Сервер недоступен. Попробуйте позже.")
    } else {
        validator.setCommands(
            knownCommands = res.knownCommands!!,
            commandsWArgs = res.commandsWArgs!!,
            humanBeingCommands = res.humanBeingCommands!!
        )
        io.print("|Введите 'help' для справки.")
    }

    while (true) {
        val line = io.readLine(" ☭ ") ?: break
        if (line.isBlank()) continue

        val parts = line.trim().split(" ")
        val commandName = parts[0].lowercase()
        val args = parts.drop(1)

        val (isValidCommand, message) = validator.validate(commandName, args)

        if (!isValidCommand) {
            if (message != null) io.print(message)
            continue
        }

        if (commandName == "exit") {
            io.print("До свидания!")
            break
        }

        if (commandName == "execute_script") {
            io.addScriptScanner(args[0])
            continue
        }

        val humanBeing = if (validator.isBuildsHumanBeing(commandName)) {
            try {
                HumanBeingBuilder(io).build()
            } catch (e: IllegalStateException) {
                io.print("[Ошибка] Ввод прерван: ${e.message}")
                continue
            }
        } else null

        // Формируем запрос и отправляем
        val request = Request(
            commandName = commandName,
            args = args,
            humanBeing = humanBeing
        )

        val response = client.sendRequest(request)

        // Обрабатываем ответ
        if (response == null) {
            io.print("[!] Сервер недоступен. Попробуйте позже.")
        } else {
            io.print(response.message)
        }
    }
}
