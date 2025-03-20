import java.io.File

def checkDbExists(): Boolean = {
//  File("./DATABASE/my_db.db").delete()
  File("./DATABASE/my_db.db").exists()
}