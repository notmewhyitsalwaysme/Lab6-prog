package commands

import collection.CollectionManager
import models.HumanBeing

class ClearCommand(private val manager: CollectionManager) : Command {
    override val name = "clear"
    override val description = "очистить коллекцию"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        manager.clear()
        return "Коллекция очищена."
    }
}
