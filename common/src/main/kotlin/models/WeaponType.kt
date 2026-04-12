package models

import kotlinx.serialization.Serializable

/**
 * Перечисление типов оружия, которым может владеть [HumanBeing].
 * Поле [weaponType] в [HumanBeing] может быть null.
 */
@Serializable
enum class WeaponType {
    HAMMER,
    SHOTGUN,
    BAT;
}
