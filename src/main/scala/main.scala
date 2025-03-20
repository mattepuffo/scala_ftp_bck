import fansi.Color

import scala.io.StdIn.readLine

@main
def main(): Unit = {
  if (!checkDbExists()) {
    val conn = createConn()
    createDb(conn)
    conn.close()
  } else {
    println(Color.Green("====="))
    println(Color.Green("DATABASE ESISTENTE"))
    println(Color.Green("====="))
  }

  test()

  //  var running = true
  //  while running do
  //    createMenu()
  //    readLine() match
  //      case "1" => println("Ciao! Come stai?")
  //      case "2" => syncMenu()
  //      case "3" => println(s"Data e ora attuale: ${java.time.LocalDateTime.now}")
  //      case "4" => println(s"Data e ora attuale: ${java.time.LocalDateTime.now}")
  //      case "5" => println(s"Data e ora attuale: ${java.time.LocalDateTime.now}")
  //      case "6" =>
  //        println(Color.Yellow("USCITA IN CORSO..."))
  //        running = false
  //      case _ => println(Color.Red("NESSUNA VOCE CORRISPONDENTE..."))
  //
  //  println(Color.Yellow("PROGRAMMA TERMINATO"))
}

def createMenu(): Unit = {
  println("=== SELEZIONA UN'OPZIONE ===")
  println("1. FTP")
  println("2. SYNC")
  println("3. LOG")
  println("4. BACKUP DB")
  println("5. PULISCI BCK")
  println("6. ESCI")
}

def syncMenu(): Unit = {
  var subRunning = true
  while subRunning do
    println("=== GESTIONE SYNC ===")
    println("1. VISUALIZZA SYNC")
    println("2. AGGIUNGI SYNC")
    println("3. CANCELLA SYNC")
    println("4. INDIETRO")

    readLine() match
      case "1" => println(s"Data e ora attuale: ${java.time.LocalDateTime.now}")
      case "2" => println("Messaggio segreto: Scala è fantastico! 🚀")
      case "3" => println("Messaggio segreto: Scala è fantastico! 🚀")
      case "4" => subRunning = false
      case _ => println(Color.Red("NESSUNA VOCE CORRISPONDENTE..."))
}

def test(): Unit = {
  zipDirectory("C:\\Personal\\Documenti", "BCK\\documenti.zip")
  //  val conn = createConn()
  //  addLog(conn)
  //  getAllLog(conn)
  //  addSync(conn)
  //  getAllSync(conn)
  //  addServer(conn)
  //  getAllServer(conn)
  //  conn.close()
}