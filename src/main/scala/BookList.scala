import scala.collection.mutable.ListBuffer

object BookList {
  val book1: Book = Book("190-76-7664-12", "The Sun", "Andrew", 245.90)
  val book2: Book = Book("34-9088-55-8", "Snowman", "Jo Nesbo", 1500)
  val book3: Book = Book("234-0-340-99912-7", "Good Friday", "Lynda", 3600)
  val book4: Book = Book("234-0-340-999-734", "Paradise Bay", "James G.", 3600)
  val book5: Book = Book("34-9088-5512-8", "The Leopard", "Jo Nesbo", 1500)
  val book6: Book = Book("190-76-664-12", "The Sun", "N. Roberts", 255.90)
  var list = ListBuffer(book1, book2, book3, book4, book5, book6)
}
