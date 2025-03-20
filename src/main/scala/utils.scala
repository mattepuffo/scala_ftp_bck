import java.io.File

def checkDbExists(): Boolean = {
//  File("./DATABASE/my_db.mv.db").delete()
  File("./DATABASE/my_db.mv.db").exists()
}