import scala.util.Random

def get(url: String,
        connectTimeout: Int = 5000,
        readTimeout: Int = 5000,
        requestMethod: String = "GET") = {
  import java.net.{URL, HttpURLConnection}
  val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
  connection.setConnectTimeout(connectTimeout)
  connection.setReadTimeout(readTimeout)
  connection.setRequestMethod(requestMethod)
  val inputStream = connection.getInputStream
  val content = scala.io.Source.fromInputStream(inputStream).mkString
  if (inputStream != null) inputStream.close
  content
}
val r = new Random()
val url = "http://localhost:9000/add"
val temps = for (i <- 1 to 100) yield {
  16 + (r.nextFloat() * 6)
}
temps.foreach { temp =>
  val content = get(s"$url/$temp")
  println(content)
}