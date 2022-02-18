package main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

val  log = LoggerFactory.getLogger("Main")!!

var g_baseUrl:String = ""
var g_substring:String = ""
var g_addwww:Boolean = false
var g_excludes:List<String> = mutableListOf<String>()

val freshUrls = mutableListOf<String>()

/**
 * Main class of FreshCache
 */
fun main(args: Array<String>) = mainBody  {

    ArgParser(args).parseInto(::Arguments).run {
        g_baseUrl = baseUrl
        g_substring = substring
        g_addwww = addwww
        g_excludes = excludes

        log.info("freshCache runs for url $baseUrl.")

        startThreadForUrl(baseUrl)
        log.info("freshCache done.")
        return@mainBody
    }
}

fun startThreadForUrl(url:String) {
    runBlocking {
        launch {
            val runner = RequestRunner()
            var nextUrls = mutableListOf<String>()
            if (!freshUrls.contains(url)) {
                freshUrls.add(url)
                var result = runner.runRequest(url, g_substring, g_excludes, g_addwww)
                println(result.orgUrl + "; " + result.duration)

                var newCnt = 0
                for (u in result.urls) {
                    var fullurl = ""
                    if (g_baseUrl.endsWith(g_substring)) {
                        fullurl = g_baseUrl.dropLast(g_substring.length)
                    } else {
                        fullurl = g_baseUrl
                    }

                    if (u.startsWith(fullurl)) {
                        fullurl = u
                    } else {
                        fullurl = fullurl + u
                    }


                    if (freshUrls.contains(fullurl) || nextUrls.contains(fullurl)) {
                        log.trace("Already found $fullurl")
                    } else {
                        newCnt++
                        nextUrls.add(fullurl)
                        log.trace("Added $fullurl")
                    }
                }
                log.info("Found " + result.urls.size + " urls ($newCnt new ones) in " + result.duration + "ms.")
                runBlocking {
                    for (u in nextUrls) {
                        launch {
                            startThreadForUrl(u)
                        }
                    }
                }
            }
        }
    }
}