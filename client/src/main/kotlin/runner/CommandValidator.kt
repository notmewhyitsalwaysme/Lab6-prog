package runner

class CommandValidator {

    // Команды, которые требуют ввода HumanBeing
    private val humanBeingCommands = setOf("add", "add_if_max", "add_if_min")

    // Команды с аргументом
    private val commandsWArgs = setOf("update", "remove_by_id", "execute_script")

    // Все команды, доступные клиенту
    private val knownCommands = setOf(
        "help", "info", "show", "add", "update",
        "remove_by_id", "clear", "execute_script",
        "add_if_max", "add_if_min", "history",
        "sum_of_minutes_of_waiting", "min_by_name",
        "print_field_descending_minutes_of_waiting",
        "exit"
    )

    fun validate(command: String, args: List<String>?): Pair<Boolean, String?> {
        if (command !in knownCommands){
            return Pair(false, "Неизвестная команда: '$command'. Введите 'help' для справки.")
        }

        if (command in commandsWArgs && args?.isEmpty() == true) {
            return if (command == "execute_script") {
                Pair(false, "[Ошибка] Укажите путь к файлу. Пример: execute_script script.txt")
            } else {
                // maybe create another set w commands w file args and another for other args
                Pair(false, "[Ошибка] Укажите id элемента. Пример: $command <uuid>")
            }
        }
         return Pair(true, null)
    }

    fun isBuildsHumanBeing(command: String): Boolean {
        return command in humanBeingCommands
    }
}