package com.autotagauditor

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import kotlinx.serialization.Serializable

@Serializable
data class AuditResult(
    val url: String,
    val tagsFound: List<String>,
    val status: String,
    val aiAnalysis: String? = "Analyzing..."
)
object BotService {
    fun auditUrl(url: String): AuditResult {
        //Set Up Chrome Driver
        WebDriverManager.chromedriver().setup()

        //Headless Chrome
        val options = ChromeOptions()
        options.addArguments("--headless")
        options.addArguments("--disable-gpu")
        options.addArguments("--remote-allow-origins=*")

        //Initialize Browser
        val driver = ChromeDriver(options)

        val foundTags = mutableListOf<String>()

        try {
            //Go to website
            driver.get(url)

            //HTML Website Source Code
            val pageSource = driver.pageSource ?: ""

            //KeyWord Search
            if (pageSource.contains("GTM-")) foundTags.add("Google Tag Manager")
            if (pageSource.contains("UA-")) foundTags.add("Universal Analytics (Legacy)")
            if (pageSource.contains("G-")) foundTags.add("Google Analytics 4")

            return AuditResult(url, foundTags, "Success")
        } catch (e: Exception) {
            return AuditResult(url, emptyList(), "Error: ${e.message}")
        } finally {
            // Always close browser, RAM issues otherwise
            driver.quit()
        }
    }
}