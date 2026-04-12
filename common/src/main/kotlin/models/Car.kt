package models

import kotlinx.serialization.Serializable

/**
 * Автомобиль, которым владеет [HumanBeing].
 *
 * @property cool признак того, что автомобиль крутой
 */
@Serializable
data class Car(
    val cool: Boolean
)
