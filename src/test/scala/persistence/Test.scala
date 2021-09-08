package persistence

import model.{ Product, ProductNotFound, VendorNotFound }
import zio.{ Ref, Task, ZLayer }

import java.util.UUID

case class Test(products: Ref[Vector[Product]]) extends Persistence.Service[Product] {

  private def evalProducts(
      products: List[Product],
      vendor: String
  ): Option[List[Product]] = {
    products.filter(_.vendor == vendor) match {
      case Nil          => None
      case head :: tail => Some(head :: tail)
    }

  }

  def getProduct(id: UUID): Task[Product] =
    products.get.flatMap(products => Task.require(ProductNotFound(id))(Task.succeed(products.find(_.id == id))))

  def createProduct(product: Product): Task[Product] =
    products.update(_ :+ product).map(_ => product)

  def getProductsByVendor(vendor: String): Task[List[Product]] =
    products.get.flatMap(products =>
      Task.require(VendorNotFound(vendor))(
        Task.succeed(evalProducts(products.toList, vendor))
      )
    )
}

object Test {

  val layer: ZLayer[Any, Nothing, ProductPersistence] =
    Ref.make(Vector.empty[Product]).map(Test(_)).toLayer

}
