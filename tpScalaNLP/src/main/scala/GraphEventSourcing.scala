import Graph._


object GraphEventSourcing {
  var _id = 0

  def nextId: Int = {
    _id += 1
    _id
  }

  var eventLogger = Seq.empty[GraphEvent]

  def list: List[MyGraph] = {
    eventLogger.foldLeft(List.empty[MyGraph])({
      case (acc,  GraphCreated(id, titre,contenu, _)) => {
        acc :+ Graph.MyGraph(Some(id), titre,contenu)
      }
      case (acc, GraphTitreChanged(id, newTitre,contenu, _)) => {
        acc.map(p => if (p.id.get == id) p.copy(titre = newTitre) else p)
      }
    })
  }

  def graphList: List[MyGraph] = list

  def graphLookup (id: Long) = list.find(_.id.get == id)

  def graphCreate (titre: String,contenu:String) = {
    val id = nextId
    eventLogger :+= GraphCreated(id, titre,contenu)
  }

  def graphRename (id: Long, newTitre: String,contenu:String) = {
    val p = list.find(_.id.get == id)
    p match {
      case Some(_) =>
        eventLogger :+= GraphTitreChanged(id, newTitre,contenu)
        list.find(_.id.get == id)
      case None => None
    }
  }
}

