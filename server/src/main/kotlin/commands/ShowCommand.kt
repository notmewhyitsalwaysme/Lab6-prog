package commands

import collection.CollectionManager
import models.HumanBeing

class ShowCommand(private val manager: CollectionManager) : Command {
    override val name = "show"
    override val description = "вывести все элементы коллекции"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        if (manager.isEmpty()) return "Коллекция пуста."
        return manager.getAll().joinToString("\n") { it.toString() }
    }
}
