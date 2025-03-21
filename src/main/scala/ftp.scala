import fansi.Color
import org.apache.commons.net.ftp.{FTP, FTPClient}

import java.io.{FileInputStream, IOException}
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

def upload(server: String, username: String, password: String, dirToUpload: String, fileUpload: String, remoteName: String): Unit = {
  val port = 21

  val ftpClient = FTPClient()
  try
    ftpClient.connect(server, port)
    val success = ftpClient.login(username, password)

    if !success then
      println(Color.Red("Impossibile collegarsi al server"))
      return

    ftpClient.cwd(dirToUpload)
    ftpClient.enterLocalPassiveMode()
    ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

    val localFilePath = fileUpload
    val remoteFilePath = remoteName
    val inputStream = FileInputStream(localFilePath)

    println(Color.Yellow("Inizio caricamento..."))
    val done = ftpClient.storeFile(remoteFilePath, inputStream)
    inputStream.close()

    if done then println(Color.Green("File caricato correttamente"))
    else println(Color.Red("Non è stato possibile caricare il file"))

  catch
    case ex: IOException => Color.Red(ex.getMessage)
  finally
    try
      if ftpClient.isConnected then
        ftpClient.logout()
        ftpClient.disconnect()
    catch
      case ex: IOException => Color.Red(ex.getMessage)
}