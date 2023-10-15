package utils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileDownloader {
    static void downloadFile(String urlFile, String path) {
        def url = new URL(urlFile)
        Path destino = Paths.get(path)
        String nomeArquivo = destino.getFileName().toString()
        URLConnection conexao = url.openConnection()

        try {
            byte[] bytes = conexao.inputStream.bytes

            Files.write(destino, bytes)

            println "$nomeArquivo baixado com sucesso em: $destino"
        } catch (Exception ignored) {
            throw new Error("Não foi possível baixar o arquivo")
        } finally {
            conexao.inputStream.close()
        }
    }
}
