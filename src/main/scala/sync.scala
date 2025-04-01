import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

import java.sql.{Connection, ResultSet}

def getAllSync(conn: Connection): Unit = {
  val sql = "SELECT * FROM sync"

  val stmt = conn.prepareStatement(sql)
  val res: ResultSet = stmt.executeQuery()

  val at = new AsciiTable()
  at.addRule()
  val row = at.addRow("NOME", "DIRECTORY", "SERVER", "PATH", "TYPE")
  row.setTextAlignment(TextAlignment.CENTER)
  at.addRule()

  while ( {
    res.next
  }) {
    val name = res.getString("name")
    val directory = res.getString("directory")
    val server = res.getString("server")
    val path = res.getString("path")
    val typeSync = res.getString("type")

    at.addRow(name, directory, server, path, typeSync)
    at.addRule()
  }

  val table = at.render()
  println(table)

  stmt.close()
}

def getSyncByName(conn: Connection, name: String): (Int, String) = {
  var strName = "";
  var count = 0;
  val sql = "SELECT * FROM sync WHERE name = ?"

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, name)

  val res: ResultSet = stmt.executeQuery()

  if (res.next()) {
    strName = res.getString("name")
    count += count + 1
  }

  stmt.close()

  (count, strName)
}

def addSync(conn: Connection, name: String, dir: String, server: String, path: String, typeSync: String): Unit = {
  val sql =
    """
      INSERT INTO sync (name, directory, server, path, type)
      VALUES (?, ?, ?, ?, ?)
      ON CONFLICT (name) DO UPDATE SET directory = ?, server = ?, path = ?, type = ?
    """

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, name)
  stmt.setString(2, dir)
  stmt.setString(3, server)
  stmt.setString(4, path)
  stmt.setString(5, typeSync)
  // UPDATE
  stmt.setString(6, dir)
  stmt.setString(7, server)
  stmt.setString(8, path)
  stmt.setString(9, typeSync)

  stmt.executeUpdate()
  stmt.close()
}

def delSync(conn: Connection, name: String): Unit = {
  val sql =
    """
      DELETE FROM sync WHERE name = ?
    """

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, name)

  stmt.executeUpdate()
  stmt.close()
}