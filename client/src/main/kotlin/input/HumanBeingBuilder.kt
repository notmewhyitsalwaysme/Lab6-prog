package input

import models.Car
import models.Coordinates
import models.HumanBeing
import models.WeaponType

/**
 * Интерактивный построитель объекта [HumanBeing].
 *
 * Читает поля по одному из [inputManager], показывает приглашения,
 * валидирует ввод и повторяет запрос при ошибке.
 *
 * Поля [HumanBeing.id] и [HumanBeing.creationDate] генерируются
 * автоматически и не запрашиваются у пользователя.
 *
 * @property inputManager источник ввода
 */
class HumanBeingBuilder(private val inputManager: IOManager) {

    /**
     * Запускает интерактивный ввод всех полей и возвращает готовый [HumanBeing].
     *
     * @throws IllegalStateException если EOF
     */
    fun build(): HumanBeing {
        val name = readNonBlankString("Введите имя:")
        val coordinates = readCoordinates()
        val realHero = readBoolean("Герой? (true/false):")
        val hasToothpick = readBoolean("Есть зубочистка? (true/false):")
        val impactSpeed = readDouble("Введите скорость удара (impactSpeed):")
        val soundtrackName = readNonBlankString("Введите название саундтрека:")
        val minutesOfWaiting = readFloat("Введите минуты ожидания (minutesOfWaiting):")
        val weaponType = readNullableWeaponType()
        val car = readCar()

        return HumanBeing(
            name = name, coordinates = coordinates, realHero = realHero,
            hasToothpick = hasToothpick, impactSpeed = impactSpeed,
            soundtrackName = soundtrackName, minutesOfWaiting = minutesOfWaiting,
            weaponType = weaponType, car = car
        )
    }

    private fun readCoordinates(): Coordinates {
        val x = readLong("Введите координату X (тип Long):")
        val y = readInt("Введите координату Y (тип Int):")
        return Coordinates(x, y)
    }

    private fun readCar(): Car {
        val cool = readBoolean("Крутой автомобиль ? (true/false):")
        return Car(cool)
    }

    /**
     * Читает непустую строку. Повторяет запрос при пустом вводе.
     */
    private fun readNonBlankString(prompt: String): String {
        while (true) {
            val input = inputManager.readLine(" > $prompt")
                ?: throw IllegalStateException("EOF")
            if (input.isNotBlank()) return input.trim()
            inputManager.print("[Ошибка] Строка не может быть пустой.")
        }
    }

    /**
     * Читает Long. Повторяет запрос при некорректном вводе.
     */
    private fun readLong(prompt: String): Long {
        while (true) {
            val input = inputManager.readLine(" > $prompt")
                ?: throw IllegalStateException("EOF")
            try {
                return input.trim().toLong()
            } catch (e: NumberFormatException) {
                inputManager.print("[Ошибка] Ожидается целое число (Long). Попробуйте снова.")
            }
        }
    }

    /**
     * Читает Int. Повторяет запрос при некорректном вводе.
     */
    private fun readInt(prompt: String): Int {
        while (true) {
            val input = inputManager.readLine(" > $prompt")
                ?: throw IllegalStateException("EOF")
            try {
                return input.trim().toInt()
            } catch (e: NumberFormatException) {
                inputManager.print("[Ошибка] Ожидается целое число (Int). Попробуйте снова.")
            }
        }
    }

    /**
     * Читает Double. Повторяет запрос при некорректном вводе.
     */
    private fun readDouble(prompt: String): Double {
        while (true) {
            val input = inputManager.readLine(" > $prompt")
                ?: throw IllegalStateException("EOF")
            try {
                return input.trim().toDouble()
            } catch (e: NumberFormatException) {
                inputManager.print("[Ошибка] Ожидается число с плавающей точкой (Double). Попробуйте снова.")
            }
        }
    }

    /**
     * Читает Float. Повторяет запрос при некорректном вводе.
     */
    private fun readFloat(prompt: String): Float {
        while (true) {
            val input = inputManager.readLine(" > $prompt")
                ?: throw IllegalStateException("EOF")
            try {
                return input.trim().toFloat()
            } catch (e: NumberFormatException) {
                inputManager.print("[Ошибка] Ожидается число (Float). Попробуйте снова.")
            }
        }
    }

    /**
     * Читает Boolean (true/false). Повторяет запрос при некорректном вводе.
     */
    private fun readBoolean(prompt: String): Boolean {
        while (true) {
            val input = inputManager.readLine(" > $prompt")
                ?: throw IllegalStateException("EOF")
            when (input.trim().lowercase()) {
                "true"  -> return true
                "false" -> return false
                else    -> inputManager.print("[Ошибка] Ожидается 'true' или 'false'. Попробуйте снова.")
            }
        }
    }

    /**
     * Читает [WeaponType] или null (пустая строка).
     * Перед вводом выводит список допустимых констант.
     */
    private fun readNullableWeaponType(): WeaponType? {
        val constants = WeaponType.entries.joinToString(", ")
        while (true) {
            val input = inputManager.readLine(" > Введите тип оружия (доступные: $constants) или оставьте пустым для null:")
                ?: throw IllegalStateException("EOF")
            if (input.isBlank()) return null
            try {
                return WeaponType.valueOf(input.trim().uppercase())
            } catch (e: IllegalArgumentException) {
                inputManager.print("[Ошибка] Неизвестный тип оружия '$input'. Допустимые значения: $constants")
            }
        }
    }

    /**
     * Выводит приглашение к вводу только в интерактивном режиме.
     * В режиме скрипта приглашения не выводятся.
     */
    private fun printPrompt(message: String) {
        if (inputManager.isInteractive) inputManager.print(" > $message ")
    }
}
