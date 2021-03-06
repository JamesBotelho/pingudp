package Cliente;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ReliableUdpSender {

    private static final int TIMEOUT = 1000;
    private static final int num_ack = 10;


    public static int examinaDado(DatagramPacket recebido) throws IOException {
        byte[] buf = recebido.getData();
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        InputStreamReader isr = new InputStreamReader(bais);
        BufferedReader br = new BufferedReader(isr);

        String line = br.readLine();

        int local = line.indexOf(":");
        return Integer.parseInt(line.substring(local+2, line.length()));
    }

    public static void main(String[] args) throws IOException {
        /*if(args.length != 2){
            System.out.println("Os argumentos necessários são: IP PORTA");
            return;
        }
        InetAddress endereco = InetAddress.getByName(args[0]);
        int porta = Integer.parseInt(args[1]);*/
        InetAddress endereco = InetAddress.getByName("127.0.0.1");
        int porta = 8080;
        int n_ack = 0; //Número de iteradas que é utilizado na mensagem
        int ack_recebido;
        boolean ack_nao_confirmado = true;
        String mensagem;    //Mensagem que será enviada
        byte[] buffer; //buffer que será usado no envio da mensagem para o servidor
        while(n_ack < num_ack){
            mensagem = "Mensagem ACK: " + String.valueOf(n_ack) + "\n";
            buffer = mensagem.getBytes();

            //Prepara o pacote para o envio
            DatagramPacket pkg = new DatagramPacket(buffer, buffer.length, endereco, porta);

            //Abre a conexão
            DatagramSocket ds = new DatagramSocket();

            //Envia a mensagem até o ack ser confirmado pelo servidor
            while(ack_nao_confirmado) {
                System.out.print("Enviando para " + pkg.getAddress().getHostAddress() + ": " + mensagem);
                ds.send(pkg);
                try {

                    //Caso haja falta de sincronia, entra em espera até que o ack corrente seja confirmado
                    do {
                        ds.setSoTimeout(TIMEOUT);

                        ds.receive(pkg);
                        ack_recebido = examinaDado(pkg);

                    }while(ack_recebido != n_ack);

                    //Imprime o ack confirmado
                    System.out.println("ACK: " + ack_recebido + " confirmado");
                    //Interrompe o laço
                    ack_nao_confirmado = false;
                } catch (IOException e) {
                    //Caso o pacote não seja retornado no tempo determinado, exibe a mensagem abaixo
                    System.out.print("Número de ACK " + String.valueOf(n_ack) + " perdido\n");
                }
            }
            //Incrementa o número de sequência de ack
            n_ack++;
            ack_nao_confirmado = true;
        }
    }
}
