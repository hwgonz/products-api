package api

import cats.data.Validated.{ Invalid, Valid }
import io.circe.generic.auto._
import io.circe.{ Decoder, Encoder }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import zio._
import zio.interop.catz._
import model.{ Product, ProductValidator }
import persistence._

final case class Api[R <: ProductPersistence](rootUri: String) {

  type ProductTask[A] = RIO[R, A]

  implicit def circeJsonDecoder[A](implicit
      decoder: Decoder[A]
  ): EntityDecoder[ProductTask, A] = jsonOf[ProductTask, A]

  implicit def circeJsonEncoder[A](implicit
      decoder: Encoder[A]
  ): EntityEncoder[ProductTask, A] =
    jsonEncoderOf[ProductTask, A]

  val dsl: Http4sDsl[ProductTask] = Http4sDsl[ProductTask]
  import dsl._

  import ProductValidator._

  object VendorQueryParamMatcher extends QueryParamDecoderMatcher[String]("vendor")

  def route: HttpRoutes[ProductTask] = {

    HttpRoutes.of[ProductTask] {

      case GET -> Root / UUIDVar(id) =>
        getProduct(id).foldM(_ => NotFound(), Ok(_))

      case GET -> Root :? VendorQueryParamMatcher(vendor) =>
        getProductsByVendor(vendor).foldM(_ => NotFound(), Ok(_))

      case request @ POST -> Root =>
        request.decode[Product] { product =>
          val validationResults = ProductValidator.validateProduct(product)
          validationResults match {
            case Valid(aProduct) => Created(createProduct(aProduct))
            case Invalid(errors) => BadRequest(errors)
          }

        }

    }
  }

}
