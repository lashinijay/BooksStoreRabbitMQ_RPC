import com.google.gson.Gson

object Controller {

  val gson: Gson = new Gson()

  def controller(message: String): String = {

    val content = Parser.parseContent(message)
    if (content.isEmpty) "Invalid Request"
    else {
      val request = content.head
      val payload = content(1)

      if (request.equals("get")) Parser.checkResult(Services.searchByIsbn(payload))
      else if (request.equals("search")) Parser.checkResult(Services.searchByName(payload))
      else if (request.equals("add")) gson.toJson(Services.add(Parser.fromJson(payload)))
      else "Invalid Request"
    }
  }

}

