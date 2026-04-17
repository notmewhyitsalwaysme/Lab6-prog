package collection

import models.HumanBeing
import mu.KotlinLogging
import java.time.LocalDate
import java.util.TreeSet
import java.util.UUID

private val logger = KotlinLogging.logger {}

class CollectionManager {

    private val collection: TreeSet<HumanBeing> = TreeSet()
    val initDate: LocalDate = LocalDate.now()

    fun add(humanBeing: HumanBeing): Boolean {
        logger.info { "[ADD] Element created ${humanBeing.id}" }
        return collection.add(humanBeing)
    }

    fun update(id: UUID, updated: HumanBeing): Boolean {
        val old = getById(id) ?: return false
        collection.remove(old)
        val replaced = updated.copy(id = old.id, creationDate = old.creationDate)
        logger.info { "[UPDATE] Element updated ${old.id}" }
        return collection.add(replaced)
    }

    fun removeById(id: UUID): Boolean {
        val target = getById(id) ?: return false
        logger.info { "[REMOVE] Element deleted ${target.id}" }
        return collection.remove(target)
    }

    fun clear() {
        collection.clear()
        logger.info { "[CLEAR] Collection cleared" }
    }

    fun getAll(): TreeSet<HumanBeing> = TreeSet(collection)

    fun getById(id: UUID): HumanBeing? =
        collection.stream()
            .filter { it.id == id }
            .findFirst()
            .orElse(null)

    fun size(): Int = collection.size

    fun isEmpty(): Boolean = collection.isEmpty()

    fun getMax(): HumanBeing? =
        collection.stream()
            .max(Comparator.naturalOrder())
            .orElse(null)

    fun getMin(): HumanBeing? =
        collection.stream()
            .min(Comparator.naturalOrder())
            .orElse(null)

    fun sumOfMinutesOfWaiting(): Double =
        collection.stream()
            .mapToDouble { it.minutesOfWaiting.toDouble() }
            .sum()

    fun minByName(): HumanBeing? =
        collection.stream()
            .min(Comparator.comparing { it.name })
            .orElse(null)

    fun getMinutesOfWaitingDescending(): List<Float> =
        collection.stream()
            .map { it.minutesOfWaiting }
            .sorted(Comparator.reverseOrder())
            .toList()

    fun loadFromFile(items: List<HumanBeing>) {
        collection.clear()
        items.stream().forEach { collection.add(it) }
        logger.info { "[LOAD] loaded ${items.size} elements" }
    }

    fun getInfo(): String =
        """
        |Тип коллекции : ${collection::class.simpleName}
        |Тип элементов : ${HumanBeing::class.simpleName}
        |Дата инициал. : $initDate
        |Кол-во элемен.: ${collection.size}
        """.trimMargin()
}
