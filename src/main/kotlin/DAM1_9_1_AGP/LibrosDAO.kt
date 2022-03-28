package DAM1_9_1_AGP

import Book
import java.sql.DriverManager

class LibrosDAO(jdbcUrl:String, user:String, password:String) {
    private val connection = DriverManager.getConnection(jdbcUrl, user, password)
    private val books = mutableListOf<Book>()

    init{
        val query = connection.prepareStatement("SELECT * FROM BOOKS")
        val result = query.executeQuery()
        while (result.next()){
            val id = result.getString("id")
            val author = result.getString("author")
            val title = result.getString("title")
            val genre = result.getString("genre")
            val price = result.getDouble("price")
            val publishDate = result.getDate("publish_date")
            val description = result.getString("description")

            books.add(Book(id, author, title, genre, price, publishDate, description))
        }
    }

    fun getBookList() = books.toList()
}