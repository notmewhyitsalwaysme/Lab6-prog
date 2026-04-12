package commands

import collection.CollectionManager
import models.HumanBeing

class AddCommand(private val manager: CollectionManager) : Command {
    override val name = "add"
    override val description = "добавить новый элемент в коллекцию"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        humanBeing ?: return "[Ошибка] Объект HumanBeing не передан."
        return if (manager.add(humanBeing))
            "Элемент добавлен: ${humanBeing.name} [${humanBeing.id}]"
        else
            "Элемент уже существует в коллекции."
    }
}
