package commands

import models.HumanBeing
import runner.CommandInvoker

class GetCommandsCommand (
    private val invoker: CommandInvoker
): Command {
    override val name: String = "get_commands"
    override val description: String = "Получает все доступные с сервера команды"
    override val type = CommandType.SIMPLE

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        return "oh uh, client need some commands"
    }
}