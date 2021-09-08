package persistence

import model.Product
import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestEnvironment

import java.util.UUID

object ProductPersistenceTest extends DefaultRunnableSpec {

  def spec =
    suite("Persistence unit tests")(
      testM("Getting a non existing product should fail") {
        assertM(getProduct(UUID.randomUUID()).run)(fails(anything))
      },
      testM("Create a product then get it ") {
        val reference = Product(
          id = UUID.randomUUID(),
          name = "iProduct",
          price = 1500.5,
          vendor = "Apple",
          expiration = None
        )
        for {
          created <- createProduct(reference)
          product <- getProduct(created.id)
        } yield assert(created)(equalTo(reference)) &&
          assert(product)(equalTo(reference))
      },
      testM("Get list of products for vendor ") {
        val referenceA = Product(
          id = UUID.randomUUID(),
          name = "iFirstProduct",
          price = 1500.5,
          vendor = "Apple",
          expiration = None
        )
        val referenceB = Product(
          id = UUID.randomUUID(),
          name = "iSecondProduct",
          price = 1300.5,
          vendor = "Apple",
          expiration = None
        )
        val referenceC = Product(
          id = UUID.randomUUID(),
          name = "OtherProduct",
          price = 500.5,
          vendor = "Xiaomi",
          expiration = None
        )
        val expectedResult = List(referenceA, referenceB)
        for {
          _      <- createProduct(referenceA)
          _      <- createProduct(referenceB)
          _      <- createProduct(referenceC)
          result <- getProductsByVendor("Apple")
        } yield assert(result)(equalTo(expectedResult))
      }
    ).provideSomeLayer[TestEnvironment](Test.layer)
}
