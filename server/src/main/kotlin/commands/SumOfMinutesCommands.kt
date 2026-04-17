package commands

import collection.CollectionManager
import models.HumanBeing

/**
 * Выводит сумму значений поля [models.HumanBeing.minutesOfWaiting].
 */
class SumOfMinutesCommand(private val manager: CollectionManager) : Command {
    override val name = "sum_of_minutes_of_waiting"
    override val description = "вывести сумму minutesOfWaiting всех элементов"
    override val type = CommandType.SIMPLE

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String =
        "Сумма minutesOfWaiting: ${manager.sumOfMinutesOfWaiting()}"
}
