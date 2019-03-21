import java.time.Instant

import Model._
import akka.actor.{Actor, ActorRef}
import cats.effect.IO

/*
case class MyProduct(id: Option[Long], label: String, price: Double)
trait ProductService {
  def save(product : MyProduct) : IO[Long]
}

*/

object EventSourcing {
  var _id = 0

  def nextId: Int = {
    _id += 1
    _id
  }

  var eventLogger = Seq.empty[ProductEvent]

  def list: List[MyProduct] = {
    eventLogger.foldLeft(List.empty[MyProduct])({
      case (acc, ProductCreated(id, label, price, _)) => {
        acc :+ Model.MyProduct(Some(id), label, price)
      }
      case (acc, ProductLabelChanged(id, newLabel, _)) => {
        acc.map(p => if (p.id.get == id) p.copy(label = newLabel) else p)
      }
      case (acc, ProductPriceChanged(id, newPrice, _)) => {
        acc.map(p => if (p.id.get == id) p.copy(price = newPrice) else p)
      }
      case (acc, ProductDeleted(id, _)) => {
        acc.filter(_.id.get != id)
      }
    })
  }

  def productList: List[MyProduct] = list

  def productLookup (id: Long) = list.find(_.id.get == id)

  def productCreate (label: String, price: Double) = {
    val id = nextId
    eventLogger :+= ProductCreated(id, label, price)
  }

  def productRename (id: Long, newLabel: String) = {
    val p = list.find(_.id.get == id)
    p match {
      case Some(_) =>
        eventLogger :+= ProductLabelChanged(id, newLabel)
        list.find(_.id.get == id)
      case None => None
    }
  }

  def productChangePrice (id: Long, newPrice: Double) = {
    val p = list.find(_.id.get == id)
    p match {
      case Some(_) =>
        eventLogger :+= ProductPriceChanged(id, newPrice)
        list.find(_.id.get == id)
      case None => None
    }
  }

  def productDelete (id: Long) = {
    list.find(_.id.get == id).map({p=>eventLogger :+= ProductDeleted(p.id.get);"Done"})
  }
}

