package commands

import models.HumanBeing
import runner.CommandInvoker

class GetCommandsCommand (
    private val invoker: CommandInvoker
): Command {
    override val name: String = "GetCommands"
    override val description: String = "Получает все доступные с сервера команды"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        return invoker.getCommands().toString()
    }
}