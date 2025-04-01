import fansi.{Color, Str}

import java.sql.Connection
import scala.io.StdIn.readLine

@main
def main(): Unit = {
  val conn = createConn()

  if (!checkDbExists()) {
    createDb(conn)
  } else {
    println(Color.Green("====="))
    println(Color.Green("DATABASE ESISTENTE"))
    println(Color.Green("====="))
  }

  test(conn)

  //  var running = true
  //  while running do
  //    createMenu()
  //    readLine() match
  //      case "1" => println("Ciao! Come stai?")
  //      case "2" => syncMenu(conn)
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

def syncMenu(conn: Connection): Unit = {
  var subRunning = true
  while subRunning do
    println("=== GESTIONE SYNC ===")
    println("1. VISUALIZZA SYNC")
    println("2. AGGIUNGI SYNC")
    println("3. CANCELLA SYNC")
    println("4. INDIETRO")

    readLine() match
      case "1" => getAllSync(conn)
      case "2" => {
        println(Color.Blue("SCRIVI DUE VALORI DEL SYNC"))
        println(Color.Blue("NOME, PATH DA COMPRIMERE E SERVER SEPARATI DAL CARATTERE |"))
        println(Color.Yellow("NOTA: CONVIENE PRIMA CREARE IL SERVER E ANNOTARSI IL NOME"))
        println(Color.Yellow("AD ESEMPIO: nome1 | /home/fermat | server1 | cartella"))

        val input = readLine()
        val options = input.split("\\|")

        addSync(conn, options(0).trim, options(1).trim, options(2).trim, options(3).trim, options(4).trim)
      }
      case "3" => {
        println(Color.Blue("CANCELLA UN SYNC"))
        println(Color.Blue("SCRIVI IL NOME DEL SYNC CHE VUOI CANCELLARE"))
        println(Color.Yellow("AD ESEMPIO: nome1"))

        val input = readLine()

        delSync(conn, input.trim)
      }
      case "4" => subRunning = false
      case _ => println(Color.Red("NESSUNA VOCE CORRISPONDENTE..."))
}

def test(conn: Connection): Unit = {
  val res = getSyncByName(conn, "name")
  println(res(0))
  println(res(1))
}