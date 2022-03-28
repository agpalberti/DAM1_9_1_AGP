package DAM1_9_2_AGP

import Book
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.SQLException

fun main() {
    val c = ConnectionBuilder()
    println("conectando.....")

    if (c.connection.isValid(10)) {
        println("Conexión válida")

        // Deshabilito el autoCommit. Si no, tengo que quitar los commit()
        c.connection.autoCommit = false

        // Uso la conexión. De esta manera cierra la conexión cuando termine el bloque
        c.connection.use {

            // Me creo mi objeto DAO (Data Access Object), el cual sabe acceder a los ç
            // datos de la tabla USER. Necesita la conexión (it) para poder acceder a la
            // base de datos.
            // El objeto DAO va a tener todos los metodos necesarios para trabajar con
            // la tabla USER, y devolverá entidades MyUser.
            // Fuera de este objeto no debería hablarse de nada relacioando con la base
            // de datos.
            // Los objetos MyUser, tambien llamados entidades, se llaman
            // Objetos TO (Transfer Object) porque son objetos que transfieren datos.
            // desde la base de datos hasta las capas de logica de negocio y presentación.
            val librosDAO = LibroDAO(it)

            // Creamos la tabla o la vaciamos si ya existe
            librosDAO.prepareTable()
            librosDAO.insert(Book(id = "bk123", author = "El Parra", title = "La vida de Ricardo", genre = "Demiurgo", price = 20.00, publish_date = Date(2000,6,21), description = "Muchas gracias por leer mi libro" )) // Buscar un usuario
            val b = librosDAO.selectById("1")

            // Si ha conseguido el usuario, por tanto no es nulo, entonces
            // actualizar el usuario
            if (b!=null)
            {
                b.author = "Ricardo Gallego"
                librosDAO.update(b)
            }
            // Borrar un usuario
            librosDAO.deleteById("2")

            // Seleccionar todos los usuarios
            println(librosDAO.selectAll())
        }
    } else
        println("Conexión ERROR")
}

/**
 * Connection builder construye una conexión
 *
 * @constructor Create empty Connection builder
 */
class ConnectionBuilder {
    // TODO Auto-generated catch block
    lateinit var connection: Connection

    // La URL de conexión. Tendremos que cambiarsa según el SGBD que se use.
    private val jdbcURL = "jdbc:oracle:thin:@localhost:1521:XE"
    private val jdbcUsername = "programacion"
    private val jdbcPassword = "programacion"

    init {
        try {
            // Aqui construimos la conexión
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)
        } catch (e: SQLException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
    // Si termina sin excepción, habrá creado la conexión.

}

/**
 * UserDAO.java This DAO class provides CRUD database operations for the
 * table users in the database.
 *
 * @property c una conexión
 * @constructor Create empty UserDAO
 *
 * @author edu
 */

class LibroDAO(private val c: Connection) {

    // En el companion object creamos todas las constantes.
    // Las constante definirán las plantillas de las sentencias que necesitamos para construir
    // los selects, inserts, deletes, updates.

    // En aquellos casos en donde necesitemos insertar un parametro, pondremos un ?
    // Luego lo sistituiremos llamando a métodos setX, donde X será (Int, String, ...)
    // dependiendo del tiempo de dato que corresponda.
    companion object {
        private const val SCHEMA = "default"
        private const val TABLE = "BOOKS"
        private const val TRUNCATE_TABLE_BOOKS_SQL = "TRUNCATE TABLE BOOKS"
        private const val CREATE_TABLE_BOOKS_SQL =
            "create table BOOKS (ID CHAR(5) not null constraint BOOKS_PK primary key, AUTHOR VARCHAR2(50), TITLE VARCHAR2(75), GENRE VARCHAR2(30), PRICE NUMBER(4, 2), PUBLISH_DATE DATE, DESCRIPTION  VARCHAR2(200))"
        private const val INSERT_BOOKS_SQL = "INSERT INTO BOOKS" + "  (ID, AUTHOR, TITLE, GENRE, PRICE, PUBLISH_DATE, DESCRIPTION) VALUES " + " (?, ?, ?, ?, ?, ?, ?)"
        private const val SELECT_BOOKS_BY_ID = "select ID, AUTHOR, TITLE, GENRE, PRICE, PUBLISH_DATE, DESCRIPTION from BOOKS where ID =?"
        private const val SELECT_ALL_BOOKS = "select * from BOOKS"
        private const val DELETE_BOOKS_SQL = "delete from BOOKS where id = ?"
        private const val UPDATE_BOOKS_SQL = "update BOOKS set ID = ?, AUTHOR = ?, TITLE = ?, GENRE = ?, PRICE = ?, PUBLISH_DATE = ?, DESCRIPTION = ? where ID = ?"
    }


    fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (!rs.next())  truncateTable() else createTable()
    }

    private fun truncateTable() {
        println(TRUNCATE_TABLE_BOOKS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_BOOKS_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    private fun createTable() {
        println(CREATE_TABLE_BOOKS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_BOOKS_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    /**
     * Insert Inserta un objeto MyUser en la base de datos.
     * El proceso siempre es el mismo:
     *      Haciendo uso de la conexión, prepara una Statement pasandole la sentencia que se va a ejecutar
     *      en este caso, INSERT_USERS_SQL
     *      A la Statement devuelta se le aplica use
     *          Establecemos los valores por cada ? que existan en la plantilla.
     *          En este caso son 3, pq en INSERT_USERS_SQL hay tres ?
     *          Los indices tienen que ir en el mismo orden en el que aparecen
     *
     *          Finalmente, se ejecuta la Statement
     *          Se llama a commit.
     *
     * @param book
     */
    fun insert(book: Book) {
        println(INSERT_BOOKS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_BOOKS_SQL).use { st ->
                st.setString(1, book.id)
                st.setString(2, book.author)
                st.setString(3, book.title)
                st.setString(4,book.genre)
                st.setDouble(5,book.price)
                st.setDate(6, book.publish_date as Date?) // FIXME: 28/03/2022
                st.setString(7,book.description)
                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectById(id: String): Book? {
        var book: Book? = null
        // Step 1: Preparamos la Statement, asignado los valores a los indices
        //          en función de las ? que existen en la plantilla
        try {
            c.prepareStatement(SELECT_BOOKS_BY_ID).use { st ->
                st.setString(1, id)
                println(st)
                // Step 3: Ejecuta la Statement
                val rs = st.executeQuery()

                // Step 4: Procesamos el objeto ResultSet (rs), mientras tenga valores.
                //          En este caso, si hay valores, tendrá un unico valor, puesto
                //          que estamos buscando por el ID, que es la clave primaria.
                while (rs.next()) {
                    val author = rs.getString("author")
                    val title = rs.getString("title")
                    val genre = rs.getString("genre")
                    val price = rs.getDouble("price")
                    val publish_date = rs.getDate("publish_date")
                    val description = rs.getString("description")
                    book = Book(id,author,title,genre,price,publish_date,description)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return book
    }

    fun selectAll(): List<Book> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val books: MutableList<Book> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL_BOOKS).use { st ->
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getString("id")
                    val author = rs.getString("author")
                    val title = rs.getString("title")
                    val genre = rs.getString("genre")
                    val price = rs.getDouble("price")
                    val publish_date = rs.getDate("publish_date")
                    val description = rs.getString("description")
                    books.add(Book(id,author,title,genre,price,publish_date,description))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return books
    }

    fun deleteById(id: String): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE_BOOKS_SQL).use { st ->
                st.setString(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }

    fun update(book: Book): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_BOOKS_SQL).use { st ->
                st.setString(1, book.id)
                st.setString(2, book.author)
                st.setString(3, book.title)
                st.setString(4, book.genre)
                st.setDouble(5, book.price)
                st.setDate(6, book.publish_date as Date?) // FIXME: 28/03/2022
                st.setString(7, book.description)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }

    private fun printSQLException(ex: SQLException) {
        for (e in ex) {
            if (e is SQLException) {
                e.printStackTrace(System.err)
                System.err.println("SQLState: " + e.sqlState)
                System.err.println("Error Code: " + e.errorCode)
                System.err.println("Message: " + e.message)
                var t = ex.cause
                while (t != null) {
                    println("Cause: $t")
                    t = t.cause
                }
            }
        }
    }


}