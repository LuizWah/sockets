import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class alunoHandler {

    public ArrayList<aluno> alunos;
    public ArrayList<Integer> IDs_usados;
    private AlunoProcessor user;
    
    String[] firstNames = {"John", "Jane", "Michael", "Sara", "David", "Emily", "Chris", "Sophia"};
    String[] lastNames = {"Smith", "Johnson", "Brown", "Taylor", "Anderson", "Wilson", "Clark"};

    public alunoHandler(){
        this.alunos = new ArrayList<>();
        this.IDs_usados = new ArrayList<>();
        this.user = null;
    }

    public class result {
        aluno Aluno;
        boolean found;

        result(aluno Aluno, boolean found){
            this.Aluno = Aluno;
            this.found = found;
        }
    }

    public result GetAlunoByID(int id){
        for(int i = 0; i < alunos.size(); i++){
            if(alunos.get(i).GetID() == id){
                return new result(alunos.get(i), true);
            }
        }
        return new result(null, false);
    }

    public boolean Id_used(int id){
        return IDs_usados.contains(id);
    }

    public int random_int(){
        return ThreadLocalRandom.current().nextInt(1, 100 + 1);
    }

    public String random_name(){
        String firstName = firstNames[(int)(Math.random() * firstNames.length)];
        String lastName = lastNames[(int)(Math.random() * lastNames.length)];

        return firstName + " " + lastName;
    }

    private int generateUniqueID() {
        int new_ID = 0;

        if (IDs_usados.size() > 0) {
            new_ID = IDs_usados.get(IDs_usados.size() - 1) + 1;
        }

        return new_ID;
    }

    public synchronized void add_random_user(AlunoProcessor new_user) throws Exception{
        while(this.user != null && this.user != new_user){
            wait();
        }

        int new_ID = generateUniqueID();
        aluno new_aluno = new aluno(new_ID, random_int(), random_name());
        
        alunos.add(new_aluno);
        IDs_usados.add(new_ID);
        

        notifyAll();
        this.user = null;
    }

    public String show_aluno(int id){
        result Aluno = GetAlunoByID(id);

        if(Aluno.found){
            return "<html><body><h3>" + "NOME: " + Aluno.Aluno.GetName() + " <br>IDADE: " + Aluno.Aluno.GetIdade() + "</h3></body></html>";
        } else {
            return "<html><body><h3>Aluno nao existe</h3></body></html>";
        }
    }

    public synchronized boolean delete_aluno(int id, AlunoProcessor new_user) throws Exception{
        while(this.user != null && this.user != new_user){
            wait();
        }

        for(int i = 0; i < this.alunos.size(); i++){
            if(this.alunos.get(i).GetID() == id){
                this.alunos.remove(i);
                notifyAll();
                this.user = null;
                return true;
            }
        }
        notifyAll();
        this.user = null;
        return false;
    }
}