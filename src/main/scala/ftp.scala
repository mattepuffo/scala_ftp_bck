import java.sql.{Connection, ResultSet}

def getAllServer(conn: Connection): Unit = {
  val sql = "SELECT * FROM ftp"

  val stmt = conn.prepareStatement(sql)
  val res: ResultSet = stmt.executeQuery()
  while ( {
    res.next
  }) {
    val name = res.getString("name")
    val host = res.getString("host")
    val username = res.getString("username")
    val password = res.getString("username")
    println(username + " " + host)
  }

  stmt.close()
}

def addServer(conn: Connection): Unit = {
  val sql =
    """
      INSERT INTO ftp (name, host, username, password)
      VALUES (?, ?, ?, ?)
      ON CONFLICT (name) DO UPDATE SET host = ?, username = ?, password = ?
    """

  val stmt = conn.prepareStatement(sql)
  stmt.setString(1, "server")
  stmt.setString(2, "server")
  stmt.setString(3, "server")
  stmt.setString(4, "server")
  // UPDATE
  stmt.setString(5, "server")
  stmt.setString(6, "server")
  stmt.setString(7, "server")
  stmt.executeUpdate()
  stmt.close()
}