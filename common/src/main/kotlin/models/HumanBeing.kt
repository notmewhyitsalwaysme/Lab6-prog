package models

import kotlinx.serialization.Serializable
import lab6.prog.network.utils.LocalDateSerializer
import lab6.prog.network.utils.UUIDSerializer
import java.time.LocalDate
import java.util.UUID

/**
 * Основная сущность коллекции — Райан Гослинг.
 *
 * @property id уникальный идентификатор, генерируется автоматически
 * @property name имя героя (Райан Гослинг), не может быть null или пустой строкой
 * @property coordinates координаты героя, не может быть null
 * @property creationDate дата создания записи, генерируется автоматически
 * @property realHero является ли настоящим героем (конечно)
 * @property hasToothpick есть ли зубочистка (естественно)
 * @property impactSpeed скорость удара, не может быть null
 * @property soundtrackName название саундтрека, не может быть null (Nightcall - Kavinsky)
 * @property minutesOfWaiting количество минут ожидания
 * @property weaponType тип оружия, может быть null
 * @property car автомобиль героя, не может быть null
 */
@Serializable
data class HumanBeing(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val coordinates: Coordinates,
    @Serializable(with = LocalDateSerializer::class)
    val creationDate: LocalDate = LocalDate.now(),
    val realHero: Boolean,
    val hasToothpick: Boolean,
    val impactSpeed: Double,
    val soundtrackName: String,
    val minutesOfWaiting: Float,
    val weaponType: WeaponType?,
    val car: Car
) : Comparable<HumanBeing> {

    init {
        require(name.isNotBlank()) { "Имя не может быть пустым" }
    }

    /**
     * Сравниваем по имени и id
     */
    override fun compareTo(other: HumanBeing): Int {
        val byName = this.name.compareTo(other.name)
        if (byName != 0) return byName
        return this.id.toString().compareTo(other.id.toString())
    }

    /**
     * Два объекта считаются одинаковыми, если совпадают их [id].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HumanBeing) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    /**
     * Красивое строковое представление для команды [show].
     */
    override fun toString(): String =
        """
        |HumanBeing (It Drives) {
        |  id             = $id
        |  name           = $name (a.k.a. Ryan Gosling)
        |  coordinates    = (x=${coordinates.x}, y=${coordinates.y})
        |  creationDate   = $creationDate
        |  realHero       = $realHero
        |  hasToothpick   = $hasToothpick
        |  impactSpeed    = $impactSpeed
        |  soundtrackName = $soundtrackName
        |  minutesOfWait  = $minutesOfWaiting
        |  weaponType     = ${weaponType ?: "null"}
        |  car.cool       = ${car.cool}
        |}
        """.trimMargin()
}
