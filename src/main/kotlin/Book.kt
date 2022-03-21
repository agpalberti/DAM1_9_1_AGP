import java.util.*

data class Book(
    val id: String,
    val autor: String,
    val title: String,
    val genre: String,
    val price: Double,
    val publish_date: Date,
    val description: String
)