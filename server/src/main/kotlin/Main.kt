import collection.CollectionManager
import commands.*
import file.FileManager
import network.NetworkManager
import runner.CommandInvoker
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    System.setProperty("slf4j.internal.verbosity", "WARN")

    val filePath = if (args.isNotEmpty()) args[0] else "data.csv"

    val fileManager = FileManager(filePath)
    val manager = CollectionManager()
    val invoker = CommandInvoker()

    // Загружаем коллекцию из файла
    manager.loadFromFile(fileManager.read())

    // Регистрируем команды
    registerServerCommands(invoker, manager, fileManager)

    // Сохранение при завершении (Ctrl+C или kill)
    val server = NetworkManager(port = 8080, invoker = invoker)
    Runtime.getRuntime().addShutdownHook(Thread {
        println("[Server] Сохранение коллекции перед выходом...")
        fileManager.write(manager.getAll())
        server.stop()
    })

    // Консоль на сервере
    Thread {
        val scanner = java.util.Scanner(System.`in`)
        println("[Server] Серверные команды: save, exit")
        while (scanner.hasNextLine()) {
            when (scanner.nextLine().trim().lowercase()) {
                "save" -> {
                    fileManager.write(manager.getAll())
                    println("[Server] Коллекция сохранена.")
                }
                "exit" -> {
                    println("[Server] Завершение работы...")
                    exitProcess(0)
                }
                else -> println("[Server] Доступные команды: save, exit")
            }
        }
    }.also { it.isDaemon = true }.start()

    server.start()
}

fun registerServerCommands(
    invoker: CommandInvoker,
    manager: CollectionManager,
    fileManager: FileManager
) {
    listOf(
        HelpCommand(invoker),
        InfoCommand(manager),
        ShowCommand(manager),
        AddCommand(manager),
        UpdateCommand(manager),
        RemoveByIdCommand(manager),
        ClearCommand(manager),
        SaveCommand(manager, fileManager),    // только на сервере
        AddIfMaxCommand(manager),
        AddIfMinCommand(manager),
        HistoryCommand(invoker),
        SumOfMinutesCommand(manager),
        MinByNameCommand(manager),
        PrintDescendingMinutesCommand(manager),
        ExitCommand()
    ).forEach { invoker.register(it) }
}
