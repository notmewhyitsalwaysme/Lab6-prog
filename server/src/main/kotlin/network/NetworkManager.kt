package network

import kotlinx.serialization.SerializationException
import lab6.prog.network.AppJson
import runner.CommandInvoker
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

private const val BUFFER_SIZE = 65507

/**
 * UDP-сервер на основе DatagramSocket (датаграммы).
 *
 * Однопоточный: receive → deserialize → execute → send.
 *
 * @property port порт прослушивания
 * @property invoker инвокер серверных команд
 */
class NetworkManager(
    private val port: Int,
    private val invoker: CommandInvoker
) {
    private val socket = DatagramSocket(port)

    fun start() {
        println("[Server] Запущен на порту $port. Ожидание клиентов...")
        val buf = ByteArray(BUFFER_SIZE)

        while (!socket.isClosed) {
            try {
                // 1. Принять датаграмму
                val packet = DatagramPacket(buf, buf.size)
                socket.receive(packet)

                val json = String(packet.data, 0, packet.length, Charsets.UTF_8)

                // 2. Десериализовать запрос
                val request = try {
                    AppJson.decodeFromString<Request>(json)
                } catch (e: SerializationException) {
                    System.err.println("[Server] Некорректный JSON: ${e.message}")
                    sendResponse(Response(false, "Некорректный формат запроса"), packet.address, packet.port)
                    continue
                }

                println("[Server] '${request.commandName}' от ${packet.address}:${packet.port}")

                // 3. Выполнить и получить ответ
                val response = invoker.execute(request)

                // 4. Отправить ответ
                sendResponse(response, packet.address, packet.port)

            } catch (e: Exception) {
                if (!socket.isClosed)
                    System.err.println("[Server] Ошибка: ${e.message}")
            }
        }
    }

    private fun sendResponse(response: Response, address: InetAddress, port: Int) {
        val bytes = AppJson.encodeToString(Response.serializer(), response).toByteArray(Charsets.UTF_8)
        socket.send(DatagramPacket(bytes, bytes.size, address, port))
    }

    fun stop() {
        println("[Server] Остановка...")
        socket.close()
    }
}
