package network

import kotlinx.serialization.SerializationException
import lab6.prog.network.AppJson
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel



/**
 * UDP-клиент на основе DatagramChannel в неблокирующем режиме.
 *
 * При недоступности сервера уведомляет пользователя и повторяет
 * попытку каждые [retryDelayMS] мс, не более [maxRetries] раз.
 *
 * @property host адрес сервера
 * @property port порт сервера
 */
class NetworkManager(
    private val host: String,
    private val port: Int,
    private val bufferSize: Int     = 65507,
    private val timeoutMS: Long     = 3000,
    private val retryDelayMS: Long  = 5000,
    private val maxRetries: Int     = 3
) {
    private val channel: DatagramChannel = DatagramChannel.open().also {
        it.configureBlocking(false)
        it.connect(InetSocketAddress(host, port))
    }

    /**
     * Отправляет [Request] и возвращает [Response].
     * При недоступности сервера возвращает null.
     */
    fun sendRequest(request: Request): Response? {
        val requestBytes = AppJson.encodeToString(Request.serializer(), request).toByteArray(Charsets.UTF_8)

        repeat(maxRetries) { attempt ->

            channel.write(ByteBuffer.wrap(requestBytes))

            val response = waitForResponse()
            if (response != null) return response

            println(
                "[Client] Сервер не отвечает " +
                        "(попытка ${attempt + 1}/$maxRetries). " +
                        "Повтор через ${retryDelayMS / 1000} сек..."
            )
            Thread.sleep(retryDelayMS)
        }

        println("[Client] Сервер недоступен. Команда не выполнена.")
        return null
    }

    /**
     * Опрашивает канал до [timeoutMS] мс, возвращает ответ или null.
     */
    // catch java.net.PortUnreachableException if server is no more reachable
    private fun waitForResponse(): Response? {
        val buf = ByteBuffer.allocate(bufferSize)
        val deadline = System.currentTimeMillis() + timeoutMS

        while (System.currentTimeMillis() < deadline) {
            buf.clear()
            if (channel.read(buf) > 0) {
                buf.flip()
                return try {
                    AppJson.decodeFromString<Response>(Charsets.UTF_8.decode(buf).toString())
                } catch (e: SerializationException) {
                    System.err.println("[Client] Некорректный ответ сервера: ${e.message}")
                    null
                }
            }
            Thread.sleep(50)
        }
        return null
    }

    fun close() = channel.close()
}
