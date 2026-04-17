package network

import kotlinx.serialization.Serializable

/**
 * Ответ сервера клиенту.
 *
 * @property success true — команда выполнена, false — ошибка
 * @property message текст для вывода пользователю
 *
 *  -- только при синхронизации --
 * @property knownCommands - общий список известных команд
 * @property humanBeingCommands - команды, требующие ввода коллекции [models.HumanBeing]
 * @property commandsWArgs - команды, требующие ввода аргументов
 */
@Serializable
data class Response(
    val success: Boolean,
    val message: String,
    val knownCommands: Set<String>? = null,
    val humanBeingCommands: Set<String>? = null,
    val commandsWArgs: Set<String>? = null
)
