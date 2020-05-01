import java.util

import com.google.gson.{Gson, GsonBuilder}

object Parser {

  val gson: Gson = new GsonBuilder().setPrettyPrinting().create

  def parseContent(message: String): List[String] = {
    if (checkRequest(message)==2) {
    val request = message.split(",")(0).substring(2).dropRight(1)
    if (request.equals("get/") || request.equals("search/")) parseSearchRequest(message)
    else if (request.equals("add/")) parseAddRequest(message)
    else List()
    }
    else List()
  }

  def parseAddRequest(str: String): List[String] = {
    val request = str.split("/")(0).substring(2)
    val payload = str.split("/")(1).substring(2).dropRight(1)
    List(request, payload)
  }

  def parseSearchRequest(str: String): List[String] = {
    val request = str.split("/")(0).substring(2)
    val payload = str.split("/")(1).substring(3).dropRight(2)
    List(request, payload)
  }

  def fromJson(sb: String): Book = gson.fromJson(sb, classOf[Book])

  def checkRequest(request: String): Int = {
    request.split("/").length
  }

  def checkResult(result: util.List[Book]): String = {
    if (result.isEmpty) "No Entry was found"
    else Parser.toJson(result)
  }

  def toJson(res: util.List[Book]): String = gson.toJson(res)

}

