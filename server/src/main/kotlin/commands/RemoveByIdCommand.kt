package commands

import collection.CollectionManager
import java.util.UUID
import models.HumanBeing

class RemoveByIdCommand(private val manager: CollectionManager) : Command {
    override val name = "remove_by_id"
    override val description = "удалить элемент по id: remove_by_id <uuid>"
    override val type = CommandType.ARGS

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        if (args.isEmpty()) return "[Ошибка] Укажите id. Пример: remove_by_id <uuid>"

        val id = try {
            UUID.fromString(args[0])
        } catch (e: IllegalArgumentException) {
            return "[Ошибка] Некорректный UUID: '${args[0]}'"
        }

        return if (manager.removeById(id)) "Элемент удалён."
        else "[Ошибка] Элемент с id '$id' не найден."
    }
}
