package network

import kotlinx.serialization.Serializable

/**
 * Ответ сервера клиенту.
 *
 * @property success true — команда выполнена, false — ошибка
 * @property message текст для вывода пользователю
 */
@Serializable
data class Response(
    val success: Boolean,
    val message: String
)
