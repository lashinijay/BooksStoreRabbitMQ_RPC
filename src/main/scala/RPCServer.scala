import java.util.concurrent.CountDownLatch

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client._

class ServerCallback(val ch: Channel, val latch: CountDownLatch) extends DeliverCallback {

  override def handle(consumerTag: String, delivery: Delivery): Unit = {
    var response: String = null
    val replyProps = new BasicProperties.Builder()
      .correlationId(delivery.getProperties.getCorrelationId)
      .build

    val message = new String(delivery.getBody, "UTF-8")
    println(message)
    response = Controller.controller(message)

    ch.basicPublish("", delivery.getProperties.getReplyTo, replyProps, response.getBytes("UTF-8"))
    ch.basicAck(delivery.getEnvelope.getDeliveryTag, false)
    latch.countDown()
  }

}

object RPCServer {
  private val RPC_QUEUE_NAME = "rpc_queue"

  def main(argv: Array[String]) {
    var connection: Connection = null
    var channel: Channel = null
    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    connection = factory.newConnection()
    channel = connection.createChannel()
    channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null)
    channel.basicQos(1)
    val latch = new CountDownLatch(1)
    val serverCallback = new ServerCallback(channel, latch)
    val cancelCallback: CancelCallback = _ => {}
    channel.basicConsume(RPC_QUEUE_NAME, false, serverCallback, cancelCallback)
    println(" [x] Awaiting RPC requests")
    latch.await()
  }
}
