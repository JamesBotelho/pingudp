package Cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class PingClientOpt1 {
    private static final int TIMEOUT = 1000;
    private static long[] tempo_min_max = new long[2];
    private static long media_tempo = 0;
    private static int pct_perdido = 0;

    public static void main(String[] args) throws SocketException, IOException {
        /*if(args.length != 2){
            System.out.println("Os argumentos necessários são: IP PORTA");
            return;
        }
        InetAddress endereco = InetAddress.getByName(args[0]);
        int porta = Integer.parseInt(args[1]);*/
        InetAddress endereco = InetAddress.getByName("127.0.0.1");
        int porta = 8080;
        int n_sequencia = 0; //Número de iteradas que é utilizado na mensagem
        String mensagem;    //Mensagem que será enviada
        byte[] buffer = new byte[1024]; //buffer que será usado no envio da mensagem para o servidor
        tempo_min_max[0] = 1000;
        while (n_sequencia < 10) {
            Date tempo = new Date();
            long tempo_ms = tempo.getTime();
            mensagem = "PING " + String.valueOf(n_sequencia) + " " + tempo_ms + "\n";
            buffer = mensagem.getBytes();

            //Prepara o pacote para o envio
            DatagramPacket pkg = new DatagramPacket(buffer, buffer.length, endereco, porta);

            //Abre a conexão
            DatagramSocket ds = new DatagramSocket();

            //Envia o pacote para o destino
            System.out.print("Enviando para " + pkg.getAddress().getHostAddress() + ": " + mensagem);
            ds.send(pkg);
            try {

                //Define o tempo de espera pelo pacote
                ds.setSoTimeout(TIMEOUT);

                //Recebe o pacote enviado pelo servidor
                ds.receive(pkg);
                tempo = new Date();

                //Efetua o calculo do Ping
                tempo_ms = tempo.getTime() - tempo_ms;
                media_tempo = media_tempo + tempo_ms;

                //Exibe a mensagem, caso o pacote seja retornado a tempo
                System.out.print("PING " + String.valueOf(n_sequencia) + " " + tempo_ms + " ms\n");

                if(tempo_ms < tempo_min_max[0]){
                    tempo_min_max[0] = tempo_ms;
                }else if(tempo_min_max[1] < tempo_ms){
                    tempo_min_max[1] = tempo_ms;
                }
            } catch (IOException e) {
                //Caso o pacote não seja retornado no tempo determinado, exibe a mensagem abaixo
                System.out.print("Pacote " + String.valueOf(n_sequencia) + " perdido\n");
                pct_perdido++;
            }
            //Incrementa o número de sequência
            n_sequencia++;
        }
        media_tempo = media_tempo/(n_sequencia-pct_perdido);
        System.out.println("Tempo médio: " + media_tempo + " ms");
        System.out.println("Tempo mínimo: " + tempo_min_max[0] + " ms");
        System.out.println("Tempo máximo: " + tempo_min_max[1] + " ms");
    }
}
