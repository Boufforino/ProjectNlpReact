import java.time.Instant

object Model {

  case class MyProduct (id: Option[Long], label: String, price: Double)

  sealed trait ProductEvent

  final case class ProductCreated (id: Long, label: String, price: Double, date: Instant = Instant.now()) extends ProductEvent

  final case class ProductPriceChanged (id: Long, newPrice: Double, date: Instant = Instant.now()) extends ProductEvent

  final case class ProductLabelChanged (id: Long, label: String, date: Instant = Instant.now()) extends ProductEvent

  final case class ProductDeleted (id: Long, date: Instant = Instant.now()) extends ProductEvent

}
