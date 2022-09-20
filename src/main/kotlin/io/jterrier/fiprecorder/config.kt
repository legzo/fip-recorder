import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.Lens

data class DbUrl(val value: String)
data class DbUser(val value: String)
data class DbPassword(val value: String)

val env = Environment.ENV

val dbUrlLens: Lens<Environment, DbUrl> = EnvironmentKey.map(::DbUrl).required("DB_URL")
val dbUserLens: Lens<Environment, DbUser> = EnvironmentKey.map(::DbUser).required("DB_USER")
val dbPasswordLens: Lens<Environment, DbPassword> = EnvironmentKey.map(::DbPassword).required("DB_PASSWORD")

val dbUrl: DbUrl= dbUrlLens(env)
val dbUser: DbUser = dbUserLens(env)
val dbPassword: DbPassword = dbPasswordLens(env)