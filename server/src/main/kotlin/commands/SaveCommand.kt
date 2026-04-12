package commands

import collection.CollectionManager
import file.FileManager
import models.HumanBeing

class SaveCommand(
    private val manager: CollectionManager,
    private val fileManager: FileManager
) : Command {
    override val name = "save"
    override val description = "[SERVER] сохранить коллекцию в файл"

    override fun execute(args: List<String>, humanBeing: HumanBeing?): String {
        return if (fileManager.write(manager.getAll())) "Коллекция сохранена."
        else "[Ошибка] Не удалось сохранить коллекцию."
    }
}
