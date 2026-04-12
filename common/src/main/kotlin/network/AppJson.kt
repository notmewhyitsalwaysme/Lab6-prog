package lab6.prog.network

import kotlinx.serialization.json.Json

/**
 * Общий экземпляр Json для всего приложения.
 * ignoreUnknownKeys — устойчивость к добавлению новых полей в протокол.
 */
val AppJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}