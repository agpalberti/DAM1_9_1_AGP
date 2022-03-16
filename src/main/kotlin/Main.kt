import java.sql.Date
import java.sql.DriverManager

// the model class
data class User(val id: Int, val name: String)
data class Book (val id:String, val author:String, val title:String, val genre:String, val price:Float,
                 val publishDate: Date, val description:String)
fun main(){

    val jdbcUrl = "jdbc:oracle:thin:@localhost:1521:XE"

    // get the connection
    val connection = DriverManager
        .getConnection(jdbcUrl, "programacion", "programacion")

    val query = connection.prepareStatement("SELECT * FROM BOOKS")
    val result = query.executeQuery()
    val books = mutableListOf<Book>()
    while (result.next()){
        val id = result.getString("id")
        val author = result.getString("author")
        val title = result.getString("title")
        val genre = result.getString("genre")
        val price = result.getFloat("price")
        val publishDate = result.getDate("publish_date")
        val description = result.getString("description")

        books.add(Book(id,author, title, genre, price, publishDate, description))
    }
    books.forEach { println(it) }
}