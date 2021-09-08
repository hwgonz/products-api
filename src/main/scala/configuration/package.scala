import zio._
import pureconfig.ConfigSource

package object configuration {

  type Configuration = Has[ApiConfig] with Has[DbConfig]

  final case class ProductsConfig(api: ApiConfig, dbConfig: DbConfig)
  final case class ApiConfig(endpoint: String, port: Int)
  final case class DbConfig(url: String, user: String, password: String)

  val apiConfig: URIO[Has[ApiConfig], ApiConfig] = ZIO.service
  val dbConfig: URIO[Has[DbConfig], DbConfig] = ZIO.service

  object Configuration {
    import pureconfig.generic.auto._
    val live: Layer[Throwable, Configuration] = Task
      .effect(ConfigSource.default.loadOrThrow[ProductsConfig])
      .map(config => Has(config.api) ++ Has(config.dbConfig))
      .toLayerMany
  }
}
