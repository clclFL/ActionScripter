package club.pineclone.api;

public final class Pair<T , U> {

    public T fst;
    public U snd;

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    public Pair(T fst, U snd) {
        this.fst = fst;
        this.snd = snd;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Pair[]";
    }
}
