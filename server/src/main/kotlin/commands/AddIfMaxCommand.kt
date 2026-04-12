package commands

import collection.CollectionManager
import models.HumanBeing

class AddIfMaxCommand(private val manager: CollectionManager) : Command {
    override val name = "add_if_max"
    override val description = "добавить элемент, если он больше максимального"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        humanBeing ?: return "[Ошибка] Объект HumanBeing не передан."
        val max = manager.getMax()
        return if (max == null || humanBeing > max) {
            manager.add(humanBeing)
            "Элемент добавлен (превышает максимум)."
        } else {
            "Элемент не добавлен: не превышает текущий максимум (${max.name})."
        }
    }
}
