import groovyx.net.http.HttpBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.nio.file.Files
import java.nio.file.Paths


import static groovyx.net.http.HttpBuilder.configure

class App {
    static void main(String[] args) {

        // Página inicial
        String getUrlPrestadores = httpBuilderConfigure('https://www.gov.br/ans/pt-br')
                .getElementById("ce89116a-3e62-47ac-9325-ebec8ea95473")
                .getElementsByTag("a")
                .attr("href")


        // Página dos prestadores
        String getUrlTISS = httpBuilderConfigure(getUrlPrestadores)
                .select("a:containsOwn(TISS - Padrão para Troca de Informação de Saúde Suplementar)")
                .first()
                .attr("href")


        // Página TISS
        String getUrlTISSMonthAndYear = httpBuilderConfigure(getUrlTISS)
                .select("a:containsOwn(Clique aqui para acessar a versão Setembro/2023)")
                .attr("href")


        // Página dos arquivos TISS
        String getUrlCommunicationComponent = httpBuilderConfigure(getUrlTISSMonthAndYear)
                .select("tr td:contains(Componente de Comunicação) ~ td > a")
                .attr("href")


        def url = new URL(getUrlCommunicationComponent)
        def destino = Paths.get("./src/main/groovy/arquivo_TISS.zip")

        // Abre uma conexão com a URL
        def conexao = url.openConnection()

        try {
            // Lê os bytes do InputStream da conexão
            def bytes = conexao.inputStream.bytes

            // Grava os bytes em um arquivo local
            Files.write(destino, bytes)

            println "Arquivo baixado com sucesso em: $destino"
        } finally {
            // Certifica-se de fechar a conexão, mesmo se ocorrer uma exceção
            conexao.inputStream.close()
        }

    }

    static Document httpBuilderConfigure(String uri) {
        HttpBuilder http = configure {
            request.uri = uri
        }

        String html = http.get().toString()

        Document document = Jsoup.parse(html)

        return (document)
    }

}
