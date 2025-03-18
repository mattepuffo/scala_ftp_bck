import java.sql.{Connection, DriverManager, ResultSet, Timestamp}
import java.util.Calendar

def createConn(): Connection = {
  Class.forName("org.h2.Driver")
  DriverManager.getConnection("jdbc:h2:./DATABASE/my_db", "", "")
}

def createDb(conn: Connection): Unit = {
  val sql =
    """
       CREATE SCHEMA IF NOT EXISTS my_db;
       SET SCHEMA my_db;
       CREATE TABLE IF NOT EXISTS operation_log (id INT AUTO_INCREMENT PRIMARY KEY, operation VARCHAR(255), date TIMESTAMP);
   """

  val stmt = conn.createStatement()
  stmt.execute(sql)
  stmt.close()
}

def insertData(conn: Connection): Unit = {
  val sql = "INSERT INTO operation_log (operation, date) VALUES (?, ?)"

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, "prova")
  stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTime.getTime))
  stmt.executeUpdate()
  stmt.close()
}

def selectData(conn: Connection): Unit = {
  val sql = "SELECT * FROM operation_log"

  val stmt = conn.prepareStatement(sql)
  val res: ResultSet = stmt.executeQuery()
  while ( {
    res.next
  }) {
    val id = res.getInt("id")
    val operation = res.getString("operation")
    val dtm = res.getTimestamp("date")
    println(id + " " + operation + " " + dtm.toString)
  }

  stmt.close()
}