public enum TimeOut {

    SHORT(5),
    MEDIUM(10),
    LONG(15),
    EXTRA_LONG(30),
    PAGE_LOAD(60);

    public final int value;

    TimeOut(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }



}
