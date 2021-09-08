package persistence

import scala.concurrent.ExecutionContext
import cats.effect.Blocker
import configuration.DbConfig
import doobie.h2.H2Transactor
import doobie.implicits._
import doobie.h2.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatimedrivernative._
import doobie.{ Query0, Transactor, Update0 }
import zio._
import zio.blocking.Blocking
import model._
import zio.interop.catz._

import java.util.UUID

final class ProductPersistenceService(tnx: Transactor[Task]) extends Persistence.Service[Product] {

  import ProductPersistenceService._

  def getProduct(id: UUID): Task[Product] =
    SQL
      .getProduct(id)
      .option
      .transact(tnx)
      .foldM(
        err => Task.fail(err),
        maybeProduct => Task.require(ProductNotFound(id))(Task.succeed(maybeProduct))
      )

  def getProductsByVendor(vendor: String): Task[Seq[Product]] =
    SQL
      .getProductsByVendor(vendor)
      .to[List]
      .transact(tnx)

  def createProduct(p: Product): Task[Product] =
    SQL
      .createProduct(p)
      .run
      .transact(tnx)
      .foldM(err => Task.fail(err), _ => Task.succeed(p))

  def getAllProducts: Task[Seq[Product]] =
    SQL.getAllProducts
      .to[List]
      .transact(tnx)

}

object ProductPersistenceService {

  object SQL {

    def getProduct(id: UUID): Query0[Product] =
      sql"""SELECT * FROM Product WHERE ID = $id """.query[Product]

    def getProductsByVendor(vendor: String): Query0[Product] =
      sql""" SELECT * FROM Product WHERE vendor = $vendor """.query

    def getAllProducts: Query0[Product] =
      sql""" SELECT * FROM Product """.query

    def createProduct(product: Product): Update0 =
      sql"""INSERT INTO Product (id, name, price, vendor, expiration)
            VALUES (${product.id}, ${product.name}, ${product.price}, ${product.vendor}, ${product.expiration})""".update

    def createProductTable: doobie.Update0 =
      sql"""
        CREATE TABLE Product (
          id   UUID NOT NULL PRIMARY KEY,
          name VARCHAR NOT NULL,
          price DOUBLE NOT NULL,
          vendor VARCHAR NOT NULL,
          expiration DATE 
        )
        """.update
  }

  def createProductTable: ZIO[DBTransactor, Throwable, Unit] =
    for {
      tnx <- ZIO.service[Transactor[Task]]
      _ <-
        SQL.createProductTable.run
          .transact(tnx)
    } yield ()

  def mkTransactor(
      conf: DbConfig,
      connectEC: ExecutionContext,
      transactEC: ExecutionContext
  ): Managed[Throwable, Transactor[Task]] = {
    import zio.interop.catz._

    H2Transactor
      .newH2Transactor[Task](
        conf.url,
        conf.user,
        conf.password,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
      .toManagedZIO
  }

  val transactorLive: ZLayer[Has[DbConfig] with Blocking, Throwable, DBTransactor] =
    ZLayer.fromManaged(for {
      config    <- configuration.dbConfig.toManaged_
      connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
      blockingEC <- blocking.blocking {
        ZIO.descriptor.map(_.executor.asEC)
      }.toManaged_
      transactor <- mkTransactor(config, connectEC, blockingEC)
    } yield transactor)

  val live: ZLayer[DBTransactor, Throwable, ProductPersistence] =
    ZLayer.fromService(new ProductPersistenceService(_))

}
