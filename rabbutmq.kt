package org.example.payment

import com.rabbitmq.client.ConnectionFactory

//fun main() {
//    val RABBIT_HOST = "51.250.26.59"
//    val RABBIT_PORT = 5672
//    val USERNAME = "guest"
//    val PASSWORD = "guest123"
//
//    try {
//        val factory = ConnectionFactory().apply {
//            host = RABBIT_HOST
//            port = RABBIT_PORT
//            username = USERNAME
//            password = PASSWORD
//        }
//
//        println("Подключение к RabbitMQ на ${RABBIT_HOST}:${RABBIT_PORT}...")
//        factory.newConnection().use { connection ->
//            println("Успешное подключение к RabbitMQ!")
//
//            connection.createChannel().use { channel ->
//                println("Канал создан")
//
//                channel.queueDeclare("ikbo-07-22_larin", false, true, true, null)
//                println("Создана эксклюзивная очередь: ikbo-07-22_larin")
//
//                channel.queueDeclare("ikbo-07-22_kuzin", true, false, false, null)
//                println("Создана сохраняемая очередь: ikbo-07-22_kuzin")
//
//                channel.queueDeclare("ikbo-07-22_mishin", false, false, true, null)
//                println("Создана автоудаляемая очередь: ikbo-07-22_mishin")
//
//                println("\n=== ПРОВЕРКА СОЗДАННЫХ ОЧЕРЕДЕЙ ===")
//                val queues = listOf(
//                    "ikbo-07-22_larin",
//                    "ikbo-07-22_kuzin",
//                    "ikbo-07-22_mishin",
//                )
//
//                queues.forEach { queueName ->
//                    try {
//                        val result = channel.queueDeclarePassive(queueName)
//                        println("Очередь '$queueName' существует (сообщений: ${result.messageCount})")
//                    } catch (e: Exception) {
//                        println("Очередь '$queueName' не найдена: ${e.message}")
//                    }
//                }
//
//                println("\nОжидание 60 секунд...")
//                println("Проверьте очереди в веб-интерфейсе: http://51.250.26.59:15672")
//                println("Логин: guest, Пароль: guest")
//                Thread.sleep(60000)
//
//                println("Подключение закрыто.")
//            }
//        }
//    } catch (e: Exception) {
//        println(" Ошибка: ${e.message}")
//        e.printStackTrace()
//    }
//}

fun main() {
    val RABBIT_HOST = "51.250.26.59"
    val RABBIT_PORT = 5672
    val USERNAME = "guest"
    val PASSWORD = "guest123"

    val factory = ConnectionFactory().apply {
        host = RABBIT_HOST
        port = RABBIT_PORT
        username = USERNAME
        password = PASSWORD
    }

    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->

            val exchangeFanout = "ikbo-07-22_larin_fanout"
            channel.exchangeDeclare(exchangeFanout, "fanout", true)
            println("Создан fanout-обменник '$exchangeFanout' (сообщения сохраняются)")

            val messageFanout = "# сообщение: симуляция нагрузки"
            channel.basicPublish(exchangeFanout, "", null, messageFanout.toByteArray())
            println("Отправлено сообщение через fanout: $messageFanout")
            Thread.sleep(2000)

            val exchangeDirect = "ikbo-07-22_kuzin_direct"
            channel.exchangeDeclare(exchangeDirect, "direct", false)
            println("Создан direct-обменник '$exchangeDirect' (сообщения не сохраняются)")

            val messageDirect = "* сообщение: временная нагрузка"
            channel.basicPublish(exchangeDirect, "load", null, messageDirect.toByteArray())
            println("Отправлено сообщение через direct: $messageDirect")
            Thread.sleep(2000)

            val exchangeTopic1 = "ikbo-07-22_mishin_topic"
            channel.exchangeDeclare(exchangeTopic1, "topic", true)
            println("Создан topic-обменник '$exchangeTopic1' (сообщения сохраняются)")

            val messageTopic = "- сообщение: нагрузка с маршрутом"
            channel.basicPublish(exchangeTopic1, "load.topic", null, messageTopic.toByteArray())
            println("Отправлено сообщение через topic: $messageTopic")
            Thread.sleep(2000)

            println("\nВсе обменники созданы, сообщения отправлены.")
        }
    }
}