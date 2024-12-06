

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AlunoProcessor implements Runnable {

    private Socket socket;
    private alunoHandler AH;

    public String FindMethod(String url){
        if(url.contains("POST")){
            return "POST";
        }
        else if(url.contains("GET")){
            return "GET";
        }
        else if(url.contains("DELETE")){
            return "DELETE";
        }
        else{
            return "ERROR";
        }
    }

    public String AlunoRoute(String url){
        String regex_post = "^POST /aluno HTTP/1.1";
        String regex_get = "^GET /aluno/\\d+ HTTP/1.1";
        String regex_delete = "^DELETE /aluno/\\d+ HTTP/1.1";

        Pattern pattern_post = Pattern.compile(regex_post);
        Pattern pattern_get = Pattern.compile(regex_get);
        Pattern pattern_delete = Pattern.compile(regex_delete);


        Matcher matcher_post = pattern_post.matcher(url);
        Matcher matcher_get = pattern_get.matcher(url);
        Matcher matcher_delete = pattern_delete.matcher(url);
        
        if(matcher_post.find()){
            return "POST";
        }
        else if(matcher_get.find()){
            return "GET";
        }
        else if(matcher_delete.find()){
            return "DELETE";
        }
        else{
            return "ERROR";
        }
    }

    public int extract_aluno_id(String url){
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(url);

        matcher.find();
        String number =  matcher.group();

        return Integer.parseInt(number);
    }

    public AlunoProcessor(Socket socket, alunoHandler ah) {
        this.socket = socket;
        this.AH = ah;
    }

    public void HTML_response(String responseBody, PrintWriter out){
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Content-Length: " + responseBody.length());
        out.println();
        out.println(responseBody);
        out.flush();
    }

    @Override
    public void run() {

        try {

            System.out.println(
                    "Novo cliente conectado: " + this.socket.getInetAddress().getHostAddress().toString()
                            + " at port " + this.socket.getPort());

            try (
                PrintWriter out = new PrintWriter(this.socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));){

                String message = in.readLine();

                String method = this.AlunoRoute(message);

                System.out.println(message);
                if(method == "Error"){
                    String responseBody = "<html><body><h3>ERROR 404</h3></body></html>";
                    out.println("HTTP/1.1 404 ERROR");
                    this.HTML_response(responseBody, out);
                }
                else if(method == "POST"){
                    try{
                        AH.add_random_user(this);

                    }catch(Exception e){

                    }

                        int last_added = AH.IDs_usados.size() - 1;

                        String Name = AH.alunos.get(last_added).GetName();
                        int Idade = AH.alunos.get(last_added).GetIdade();
                        String responseBody = "<html><body><h3>NAME: " + Name + "</h3><br><h3>IDADE: " + Idade + "</h3></body></html>";
       
                        out.println("HTTP/1.1 200");
                        this.HTML_response(responseBody, out);
                    }
                else if(method == "GET"){

                        int id = this.extract_aluno_id(message);

                        String responseBody = this.AH.show_aluno(id);

                       if(responseBody.charAt(0) != '<'){
                           out.println("HTTP/1.1 404");
                           this.HTML_response(responseBody, out);
                       }
                       else{
                        out.println("HTTP/1.1 200");
                        this.HTML_response(responseBody, out);
                       }
                    }
                else if(method == "DELETE") {
                    int id = this.extract_aluno_id(message);
                    try{
                        boolean result = this.AH.delete_aluno(id, this);
                        String responseBody;
    
                        if(result){
                            responseBody = "<html><body><h3>O aluno foi deletado.</h3></body></html>" + message;
                        }
                        else{
                            responseBody = "<html><body><h3>O aluno nao existe.</h3></body></html>" + message;
                        }
                        out.println("HTTP/1.1 200 OK");
                        this.HTML_response(responseBody, out);

                    }catch(Exception e){

                    }
                }
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
