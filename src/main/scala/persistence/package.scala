import zio.{ Has, RIO, Task }
import doobie.util.transactor.Transactor
import model.Product

import java.util.UUID

package object persistence {

  object Persistence {
    trait Service[A] {
      def getProduct(id: UUID): Task[A]
      def getProductsByVendor(vendor: String): Task[Seq[A]]
      def createProduct(p: A): Task[A]
    }
  }

  type DBTransactor       = Has[Transactor[Task]]
  type ProductPersistence = Has[Persistence.Service[Product]]

  def getProduct(id: UUID): RIO[ProductPersistence, Product] =
    RIO.accessM(_.get.getProduct(id))

  def getProductsByVendor(
      vendor: String
  ): RIO[ProductPersistence, Seq[Product]] =
    RIO.accessM(_.get.getProductsByVendor(vendor))

  def createProduct(p: Product): RIO[ProductPersistence, Product] =
    RIO.accessM(_.get.createProduct(p))

}
