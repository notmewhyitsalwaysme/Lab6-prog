package commands

/**
 * Завершает программу без сохранения.
 */
import models.HumanBeing

class ExitCommand() : Command {
    override val name = "exit"
    override val description = "завершить программу"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String = ""
}
