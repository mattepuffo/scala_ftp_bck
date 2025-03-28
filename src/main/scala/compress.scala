import java.io.{BufferedInputStream, File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.{ZipEntry, ZipOutputStream}

def zipDirectory(sourceDirPath: String, zipFilePath: String): Unit = {
  val currentDateTime: LocalDateTime = LocalDateTime.now()
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd")
  val formattedDate: String = currentDateTime.format(formatter)

  val sourcePath = Paths.get(sourceDirPath)
  val zipFile = new File(zipFilePath + "_" + formattedDate + ".zip")

  new ZipOutputStream(new FileOutputStream(zipFile)).use { zipOut =>
    Files.walk(sourcePath).forEach { path =>
      val file = path.toFile
      if (!file.isDirectory) {
        val relativePath = sourcePath.relativize(path).toString
        val entryName = relativePath.replace(File.separatorChar, '/')
        val zipEntry = new ZipEntry(entryName)
        zipOut.putNextEntry(zipEntry)

        val buffer = new Array[Byte](1024)
        val in = new BufferedInputStream(new FileInputStream(file))
        var bytesRead = 0
        while ( {
          bytesRead = in.read(buffer);
          bytesRead
        } != -1) {
          zipOut.write(buffer, 0, bytesRead)
        }
        in.close()
        zipOut.closeEntry()
      }
    }
  }
}

extension [T <: AutoCloseable](resource: T)
  def use[R](block: T => R): R =
    try block(resource)
    finally resource.close()