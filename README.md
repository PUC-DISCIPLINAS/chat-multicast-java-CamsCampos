# Chat Multicast em Java
Aluna: Camila Silva Campos | 5° período de Engenharia de Software

Professor: Hugo de Paula
## Inicialização
### Servidor:
> Abra a pasta `bin` do projeto no terminal e execute: 

    java redes.Server
    
### Cliente:
> Abra a pasta `bin` do projeto no terminal e execute: **java redes.Client <endereçoIp> <nomeUsuário>**. Exemplo:

    java redes.Client localhost "Camila Campos"

## Protocolo de comunicação
### Mensagens:
>Enquanto a aplicação estiver rodando, você poderá digitar os seguintes comandos no terminal cliente: 

Obs: é possível que o mesmo usuário (cliente) entre em mais de uma sala. Por isso, alguns comandos requerem o ID da sala
| Mensagem | Comando | Exemplo |
| ------ | ------ | ------ | 
| Criar sala | criarSala nome=nome_sala | `criarSala nome=Grupo de estudos`
| Listar salas | listarSalas | `listarSalas` 
| Entrar na sala| entrarNaSala id=id_sala| `entrarNaSala id=224.0.0.1` 
| Listar membros da sala | listarMembrosSala id=id_sala | `listarMembrosSala id=224.0.0.1` 
| Enviar mensagem | enviarMensagem id=id_sala m=mensagem| `enviarMensagem id=224.0.0.1 m=Oi, pessoal. Tudo bem?` 
| Sair da sala| sairDaSala id=id_sala | `sairDaSala id=224.0.0.1` 

## Estrutura de Pacotes e Classes
#### Pacote `componentes` :
* Classe:  
    * **ChatRoom** | referente à sala de chat, contendo suas informações básicas como **nome, id da sala e lista de membros** 
#### Pacote `redes` :
* Classes:  
    * **Client** | responsável por:
        * Receber todas os comandos do usuário, executá-los e responder.
        * Se comunicar com o servidor, repassando comandos quando necessário e aguardar a resposta.
        * Instanciar a classe Multicast
    * **ListenMulticast** | é uma thread responsável por invocar o método `join()` do multicast, que entrará em loop infinito para ouvir as mensagens dos usuários.   
    * **Multicast** | reponsável pela conexão com o protocolo multicast. Alguns de seus métodos são:
        * `join()`: une um cliente à um determinado grupo multicast e permanece ouvindo mensagens desse grupo até o fim do programa.
        * `sendMessage()`: envia as mensagens de um cliente ao grupo multicast especificado
        * `leave()`: remove o cliente de um determinado grupo multicast
    * **Server** | recebe requisições do cliente e responde. Algumas de suas tarefas são:
        * Prover um ip multicast válido para cada sala de bate-papo
        * Manter uma lista das salas criadas
        * Listar salas e os membros dessas salas.
