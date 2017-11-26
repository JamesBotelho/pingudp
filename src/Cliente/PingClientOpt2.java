package Cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingClientOpt2 {

    private static final int TIMEOUT = 1000;
    private static InetAddress endereco;
    private static int porta;
    private static int n_sequencia;
    private static byte[] buffer;
    private static String mensagem;

    public static void main(String[] args) throws SocketException, IOException {
        /*if(args.length != 2){
            System.out.println("Os argumentos necessários são: IP PORTA");
            return;
        }
        InetAddress endereco = InetAddress.getByName(args[0]);
        int porta = Integer.parseInt(args[1]);*/
        endereco = InetAddress.getByName("127.0.0.1");
        porta = 8080;
        n_sequencia = 0; //Número de iteradas que é utilizado na mensagem
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Date tempo = new Date();
                long tempo_ms = tempo.getTime();
                mensagem = "PING " + String.valueOf(n_sequencia) + " " + tempo_ms + "\n";
                buffer = mensagem.getBytes();

                //Prepara o pacote para o envio
                DatagramPacket pkg = new DatagramPacket(buffer, buffer.length, endereco, porta);
                try {
                    //Abre a conexão
                    DatagramSocket ds = new DatagramSocket();

                    //Envia o pacote para o destino
                    System.out.print("Enviando para " + pkg.getAddress().getHostAddress() + ": " + mensagem);
                    ds.send(pkg);


                    //Define o tempo de espera pelo pacote
                    ds.setSoTimeout(TIMEOUT);

                    //Recebe o pacote enviado pelo servidor
                    ds.receive(pkg);
                    tempo = new Date();

                    //Efetua o calculo do Ping
                    tempo_ms = tempo.getTime() - tempo_ms;

                    //Exibe a mensagem, caso o pacote seja retornado a tempo
                    System.out.print("PING " + String.valueOf(n_sequencia) + " " + tempo_ms + " ms\n");
                } catch (IOException e) {
                    //Caso o pacote não seja retornado no tempo determinado, exibe a mensagem abaixo
                    System.out.print("Pacote " + String.valueOf(n_sequencia) + " perdido\n");
                }
                //Incrementa o número de sequência
                n_sequencia++;
                if(n_sequencia == 10){
                    t.cancel();
                }
            }
        },0, 1000);
    }
}
