public class Lines {
    String tag;
    String block;
    int time=0;
    int setIndex; // S0 S1...
    int v=0; // valid bit
    int B; // data size

    public Lines() {

    }
    public Lines(String tag, String block, int setIndex, int v, int b) {
        this.tag = tag;
        this.block = block;
        this.time = time;
        this.v = v;
        B = b;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        time++;
    }

    public String getData() {
        return block;
    }

    public void setData(String data) {
        this.block = data;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSetIndex() {
        return setIndex;
    }

    public void setSetIndex(int setIndex) {
        this.setIndex = setIndex;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }
}