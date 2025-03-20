import java.sql.{Connection, ResultSet, Timestamp}
import java.util.Calendar

def getAllLog(conn: Connection): Unit = {
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

def addLog(conn: Connection): Unit = {
  val sql = "INSERT INTO operation_log (operation, date) VALUES (?, ?)"

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, "prova")
  stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTime.getTime))
  stmt.executeUpdate()
  stmt.close()
}