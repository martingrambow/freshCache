package main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Arguments(parser: ArgParser) {

    val baseUrl: String by parser.storing(
        "-u", "--url",
        help = "base url which is in the beginning of every url."
    ).default("tu.berlin/mcc/")

    val substring: String by parser.storing(
        "-g", "--group",
        help = "reseach group ID (\"/mcc/\" for the new website, \"mcc\" for the old one)."
    ).default("/mcc/")

    val excludes by parser.adding(
        "-e", "--exclude",
        help = "urls containing this substring will be ignored/excluded \n" +
                "(for the new website: -e rss -e cHash)\n" +
                "(for the old website: -e rss -e /minhilfe/ -e /maxhilfe/ -e .css -e /sitemap/ -e /servicemenue/ -e isMobile= -e #).")

    val addwww: Boolean by parser.flagging(
        "-w", "--www",
        help = "add www in front (for the old website, this flag must be turned on)."
    ).default(false)
}