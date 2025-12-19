package com.autotagauditor

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.slf4j.LoggerFactory
import java.io.FileInputStream

object FirestoreService {
    // db variable holds the connection to the database
    private val db: Firestore

    private val logger = LoggerFactory.getLogger("FirestoreService")

    init{
        try {
            // Find the Key file to connect to Firebase
            val serviceAccount = FileInputStream("service-account.json")
            // Configure Firebase with those keys
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            // Connect to Firebase, Check first if app is already running
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }

            db = FirestoreClient.getFirestore()
            logger.info("Success: Connected to Firebase Database")
        } catch (e: Exception) {
            logger.error("Error connecting to Firebase Database", e)
            throw e
        }

    }

    fun saveScan(scanResult: AuditResult) {
        // Map to store the audit results
        val data = mapOf(
            "url" to scanResult.url,
            "tags" to scanResult.tagsFound,
            "status" to scanResult.status,
            // Add exact time item was saved
            "timestamp" to com.google.cloud.Timestamp.now()
        )

        //"scans" the name of the Collection in the firebase database
        // use .add(data) to instantly create a new document within the collection
        db.collection("scans").add(data)

        logger.info("Successfully saved ${scanResult.url} to Firebase Database under Collection: ${db.collection("scans")}")
    }

}