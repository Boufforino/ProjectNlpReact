
import java.time.Instant

object Text {

  case class MyText (id: Option[Long], titre: String,contenu:String)

  sealed trait TextEvent

  final case class TextCreated (id: Long, titre: String,contenu:String, date: Instant = Instant.now()) extends TextEvent

  final case class TextTitreChanged (id: Long, newTitre: String,contenu:String, date: Instant = Instant.now()) extends TextEvent


}
