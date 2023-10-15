package crawler

import com.opencsv.CSVWriter
import utils.FileDownloader
import utils.HttpUtils

class TISSCrawler {
    static String getUrlTISS

    static void run() {
        HttpUtils httpUtils = new HttpUtils()
        FileDownloader fileDownloader = new FileDownloader()
        // Buscar a Url para a página dos prestadores
        String getUrlPrestadores = httpUtils.httpBuilderAndParseJsoup('https://www.gov.br/ans/pt-br')
                .getElementById("ce89116a-3e62-47ac-9325-ebec8ea95473")
                .getElementsByTag("a")
                .attr("href")

        //  Buscar a Url para a página TISS
        getUrlTISS = httpUtils.httpBuilderAndParseJsoup(getUrlPrestadores)
                .select("a:containsOwn(TISS - Padrão para Troca de Informação de Saúde Suplementar)")
                .first()
                .attr("href")

        //  Buscar a Url para a página dos arquivos TISS - Versão mês e ano
        String getUrlTISSMonthAndYear = httpUtils.httpBuilderAndParseJsoup(getUrlTISS)
                .select("h2:containsOwn(Padrão TISS – Versão Setembro/2023) + p.callout a:containsOwn(Clique aqui para acessar a versão Setembro/2023)")
                .attr("href")


        // Buscar a Url para o download do arquivo Componente de Comunicação
        String getUrlCommunicationComponent = httpUtils.httpBuilderAndParseJsoup(getUrlTISSMonthAndYear)
                .select("tr td:contains(Componente de Comunicação) ~ td > a")
                .attr("href")

        fileDownloader.downloadFile(getUrlCommunicationComponent, "./src/main/groovy/downloads/fileTISS.zip")

        //  Buscar a Url para a tabela TISS - Histórico das versões dos Componentes do Padrão TISS
        String getUrlTISSVersionHistory = httpUtils.httpBuilderAndParseJsoup(getUrlTISS)
                .select("h2:containsOwn(Padrão TISS – Histórico das versões dos Componentes do Padrão TISS) + p.callout a:containsOwn(Clique aqui para acessar todas as versões dos Componentes)")
                .attr("href")

        // Buscar cabeçalho da tabela
        ArrayList headersArray = []
        httpUtils.httpBuilderAndParseJsoup(getUrlTISSVersionHistory)
                .select("thead > tr > th:lt(3)")
                .each { element ->
                    headersArray << element.text()
                }


        // Buscar dados de 2016 na tabela
        ArrayList dataArray = []
        httpUtils.httpBuilderAndParseJsoup(getUrlTISSVersionHistory)
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
        File outputFile = new File("./src/main/groovy/versionHistoryTISS.csv")

        CSVWriter writer = new CSVWriter(new FileWriter(outputFile))
        writer.writeNext(headersArray as String[])

        dataArray.each { row ->
            writer.writeNext(row as String[])
        }

        writer.close()

        println "Arquivo CSV gerado com sucesso em: $outputFile"
    }
}
