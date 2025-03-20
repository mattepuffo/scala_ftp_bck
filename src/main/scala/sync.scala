import java.sql.{Connection, ResultSet}

def getAllSync(conn: Connection): Unit = {
  val sql = "SELECT * FROM sync"

  val stmt = conn.prepareStatement(sql)
  val res: ResultSet = stmt.executeQuery()
  while ( {
    res.next
  }) {
    val name = res.getString("name")
    val directory = res.getString("directory")
    val server = res.getString("server")
    val path = res.getString("path")
    println(name + " " + directory + " " + path)
  }

  stmt.close()
}

def addSync(conn: Connection): Unit = {
//  val sql = "INSERT INTO sync (name, directory, server, path) VALUES (?, ?, ?, ?) ON CONFLICT (name) DO UPDATE SET directory = ?, server = ?, path = ?"
  val sql = "MERGE INTO sync (name, directory, server, path) KEY (name) VALUES (?, ?, ?, ?)"

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, "name")
  stmt.setString(2, "dir2")
  stmt.setString(3, "server")
  stmt.setString(4, "path")
  stmt.executeUpdate()
  stmt.close()
}