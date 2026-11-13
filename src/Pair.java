
import java.util.*;

public class Pair<A,B> {
    A first;
    B second;

    public Pair(A first,B second){
        this.first=first;
        this.second=second;
    }

    public A getFirst(){return this.first;}
    public B getSecond(){return this.second;}
    public boolean equals(Object obj){
        if (this==obj) return true;
        if (obj==null||this.getClass()!=obj.getClass()) return false;
        Pair<?,?> other= (Pair<?,?>) obj;

        return Objects.equals(this.getFirst(),other.getFirst())&&Objects.equals(this.getSecond(),other.getSecond());

    }
}
