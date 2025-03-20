import fansi.Color

import java.sql.{Connection, DriverManager}

def createConn(): Connection = {
  Class.forName("org.h2.Driver")
  DriverManager.getConnection("jdbc:h2:./DATABASE/my_db", "", "")
}

def createDb(conn: Connection): Unit = {
  println(Color.Green("====="))
  println(Color.Green("CREAZIONE TABELLE"))

  val sql =
    """
       CREATE SCHEMA IF NOT EXISTS my_db;
       SET SCHEMA my_db;
       CREATE TABLE IF NOT EXISTS operation_log (id INT AUTO_INCREMENT PRIMARY KEY, operation VARCHAR(255), date TIMESTAMP);
       CREATE TABLE IF NOT EXISTS sync (name VARCHAR(255), directory VARCHAR(255), server VARCHAR(255), path VARCHAR(255));
       CREATE UNIQUE INDEX name_uq ON sync (name);
       CREATE TABLE IF NOT EXISTS ftp (name VARCHAR, host VARCHAR, username VARCHAR, password VARCHAR);
       CREATE UNIQUE INDEX ftp_uq ON ftp (name);
    """

  val stmt = conn.createStatement()
  stmt.execute(sql)
  stmt.close()

  println(Color.Green("TABELLE CREATE"))
  println(Color.Green("====="))
}