# SimpleRSA

## Descrição do Projeto
O **SimpleRSA** é um projeto que implementa a criptografia RSA de forma simples, com uma interface gráfica para facilitar a utilização de chaves públicas e privadas na criptografia e descriptografia de texto. O objetivo é fornecer uma maneira intuitiva para que os usuários possam experimentar o funcionamento do algoritmo RSA.

## Funcionalidades
- Geração de pares de chaves RSA (pública e privada), 2048 bits, com ou sem data de validade
- Criptografia de texto usando a chave pública
- Descriptografia de texto usando a chave privada
- Exportação e importação de chaves

## Interface Gráfica
Ao executar o **SimpleRSA**, a interface gráfica permitirá que você:
- Gere automaticamente um par de chaves RSA
- Criptografe uma mensagem com a chave pública
- Descriptografe a mensagem com a chave privada
- Exporte as suas chaves geradas ou importe chaves de outra pessoa

## Exemplo de Uso
1. Clique no botão "GENERATE KEYS" para gerar um par de chaves.
2. Insira o texto que deseja criptografar no campo de entrada.
3. Clique em "ENCRYPT" para gerar o texto cifrado.
4. Cole o texto cifrado e clique em "DECRYPT" para ver a mensagem original.

## Limitações
- O programa não possui nenhum tipo de cache para salvar as chaves importadas ou as geradas, de forma que, toda vez que o programa é fechado, as chaves que estavam sendo utilizadas são perdidas, portanto é fortemente recomendado exportar as suas chaves.
- Os arquivos para tráfego das chaves são *.key*, nada mais, nada menos que objetos da classe key serializados, podendo ser facilmente corrompidos por um editor de texto, ou lidos através de outro programa. Portanto o uso do **SimplerRSA** deve ser apenas para fins de curiosidade.
