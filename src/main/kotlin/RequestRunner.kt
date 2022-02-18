package main

import io.ktor.client.HttpClient
import io.ktor.client.features.*
import io.ktor.client.features.HttpTimeout.Feature.install
import io.ktor.client.request.get
import io.ktor.client.response.*
import org.slf4j.LoggerFactory
import java.net.ConnectException

class RequestRunner {

    val log = LoggerFactory.getLogger("Main")!!

    suspend fun runRequest(url: String, substring:String, excludes:List<String>, addwww: Boolean): ResultObject {

        val start = System.currentTimeMillis()
        val page = getPageContent(url, addwww)
        val duration = (System.currentTimeMillis() - start).toInt()

        var newUrls = mutableListOf<String>()
        if (page != null) {
            val lines = page.split("\n")
            for (l in lines) {
                if (l.contains(substring)) {
                    log.trace("Found a relevant line: $l")
                    newUrls.addAll(extractUrls(l,substring, excludes))
                    //Remove duplicates
                    newUrls.distinct()
                }
            }
        } else {
            log.error("No answer for $url")
        }

        val result = ResultObject(urls = newUrls, duration = duration, orgUrl = url)
        return result
    }

    private fun extractUrls(line:String, substring: String, excludes: List<String>):List<String> {
        var urls = mutableListOf<String>()

        //val regex = "href=\"\\w+\"".toRegex()
        val regex = "(href=\")[^\"]+(\")".toRegex()
        val matches = regex.findAll(line)!!

        matches.forEach { matchResult ->
            var u = matchResult.value.drop(6).dropLast(1)
            if (u.startsWith("http://")) {
                u = u.drop(7)
            }
            if (u.startsWith("https://")) {
                u = u.drop(8)
            }
            if (u.startsWith("www.")) {
                u = u.drop(4)
            }
            log.trace("Found " + matchResult.value + " --> " + u)
            var noExcludes = true
            for (e in excludes) {
                if (u.contains(e)) {
                    noExcludes = false
                }
            }
            if (noExcludes && u.contains(substring)) {
                urls.add(u)
            }
        }

        return urls
    }

    suspend private fun getPageContent(url: String, addwww:Boolean) : String? {
        var answer: String? = null

        var fullUrl = url
        if (addwww) {
            fullUrl = "www." + fullUrl
        }
        fullUrl = "http://" + fullUrl

        try {
            val client = HttpClient(){
                install(HttpTimeout){
                    //10.000ms = 10sek
                    //100.000ms = 100sek ;)
                    requestTimeoutMillis = 100000
                    connectTimeoutMillis = 100000
                    socketTimeoutMillis = 100000
                }
            }
            log.info("Start request to $fullUrl")

            if (checkUrl(fullUrl)) {
                answer = client.get(urlString = fullUrl)

                client.close()
                if (answer != null) {
                    log.trace("Received " + answer.length + " characters.")
                }
            } else {
                log.warn("Found invalid url, skip $fullUrl")
            }
        } catch (e: ConnectException) {
            log.error("Error: Connect exception while connecting to $fullUrl:" +  e.toString())
        }
        return answer
    }

    private fun checkUrl(url:String):Boolean {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            var tmp = g_baseUrl
            if (g_baseUrl.endsWith(g_substring)) {
                tmp = tmp.dropLast(g_substring.length)
            }

            if (url.contains(tmp) && url.contains(g_substring)) {
                return true
            }
        }
        return false
    }
}