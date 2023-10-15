# 🔍 Web Crawler - ANS (Agência Nacional de Saúde Suplementar)

Este Web Crawler foi desenvolvido em Groovy, utilizando as bibliotecas Jsoup e HTTPBuilder, para automatizar a coleta de informações e downloads a partir do site da Agência Nacional de Saúde Suplementar (ANS).

## Funcionalidades Principais

### 1. Download dos Arquivos da Documentação do Padrão TISS
O Web Crawler realiza o download dos arquivos da documentação do Padrão TISS (Troca de Informações na Saúde Suplementar) disponíveis no site da ANS. Os arquivos são salvos na pasta 'downloads'.

### 2. Coleta de Dados do Histórico do Padrão TISS
O Crawler acessa o campo "Histórico das versões dos Componentes do Padrão TISS" e coleta dados relevantes da tabela. Os dados de competência, publicação e início de vigência são extraídos a partir da competência de janeiro de 2016. Os resultados são armazenados em um arquivo CSV chamado 'versionHistoryTISS.csv'.

### 3. Download da Tabela de Erros no Envio para a ANS
O Crawler acessa o campo "Tabelas relacionadas" e realiza o download da "Tabela de erros no envio para a ANS". O arquivo é salvo na pasta 'downloads' com o nome 'errorsOnShippingANS.xlsx'.

## Pré-requisitos
- [Groovy](https://groovy-lang.org/)
- [Jsoup](https://jsoup.org/)
- [HTTPBuilder](https://github.com/jgritman/httpbuilder)

## Como Usar
1. Clone o repositório para o seu ambiente local.
2. Execute o gradle para construir e iniciar o Web Crawler.

```bash
./gradlew run
```
## Estrutura do Projeto
- src/main/groovy/crawler: Contém os scripts principais do Web Crawler.
- src/main/groovy/downloads: Pasta onde os arquivos baixados são armazenados.
- src/main/groovy/utils: Contém utilitários e funções auxiliares utilizadas pelos scripts.

## 🌹 Agradecimento

Obrigado por explorar este projeto! Espero que seja útil para você.

Desenvolvido por [Victor Nathanael](https://www.linkedin.com/in/victornathanael/)
