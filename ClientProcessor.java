package br.edu.ifsp.ppd.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientProcessor implements Runnable {

    private Socket socket;
    private Counter counter;

    public ClientProcessor(Socket socket, Counter counter) {
        this.socket = socket;
        this.counter = counter;
    }

    @Override
    public void run() {

        try {

            System.out.println(
                    "Novo cliente conectado: " + this.socket.getInetAddress().getHostAddress().toString()
                            + " at port " + this.socket.getPort());

            try (
                    PrintWriter out = new PrintWriter(this.socket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));) {

                String message = in.readLine();

                String responseBody = "<html><body><h3>Olá, mundo!</h3><p>Acesso número: " + this.counter.incrementAndGet() + "</p></body></html>";

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html; charset=UTF-8");
                out.println("Content-Length: " + responseBody.length());
                out.println();
                out.println(responseBody);
                out.flush();
            }
            
        } catch (IOException e) {

        } finally {
            this.close();
        }
    }

    private void close() {
        try {
            this.socket.close();
        } catch(IOException e) {
            
        }
    }
}
