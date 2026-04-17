package file

import models.Car
import models.Coordinates
import models.HumanBeing
import models.WeaponType
import mu.KotlinLogging
import java.io.File
import java.io.FileNotFoundException
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Scanner
import java.util.UUID

private val logger = KotlinLogging.logger {}

/**
 * Менеджер файлового хранилища коллекции.
 *
 * Читает данные с помощью [Scanner], записывает через [OutputStreamWriter].
 * Формат хранения — CSV, одна строка = один объект [HumanBeing].
 *
 * Порядок полей в CSV:
 * `id,name,coordX,coordY,creationDate,realHero,hasToothpick,
 *  impactSpeed,soundtrackName,minutesOfWaiting,weaponType,carCool`
 *
 * @property filePath путь к CSV-файлу, полученный из аргумента командной строки
 */
class FileManager(private val filePath: String) {

    companion object {
        private const val DELIMITER = ","
        private const val HEADER =
            "id,name,coordX,coordY,creationDate,realHero," +
                    "hasToothpick,impactSpeed,soundtrackName,minutesOfWaiting,weaponType,carCool"
    }

    /**
     * Читает коллекцию из CSV-файла с помощью [Scanner].
     *
     * Пропускает заголовок и битые строки (с предупреждением в stderr).
     * При отсутствии файла или проблемах с доступом возвращает пустой список.
     *
     * @return список корректно распарсенных объектов [HumanBeing]
     */
    fun read(): List<HumanBeing> {
        val file = File(filePath)

        if (!file.exists()) {
            System.err.println("Файл '$filePath' не найден. Коллекция будет пустой.")
            logger.warn{"There's no '$filePath'. Collection will be empty."}
            return emptyList()
        }

        if (!file.canRead()) {
            System.err.println("Нет прав на чтение файла '$filePath'. Коллекция будет пустой.")
            logger.warn{"Permission denied: '$filePath'. Collection will be empty."}
            return emptyList()
        }

        val result = mutableListOf<HumanBeing>()
        var lineNumber = 0

        try {
            Scanner(file, Charsets.UTF_8).use { scanner ->
                while (scanner.hasNextLine()) {
                    val line = scanner.nextLine().trim()
                    lineNumber++

                    // skip header and empty lines
                    if (lineNumber == 1 && line.startsWith("id")) continue
                    if (line.isBlank()) continue

                    val parsedLine = parseLine(line, lineNumber)
                    if (parsedLine != null) {
                        result.add(parsedLine)
                    } else {
                        System.err.println("Строка $lineNumber пропущена: некорректные данные → '$line'")
                        logger.warn { "Line $lineNumber of $filePath is skipped: invalid data → '$line'" }
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            System.err.println("Файл не найден во время чтения: ${e.message}")
            logger.warn(e) {"File not found: ${e.message}"}
        }

        println("Загружено элементов: ${result.size} из файла '$filePath'")
        logger.info {"${result.size} elements loaded from'$filePath'"}
        return result
    }

    /**
     * Парсит одну CSV-строку в объект [HumanBeing].
     *
     * @param line строка CSV
     * @param lineNumber номер строки (для сообщений об ошибках)
     * @return объект [HumanBeing] или null при ошибке парсинга
     */
    private fun parseLine(line: String, lineNumber: Int): HumanBeing? {
        return try {
            val parts = line.split(DELIMITER, limit = 12)
            if (parts.size < 12) {
                System.err.println("Строка $lineNumber: ожидалось 12 полей, найдено ${parts.size}")
                logger.warn {"Line $lineNumber: expected 12 fields, found ${parts.size}"}
                return null
            }

            val id = UUID.fromString(parts[0].trim())
            val name = parts[1].trim()
            val coordX = parts[2].trim().toLong()
            val coordY = parts[3].trim().toInt()
            val creationDate = LocalDate.parse(parts[4].trim())
            val realHero = parts[5].trim().toBoolean()
            val hasToothpick = parts[6].trim().toBoolean()
            val impactSpeed = parts[7].trim().toDouble()
            val soundtrackName = parts[8].trim()
            val minutesOfWaiting = parts[9].trim().toFloat()
            val weaponType = parts[10].trim().let {
                if (it.equals("null", ignoreCase = true) || it.isBlank()) null
                else WeaponType.valueOf(it)
            }
            val carCool = parts[11].trim().toBoolean()

            require(name.isNotBlank()) { "name не может быть пустым" }

            HumanBeing(
                id = id, name = name, coordinates = Coordinates(coordX, coordY),
                creationDate = creationDate, realHero = realHero, hasToothpick = hasToothpick,
                impactSpeed = impactSpeed, soundtrackName = soundtrackName,
                minutesOfWaiting = minutesOfWaiting, weaponType = weaponType, car = Car(carCool)
            )
        } catch (e: IllegalArgumentException) {
            System.err.println("Строка $lineNumber — ошибка данных: ${e.message}")
            logger.warn(e) {"Line $lineNumber: ${e.message}"}
            null
        } catch (e: DateTimeParseException) {
            System.err.println("Строка $lineNumber — неверный формат даты: ${e.message}")
            logger.warn(e) {"Line $lineNumber: ${e.message}"}
            null
        } catch (e: Exception) {
            System.err.println("Строка $lineNumber — неожиданная ошибка: ${e.message}")
            logger.warn(e) {"Line $lineNumber: ${e.message}"}
            null
        }
    }

    /**
     * Записывает коллекцию в CSV-файл с помощью [OutputStreamWriter].
     *
     * @param items коллекция объектов [HumanBeing] для записи
     * @return true если запись прошла успешно
     */
    fun write(items: Collection<HumanBeing>): Boolean {
        val file = File(filePath)

        if (file.exists() && !file.canWrite()) {
            System.err.println("Нет прав на запись в файл '$filePath'.")
            logger.warn{"Permission denied: '$filePath'."}
            return false
        }

        return try {
            OutputStreamWriter(file.outputStream(), Charsets.UTF_8).use { writer ->
                writer.write(HEADER)
                writer.write("\n")
                items.forEach { h ->
                    writer.write(toCSVLine(h))
                    writer.write("\n")
                }
            }
            println("Коллекция сохранена в '$filePath' (${items.size} элементов).")
            true
        } catch (e: SecurityException) {
            println("Отказано в доступе к файлу '$filePath': ${e.message}")
            logger.warn(e) {"Permission denied: '$filePath'."}
            false
        } catch (e: Exception) {
            println("Ошибка записи в файл: ${e.message}")
            logger.warn(e) {"An error occurred: '$filePath'."}
            false
        }
    }

    /**
     * Сериализует объект [HumanBeing] в строку CSV.
     *
     * @param h объект для сериализации
     * @return строка в формате CSV
     */
    private fun toCSVLine(h: HumanBeing): String = listOf(
        h.id, h.name, h.coordinates.x, h.coordinates.y, h.creationDate,
        h.realHero, h.hasToothpick, h.impactSpeed, h.soundtrackName,
        h.minutesOfWaiting, h.weaponType ?: "null", h.car.cool
    ).joinToString(DELIMITER)
}
