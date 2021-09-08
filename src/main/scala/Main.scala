import cats.effect.{ExitCode => CatsExitCode}
import configuration.Configuration
import api.Api
import persistence.{DBTransactor, ProductPersistence, ProductPersistenceService}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.putStrLn
import zio.interop.catz._

object Main extends App {

  type AppEnvironment = Configuration
    with Clock
    with DBTransactor
    with ProductPersistence

  type AppTask[A] = RIO[AppEnvironment, A]

  val appEnvironment =
    Configuration.live >+> Blocking.live >+> ProductPersistenceService.transactorLive >+> ProductPersistenceService.live

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    val program: ZIO[AppEnvironment, Throwable, Unit] =
      for {
        _ <- ProductPersistenceService.createProductTable
        api <- configuration.apiConfig
        httpApp = Router[AppTask](
          "/products" -> Api(s"${api.endpoint}/products").route
        ).orNotFound

        server <- ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(api.port, api.endpoint)
            .withHttpApp(CORS(httpApp))
            .serve
            .compile[AppTask, AppTask, CatsExitCode]
            .drain
        }
      } yield server

    program
      .provideSomeLayer[ZEnv](appEnvironment)
      .tapError(err => putStrLn(s"Execution failed with: $err"))
      .exitCode
  }
}
