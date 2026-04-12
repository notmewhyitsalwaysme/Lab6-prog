package commands

import collection.CollectionManager
import models.HumanBeing

class InfoCommand(private val manager: CollectionManager) : Command {
    override val name = "info"
    override val description = "вывести информацию о коллекции"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String =
        manager.getInfo()
}
