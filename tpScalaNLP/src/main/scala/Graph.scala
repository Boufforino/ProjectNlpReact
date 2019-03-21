import java.time.Instant

object Graph {
  case class MyGraph (id: Option[Long], titre: String,contenu:String)

  sealed trait GraphEvent

  final case class GraphCreated (id: Long, titre: String,contenu:String, date: Instant = Instant.now()) extends GraphEvent

  final case class GraphTitreChanged (id: Long, newTitre: String,contenu:String, date: Instant = Instant.now()) extends GraphEvent

}
