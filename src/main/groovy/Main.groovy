import groovyx.net.http.HttpBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.opencsv.CSVWriter

import static groovyx.net.http.HttpBuilder.configure

class App {
    static void main(String[] args) {

        // Buscar a Url para a página dos prestadores
        String getUrlPrestadores = httpBuilderAndParseJsoup('https://www.gov.br/ans/pt-br')
                .getElementById("ce89116a-3e62-47ac-9325-ebec8ea95473")
                .getElementsByTag("a")
                .attr("href")

        //  Buscar a Url para a página TISS
        String getUrlTISS = httpBuilderAndParseJsoup(getUrlPrestadores)
                .select("a:containsOwn(TISS - Padrão para Troca de Informação de Saúde Suplementar)")
                .first()
                .attr("href")

        //  Buscar a Url para a página dos arquivos TISS - Versão mês e ano
        String getUrlTISSMonthAndYear = httpBuilderAndParseJsoup(getUrlTISS)
                .select("h2:containsOwn(Padrão TISS – Versão Setembro/2023) + p.callout a:containsOwn(Clique aqui para acessar a versão Setembro/2023)")
                .attr("href")


        // Buscar a Url para o download do arquivo Componente de Comunicação
        String getUrlCommunicationComponent = httpBuilderAndParseJsoup(getUrlTISSMonthAndYear)
                .select("tr td:contains(Componente de Comunicação) ~ td > a")
                .attr("href")

        downloadFile(getUrlCommunicationComponent, "./src/main/groovy/downloads/fileTISS.zip")

        //  Buscar a Url para a tabela TISS - Histórico das versões dos Componentes do Padrão TISS
        String getUrlTISSVersionHistory = httpBuilderAndParseJsoup(getUrlTISS)
                .select("h2:containsOwn(Padrão TISS – Histórico das versões dos Componentes do Padrão TISS) + p.callout a:containsOwn(Clique aqui para acessar todas as versões dos Componentes)")
                .attr("href")

        // Buscar cabeçalho da tabela
        ArrayList headersArray = []
        httpBuilderAndParseJsoup(getUrlTISSVersionHistory)
                .select("thead > tr > th:lt(3)")
                .each { element ->
                    headersArray << element.text()
                }


        // Buscar dados de 2016 na tabela
        ArrayList dataArray = []
        httpBuilderAndParseJsoup(getUrlTISSVersionHistory)
                .select("tr > td:containsOwn(2016):lt(1)," +
                        " tr > td:containsOwn(2016):lt(1) + td," +
                        " tr > td:containsOwn(2016):lt(1) + td + td")
                .eachWithIndex { element, index ->
                    int groupIndex = (int) (index / 3)
                    if (dataArray.size() <= groupIndex) {
                        dataArray << []
                    }
                    dataArray[groupIndex] << element.text()
                }

        // Criação do arquivo CSV
        def outputFile = new File("./src/main/groovy/versionHistoryTISS.csv")

        def writer = new CSVWriter(new FileWriter(outputFile))
        writer.writeNext(headersArray as String[])

        dataArray.each { row ->
            writer.writeNext(row as String[])
        }

        writer.close()

        println "Arquivo CSV gerado com sucesso em: $outputFile"


        //  Buscar a Url para a página Padrão TISS – Tabelas Relacionadas
        String getUrlTISSRelatedTables = httpBuilderAndParseJsoup(getUrlTISS)
                .select("h2:containsOwn(Padrão TISS – Tabelas Relacionadas) + p.callout a:containsOwn(Clique aqui para acessar as planilhas)")
                .attr("href")



        // Buscar a Url da tabela de erros no envio para a ANS
        String getUrlTableShippingErrorANS = httpBuilderAndParseJsoup(getUrlTISSRelatedTables)
                .select("h2:containsOwn(Tabela de erros no envio para a ANS) + p.callout a:containsOwn(Clique aqui para baixar a tabela de erros no envio para a ANS (.xlsx))")
                .attr("href")

        downloadFile(getUrlTableShippingErrorANS, "./src/main/groovy/downloads/errorsOnShippingANS.xlsx")

    }


    static Document httpBuilderAndParseJsoup(String uri) {
        HttpBuilder http = configure {
            request.uri = uri
        }

        String html = http.get().toString()

        Document document = Jsoup.parse(html)

        return (document)
    }

    static void downloadFile(String urlFile, String path) {
        def url = new URL(urlFile)
        Path destino = Paths.get(path)

        // Abre uma conexão com a URL
        URLConnection conexao = url.openConnection()

        try {
            // Lê os bytes do InputStream da conexão
            byte[] bytes = conexao.inputStream.bytes

            // Grava os bytes em um arquivo local
            Files.write(destino, bytes)

            println "Arquivo baixado com sucesso em: $destino"
        } catch (Exception ignored) {
            throw new Error("Não foi possível baixar o arquivo")
        } finally {
            // Certifica-se de fechar a conexão, mesmo se ocorrer uma exceção
            conexao.inputStream.close()
        }
    }
}
