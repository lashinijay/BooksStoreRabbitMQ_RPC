import java.util

import scala.jdk.CollectionConverters._

object Services {

  def searchByName(term: String): util.List[Book] = BookList.list.filter(book => book.author == term || book.title == term).asJava

  def searchByIsbn(isbn: String): util.List[Book] = BookList.list.filter(book => book.isbn == isbn).asJava

  def add(book: Book): String = {
    BookList.list += book
    "Successfully added"
  }
}

