@main
def main(): Unit = {
  val conn = createConn()
  createDb(conn)
  insertData(conn)
  selectData(conn)
  conn.close()
}

