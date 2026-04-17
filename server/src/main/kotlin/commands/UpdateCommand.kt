package commands

import collection.CollectionManager
import models.HumanBeing
import java.util.UUID

class UpdateCommand(private val manager: CollectionManager) : Command {
    override val name = "update"
    override val description = "обновить элемент по id: update <uuid>"
    override val type = CommandType.ARGS

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        if (args.isEmpty()) return "[Ошибка] Укажите id. Пример: update <uuid>"
        humanBeing ?: return "[Ошибка] Объект HumanBeing не передан."

        val id = try {
            UUID.fromString(args[0])
        } catch (e: IllegalArgumentException) {
            return "[Ошибка] Некорректный UUID: '${args[0]}'"
        }

        return if (manager.update(id, humanBeing)) "Элемент обновлён."
        else "[Ошибка] Элемент с id '$id' не найден."
    }
}
