package io.jterrier.fiprecorder

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.Lens

data class DbUrl(val value: String)
data class DbUser(val value: String)
data class DbPassword(val value: String)

data class DatabaseConfig(
    val url: DbUrl,
    val user: DbUser,
    val password: DbPassword
)

data class ClientId(val value: String)
data class ClientSecret(val value: String)

data class SpotifyCredentials(
    val clientId: ClientId,
    val clientSecret: ClientSecret
)

val env = Environment.ENV

val dbUrlLens: Lens<Environment, DbUrl> = EnvironmentKey.map(::DbUrl).required("DB_URL")
val dbUserLens: Lens<Environment, DbUser> = EnvironmentKey.map(::DbUser).required("DB_USER")
val dbPasswordLens: Lens<Environment, DbPassword> = EnvironmentKey.map(::DbPassword).required("DB_PASSWORD")

val clientIdLens: Lens<Environment, ClientId> = EnvironmentKey.map(::ClientId).required("SPOTIFY_CLIENT_ID")
val clientSecretLens: Lens<Environment, ClientSecret> = EnvironmentKey.map(::ClientSecret).required("SPOTIFY_CLIENT_SECRET")

val databaseConfig = DatabaseConfig(
    url = dbUrlLens(env),
    user = dbUserLens(env),
    password = dbPasswordLens(env)
)

val spotifyConfig = SpotifyCredentials(
    clientId = clientIdLens(env),
    clientSecret = clientSecretLens(env),
)