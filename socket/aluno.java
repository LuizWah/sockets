public class aluno {
    private String Name;
    private int Idade;
    private int ID;

    public aluno(){
        this.ID = 0;
        this.Idade = 0; 
        this.Name = null;
    }

    public aluno(int ID, int idade, String Name){
        this.ID = ID;
        this.Idade = idade; 
        this.Name = Name;
    }




    public int GetID(){
        return this.ID;
    }
    public int GetIdade(){
        return this.Idade;
    }
    public String GetName(){
        return this.Name;
    }
}
