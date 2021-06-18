import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Cache {
    int E; //line number
    int B; //data bit
    int S; // number of sets in cache
    int miss;
    int hits;
    int evictions;
    Lines[][]sets;

    public Cache(int S, int E, int B) throws FileNotFoundException {
        this.E = E;
        this.B = B;
        this.S= S;
        sets = new Lines[S][E];//first set number second line number in that cache
        for(int i=0;i<S;i++){
            for(int j=0;j<E;j++){
                this.sets[i][j]=new Lines();
            }
        }


    }

    public int getE() {
        return E;
    }

    public void setE(int e) {
        E = e;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }

    public int getS() {
        return S;
    }

    public void setS(int s) {
        S = s;
    }

    public Lines[][] getSets() {
        return sets;
    }

    public void setSets(Lines[][] sets) {
        this.sets = sets;
    }




    }

