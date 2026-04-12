package models

import kotlinx.serialization.Serializable

/**
 * Координаты местоположения [HumanBeing].
 *
 * @property x координата X
 * @property y координата Y
 */

@Serializable
data class Coordinates(
    val x: Long,
    val y: Int
)
