public class OutputObject {
    CloudData data;
    Vector wind;

    //Object to hold a CloudData object and a vector object so recursive task can return both
    public OutputObject(CloudData data, Vector wind) {
        this.data = data;
        this.wind = wind;
    }
}
