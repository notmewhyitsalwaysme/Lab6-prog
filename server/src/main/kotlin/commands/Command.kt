package commands

import models.HumanBeing

/**
 * Базовый интерфейс команды.
 *
 * Теперь execute() возвращает String — результат для отправки клиенту.
 * Зависимость от IOManager полностью убрана из команд.
 */
interface Command {
    val name: String
    val description: String

    /**
     * @param args строковые аргументы (UUID и т.п.)
     * @param humanBeing объект из запроса — только для add/update/add_if_max/add_if_min
     * @return строка-результат для клиента
     */
    fun execute(args: List<String>, humanBeing: HumanBeing? = null): String
}
