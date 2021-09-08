package model

import java.time.LocalDate
import java.util.UUID
import cats.data._
import cats.implicits._
import io.circe._
import io.circe.syntax._

final case class Product(
    id: UUID,
    name: String,
    price: Double,
    vendor: String,
    expiration: Option[LocalDate]
)

sealed trait ProductValidationError {
  def errorMessage: String
}

case object InvalidProductId extends ProductValidationError {
  def errorMessage: String = "Product Id can't be empty"
}

case object InvalidProductName extends ProductValidationError {
  def errorMessage: String = "Product name can't contain more than 10 characters"
}

case object InvalidVendorName extends ProductValidationError {
  def errorMessage: String = "Vendor name can't contain more than 10 characters"
}

case object InvalidPrice extends ProductValidationError {
  def errorMessage: String = "Price must be higher than 0"
}

case object InvalidExpirationDate extends ProductValidationError {
  def errorMessage: String = "Expiration date has to be a date in the future"
}

object ProductValidator {

  type ValidationResult[A] = ValidatedNec[ProductValidationError, A]

  implicit val ProductValidationErrorEncoder: Encoder[ProductValidationError] = error =>
    Json.obj(
      "error" -> error.errorMessage.asJson
    )

  def validateProductId(productId: UUID): ValidationResult[UUID] =
    if (productId.toString.nonEmpty) productId.validNec
    else InvalidProductId.invalidNec

  def validateProductName(productName: String): ValidationResult[String] =
    if (productName.length <= 10) productName.validNec
    else InvalidProductName.invalidNec

  def validateVendorName(vendorName: String): ValidationResult[String] =
    if (vendorName.length <= 10) vendorName.validNec
    else InvalidVendorName.invalidNec

  def validateProductPrice(productPrice: Double): ValidationResult[Double] =
    if (productPrice > 0) productPrice.validNec
    else InvalidPrice.invalidNec

  def validateExpirationDate(expirationDate: LocalDate): ValidationResult[LocalDate] =
    if (expirationDate.isAfter(LocalDate.now)) expirationDate.validNec
    else InvalidExpirationDate.invalidNec

  def validateProduct(possibleProduct: Product): ValidationResult[Product] = {

    (
      validateProductId(possibleProduct.id),
      validateProductName(possibleProduct.name),
      validateProductPrice(possibleProduct.price),
      validateVendorName(possibleProduct.vendor),
      possibleProduct.expiration.traverse(validateExpirationDate)
    ).mapN(Product)

  }

}

final case class ProductNotFound(id: UUID) extends Exception

final case class VendorNotFound(vendor: String) extends Exception
