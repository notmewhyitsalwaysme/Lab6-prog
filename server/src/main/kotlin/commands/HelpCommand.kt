package commands

import runner.CommandInvoker
import models.HumanBeing

class HelpCommand(private val invoker: CommandInvoker) : Command {
    override val name = "help"
    override val description = "вывести справку по доступным командам"
    override val type = CommandType.SIMPLE

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String =
        invoker.getCommands().values
            .sortedBy { it.name }
            .joinToString("\n") { "  ${it.name.padEnd(40)} — ${it.description}" }
}

