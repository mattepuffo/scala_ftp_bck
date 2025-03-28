import fansi.Color

import java.sql.{Connection, DriverManager}

def createConn(): Connection = {
  DriverManager.getConnection("jdbc:sqlite:./DATABASE/my_db.db")
}

def createDb(conn: Connection): Unit = {
  println(Color.Green("====="))
  println(Color.Green("CREAZIONE TABELLE"))

  val sql =
    """
       CREATE TABLE IF NOT EXISTS operation_log (id INTEGER PRIMARY KEY, operation VARCHAR(255), date TIMESTAMP);
       CREATE TABLE IF NOT EXISTS sync (name VARCHAR(255), directory VARCHAR(255), server VARCHAR(255), path VARCHAR(255), type VARCHAR(255));
       CREATE UNIQUE INDEX name_uq ON sync (name);
       CREATE TABLE IF NOT EXISTS ftp (name VARCHAR, host VARCHAR, username VARCHAR, password VARCHAR);
       CREATE UNIQUE INDEX ftp_uq ON ftp (name);
    """

  val stmt = conn.createStatement()
  stmt.executeUpdate(sql)
  stmt.close()

  println(Color.Green("TABELLE CREATE"))
  println(Color.Green("====="))
}