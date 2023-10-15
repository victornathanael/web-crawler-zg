package utils

import groovyx.net.http.HttpBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import static groovyx.net.http.HttpBuilder.configure

class HttpUtils {
    static Document httpBuilderAndParseJsoup(String uri) {
        HttpBuilder http = configure {
            request.uri = uri
        }

        String html = http.get().toString()

        Document document = Jsoup.parse(html)

        return (document)
    }
}
