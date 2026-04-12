package network

import kotlinx.serialization.Serializable
import models.HumanBeing

/**
 * Запрос от клиента к серверу.
 *
 * @property commandName имя команды ("add", "show", "remove_by_id" ...)
 * @property args строковые аргументы (UUID для update/remove_by_id и т.п.)
 * @property humanBeing объект для команд: add, update, add_if_max, add_if_min
 */
@Serializable
data class Request(
    val commandName: String,
    val args: List<String> = emptyList(),
    val humanBeing: HumanBeing? = null
)
