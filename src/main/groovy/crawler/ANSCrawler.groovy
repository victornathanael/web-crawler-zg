package crawler

import utils.FileDownloader
import utils.HttpUtils

class ANSCrawler {
    static void run() {
        HttpUtils httpUtils = new HttpUtils()
        FileDownloader fileDownloader = new FileDownloader()
        TISSCrawler tissCrawler = new TISSCrawler()


        //  Buscar a Url para a página Padrão TISS – Tabelas Relacionadas
        String getUrlTISSRelatedTables = httpUtils.httpBuilderAndParseJsoup(tissCrawler.getUrlTISS)
                .select("h2:containsOwn(Padrão TISS – Tabelas Relacionadas) + p.callout a:containsOwn(Clique aqui para acessar as planilhas)")
                .attr("href")

        // Buscar a Url da tabela de erros no envio para a ANS
        String getUrlTableShippingErrorANS = httpUtils.httpBuilderAndParseJsoup(getUrlTISSRelatedTables)
                .select("h2:containsOwn(Tabela de erros no envio para a ANS) + p.callout a:containsOwn(Clique aqui para baixar a tabela de erros no envio para a ANS (.xlsx))")
                .attr("href")

        fileDownloader.downloadFile(getUrlTableShippingErrorANS, "./src/main/groovy/downloads/errorsOnShippingANS.xlsx")
    }
}
