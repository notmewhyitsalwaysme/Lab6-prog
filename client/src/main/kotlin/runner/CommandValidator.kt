package runner

class CommandValidator {

    private var humanBeingCommands = setOf<String>()
    private var commandsWArgs = setOf<String>()
    private var knownCommands = setOf<String>()

    fun getKnownCommands(): Set<String> {return knownCommands}

    fun setCommands(knownCommands: Set<String>, commandsWArgs: Set<String>, humanBeingCommands: Set<String>) {
        this.knownCommands = knownCommands
        this.commandsWArgs = commandsWArgs
        this.humanBeingCommands = humanBeingCommands
    }

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