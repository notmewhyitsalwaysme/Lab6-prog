package commands

import collection.CollectionManager
import models.HumanBeing

/**
 * Выводит значения поля [models.HumanBeing.minutesOfWaiting] в порядке убывания.
 */
class PrintDescendingMinutesCommand(private val manager: CollectionManager) : Command {
    override val name = "print_field_descending_minutes_of_waiting"
    override val description = "вывести minutesOfWaiting в порядке убывания"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        val values = manager.getMinutesOfWaitingDescending()
        return if (values.isEmpty()) "Коллекция пуста."
        else values.joinToString("\n")
    }
}
