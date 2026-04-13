package network

import kotlinx.serialization.SerializationException
import lab6.prog.network.AppJson
import java.net.InetSocketAddress
import java.net.PortUnreachableException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel


class NetworkManager(
    private val host: String,
    private val port: Int,
    private val bufferSize: Int = 65507,
    private val timeoutMS: Long  = 3000,
    private val retryDelayMS: Long = 5000,
    private val maxRetries: Int = 3,
) {
    private var channel: DatagramChannel = openChannel()

    private fun openChannel(): DatagramChannel =
        DatagramChannel.open().also {
            it.configureBlocking(false)
            it.connect(InetSocketAddress(host, port))
        }

    fun sendRequest(request: Request): Response? {
        val requestBytes = AppJson.encodeToString(Request.serializer(), request)
            .toByteArray(Charsets.UTF_8)

        repeat(maxRetries) { attempt ->
            try {
                channel.write(ByteBuffer.wrap(requestBytes))
                val response = waitForResponse()
                if (response != null) return response

            } catch (e: PortUnreachableException) {
                reconnect()
            } catch (e: Exception) {
                System.err.println("[Client] Ошибка отправки: ${e.message}")
                reconnect()
            }

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

    private fun waitForResponse(): Response? {
        val buf = ByteBuffer.allocate(bufferSize)
        val deadline = System.currentTimeMillis() + timeoutMS

        while (System.currentTimeMillis() < deadline) {
            try {
                buf.clear()
                if (channel.read(buf) > 0) {
                    buf.flip()
                    return AppJson.decodeFromString<Response>(
                        Charsets.UTF_8.decode(buf).toString()
                    )
                }
            } catch (e: PortUnreachableException) {
                return null
            } catch (e: SerializationException) {
                System.err.println("[Client] Некорректный ответ сервера: ${e.message}")
                return null
            }
            Thread.sleep(50)
        }
        return null
    }

    private fun reconnect() {
        try { channel.close() } catch (_: Exception) {}
        channel = openChannel()
    }

    fun close() {
        try { channel.close() } catch (_: Exception) {}
    }
}
