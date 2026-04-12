package commands

import collection.CollectionManager
import models.HumanBeing

/**
 * Выводит любой элемент с минимальным значением поля [HumanBeing.name].
 */
class MinByNameCommand(private val manager: CollectionManager) : Command {
    override val name = "min_by_name"
    override val description = "вывести элемент с минимальным именем"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String =
        manager.minByName()?.toString() ?: "Коллекция пуста."
}
