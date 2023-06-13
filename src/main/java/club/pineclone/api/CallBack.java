package club.pineclone.api;

public interface CallBack<T> {

    void callBack(T t);

    static CallBack<?> plain() {
        return t -> {};
    }

}
