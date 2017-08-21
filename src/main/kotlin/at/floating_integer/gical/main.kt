// Copyright (c) 2017 Florian Hassanen

package at.floating_integer.gical

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

const val APP_NAME = "gical"

val AUTH_STORAGE = FileDataStoreFactory(File(System.getProperty("user.home"), ".gical"))

val HTTP = GoogleNetHttpTransport.newTrustedTransport()

val SCOPES = listOf(CalendarScopes.CALENDAR)


val JSON = JacksonFactory.getDefaultInstance()

fun main(args: Array<String>) {
    val cred = GoogleClientSecrets.load(JSON, Files.newBufferedReader(Paths.get("credentials.json")))

    val flow = GoogleAuthorizationCodeFlow.Builder(HTTP, JSON, cred, SCOPES)
            .setDataStoreFactory(AUTH_STORAGE)
            .setAccessType("offline").build()

    val user = AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("florian.hassanen")

    val cal = Calendar.Builder(HTTP, JSON, user).setApplicationName(APP_NAME).build()

    cal.CalendarList().list().execute().items.filter { it.summary == "hockn" }.firstOrNull()?.let {
        val event = Event()
                .setSummary("auto")
                .setLocation("Vienna, AT")
                .setStart(EventDateTime().setDateTime(DateTime("2017-08-21T13:00:00+02:00")))
                .setEnd(EventDateTime().setDateTime(DateTime("2017-08-21T14:00:00+02:00")))

        cal.events().insert(it.id, event).execute()
    }
}
