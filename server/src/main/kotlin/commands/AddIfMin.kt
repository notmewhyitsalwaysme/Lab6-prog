package commands

import collection.CollectionManager
import models.HumanBeing

class AddIfMinCommand(private val manager: CollectionManager) : Command {
    override val name = "add_if_min"
    override val description = "добавить элемент, если он меньше минимального"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        humanBeing ?: return "[Ошибка] Объект HumanBeing не передан."
        val min = manager.getMin()
        return if (min == null || humanBeing < min) {
            manager.add(humanBeing)
            "Элемент добавлен (меньше минимума)."
        } else {
            "Элемент не добавлен: не меньше текущего минимума (${min.name})."
        }
    }
}
