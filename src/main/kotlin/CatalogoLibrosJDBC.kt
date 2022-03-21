import java.sql.DriverManager

class CatalogoLibrosJDBC(jdbcUrl:String, user:String, password:String):CatalogoLibros{
    private val books = CatalogoLibrosDAO(jdbcUrl, user, password).getBookList()

    override fun existeLibro(idLibro: String): Boolean = books.any { it.id == idLibro }

    override fun infoLibro(idLibro: String): Map<String, Any> = books.find { it.id == idLibro }.serializeToMap()
}