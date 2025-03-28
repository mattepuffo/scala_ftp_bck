import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import java.sql.{Connection, ResultSet, Timestamp}
import java.util.Calendar

def getAllLog(conn: Connection): Unit = {
  val sql = "SELECT * FROM operation_log"

  val stmt = conn.prepareStatement(sql)
  val res: ResultSet = stmt.executeQuery()

  val at = new AsciiTable()
  at.addRule()
  val row = at.addRow("ID", "OPERAZIONE", "DATA")
  row.setTextAlignment(TextAlignment.CENTER)
  at.addRule()

  while ( {
    res.next
  }) {
    val id = res.getInt("id")
    val operation = res.getString("operation")
    val dtm = res.getTimestamp("date")

    at.addRow(id, operation, dtm.toString)
    at.addRule()
  }

  val table = at.render()
  println(table)

  stmt.close()
}

def addLog(conn: Connection): Unit = {
  val sql = "INSERT INTO operation_log (id, operation, date) VALUES (NULL, ?, ?)"

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, "prova")
  stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTime.getTime))
  stmt.executeUpdate()
  stmt.close()
}