package commands

import runner.CommandInvoker
import models.HumanBeing

class HistoryCommand(private val invoker: CommandInvoker) : Command {
    override val name = "history"
    override val description = "вывести последние 12 команд"
    override val type = CommandType.SIMPLE

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        val history = invoker.getHistory()
        if (history.isEmpty()) return "История пуста."
        return history.mapIndexed { i, cmd -> "  ${i + 1}. $cmd" }.joinToString("\n")
    }
}
