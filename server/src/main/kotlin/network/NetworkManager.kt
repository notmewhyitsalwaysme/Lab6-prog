package network

import kotlinx.serialization.SerializationException
import lab6.prog.network.AppJson
import mu.KotlinLogging
import runner.CommandInvoker
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

private val logger = KotlinLogging.logger {}

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
        logger.info{"Server started on port $port. Waiting for connection..."}
        val buf = ByteArray(65507)

        while (!socket.isClosed) {
            try {
                val packet = DatagramPacket(buf, buf.size)
                socket.receive(packet)
                val json = String(packet.data, 0, packet.length, Charsets.UTF_8)

                val request = try {
                    AppJson.decodeFromString<Request>(json)
                } catch (e: SerializationException) {
                    System.err.println("[Server] Некорректный JSON: ${e.message}")
                    logger.warn(e) { "Invalid request: ${e.message}" }
                    sendResponse(Response(false, "Некорректный формат запроса"), packet.address, packet.port)
                    continue
                }

                println("[Server] '${request.commandName}' от ${packet.address}:${packet.port}")
                logger.info{"'${request.commandName}' от ${packet.address}:${packet.port}. Raw: $Request "}


                val response = invoker.execute(request)
                sendResponse(response, packet.address, packet.port)
                logger.info{Response.toString()}

            } catch (e: Exception) {
                if (!socket.isClosed)
                    System.err.println("[Server] Ошибка: ${e.message}")
                    logger.warn(e) { "Error: ${e.message}" }
            }
        }
    }

    private fun sendResponse(response: Response, address: InetAddress, port: Int) {
        val bytes = AppJson.encodeToString(Response.serializer(), response).toByteArray(Charsets.UTF_8)
        socket.send(DatagramPacket(bytes, bytes.size, address, port))
    }

    fun stop() {
        println("[Server] Остановка...")
        logger.info{ "Shutting down..." }
        socket.close()
    }
}
