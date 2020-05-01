import java.util.UUID
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

import com.google.gson.Gson
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client._

import scala.jdk.CollectionConverters._

class ResponseCallback(val corrId: String) extends DeliverCallback {
  val response: BlockingQueue[String] = new ArrayBlockingQueue[String](1)

  override def handle(consumerTag: String, message: Delivery): Unit = {
    if (message.getProperties.getCorrelationId.equals(corrId)) {
      response.offer(new String(message.getBody, "UTF-8"))
    }
  }

  def take(): String = {
    response.take()
  }
}

class RPCClient(host: String) {

  val factory = new ConnectionFactory()
  factory.setHost(host)

  val connection: Connection = factory.newConnection()
  val channel: Channel = connection.createChannel()
  val requestQueueName: String = "rpc_queue"
  val replyQueueName: String = channel.queueDeclare().getQueue

  def call(message: String): String = {
    val corrId = UUID.randomUUID().toString
    val props = new BasicProperties.Builder().correlationId(corrId)
      .replyTo(replyQueueName)
      .build()
    channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"))

    val responseCallback = new ResponseCallback(corrId)
    val cancelCallback: CancelCallback = _ => {}
    channel.basicConsume(replyQueueName, true, responseCallback, cancelCallback)

    responseCallback.take()
  }

  def close() {
    connection.close()
  }
}

object RPCClient {

  def main(argv: Array[String]) {
    var bookRpc: RPCClient = null
    var response: String = null
    try {
      val host = if (argv.isEmpty) "localhost" else argv(0)

      val gson: Gson = new Gson()

      bookRpc = new RPCClient(host)

//////////////////////////TEST CASES///////////////////////////////////

            val req = List("get/","34-9088-5512-8").asJava
//      val req = List("search/", "Andrew").asJava
      //      val req = List("add/", Book("234-0-340-999-734", "Paradise", "James G.", 3600)).asJava

      println("Online Books Store")
      println(req)

      response = bookRpc.call(gson.toJson(req))
      println(response)

    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (bookRpc != null) {
        try {
          bookRpc.close()
        } catch {
          case ignore: Exception =>
        }
      }
    }
  }
}
