import Text._

object TextEventSourcing {
  var _id = 0

  def nextId: Int = {
    _id += 1
    _id
  }

  var eventLogger = Seq.empty[TextEvent]

  def list: List[MyText] = {
    eventLogger.foldLeft(List.empty[MyText])({
      case (acc,  TextCreated(id, titre,contenu, _)) => {
        acc :+ Text.MyText(Some(id), titre,contenu)
      }
      case (acc, TextTitreChanged(id, newTitre,contenu, _)) => {
        acc.map(p => if (p.id.get == id) p.copy(titre = newTitre) else p)
      }
    })
  }

  def textList: List[MyText] = list

  def textLookup (id: Long) = list.find(_.id.get == id)

  def textCreate (titre: String,contenu:String) = {
    val id = nextId
    eventLogger :+= TextCreated(id, titre,contenu)
  }

  def textRename (id: Long, newTitre: String,contenu:String) = {
    val p = list.find(_.id.get == id)
    p match {
      case Some(_) =>
        eventLogger :+= TextTitreChanged(id, newTitre,contenu)
        list.find(_.id.get == id)
      case None => None
    }
  }
}

