/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

/**
 *
 * @author James
 */
public class PingClient {
    
    private static final int TIMEOUT = 1000;
    
    public static void main(String[] args) throws SocketException, IOException {
        /*if(args.length != 2){
            System.out.println("Os argumentos necessários são: IP PORTA");
            return;
        }
        InetAddress endereco = InetAddress.getByName(args[0]);
        int porta = Integer.parseInt(args[1]);*/
        InetAddress endereco = InetAddress.getByName("127.0.0.1");
        int porta = 8080;
        int n_sequencia = 0;
        String mensagem;
        byte[] buffer = new byte[1024];
        while(n_sequencia < 10){
            Date tempo = new Date();
            long tempo_ms = tempo.getTime();
            mensagem = "PING " + String.valueOf(n_sequencia) + " " + tempo_ms + "\n";
            buffer = mensagem.getBytes();
            DatagramPacket pkg = new DatagramPacket(buffer, buffer.length, endereco, porta);
            DatagramSocket ds = new DatagramSocket();
            ds.send(pkg);
            try{
                ds.setSoTimeout(TIMEOUT);
                ds.receive(pkg);
                tempo = new Date();
                tempo_ms = tempo.getTime() - tempo_ms;
                System.out.print("PING " + String.valueOf(n_sequencia) + "\t" + tempo_ms + " ms\n");
            }catch(IOException e){
                System.out.print("Pacote " + String.valueOf(n_sequencia) + " perdido\n");
            }
            n_sequencia++;
        }
    }
}
