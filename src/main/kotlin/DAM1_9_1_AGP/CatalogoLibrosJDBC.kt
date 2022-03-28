package DAM1_9_1_AGP

import CatalogoLibros
import serializeToMap

class CatalogoLibrosJDBC(jdbcUrl:String, user:String, password:String): CatalogoLibros {
    private val books = LibrosDAO(jdbcUrl, user, password).getBookList()

    override fun existeLibro(idLibro: String): Boolean = books.any { it.id == idLibro }

    override fun infoLibro(idLibro: String): Map<String, Any> = books.find { it.id == idLibro }.serializeToMap()
}