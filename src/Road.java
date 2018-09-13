public class Road {
    private int roadId;
    private int type;
    private String label;
    private String city;
    private boolean oneWay;
    private int speed;
    private int roadClass;
    private boolean notForCar;
    private boolean notForPede;
    private boolean notForBicy;

    public Road(){}

    public Road(int roadId, int type, String label, String city, int oneWay, int speed,int roadClass, int notForCar, int notForPede, int notForBicy) {
        this.roadId = roadId;
        this.type = type;
        this.label = label;
        this.city = city;
        this.oneWay = (oneWay==1);
        this.speed = speed;
        this.roadClass = roadClass;
        this.notForCar = (notForCar==1);
        this.notForPede = (notForPede==1);
        this.notForBicy = (notForBicy==1);
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return     label +
                ", roadId=" + roadId +
                ", type=" + type +
                ", city='" + city + '\'' +
                ", oneWay=" + oneWay +
                ", speed=" + speed +
                ", notForCar=" + notForCar +
                ", notForPede=" + notForPede +
                ", notForBicy=" + notForBicy + "\n"
               ;
    }

    public int getSpeed() {
        if (speed == 0) return 5;
        if (speed == 1) return 20;
        if (speed == 2) return 40;
        if (speed == 3) return 60;
        if (speed == 4) return 80;
        if (speed == 5) return 100;
        if (speed == 6) return 110;
        if (speed == 7) return 110;
        return 110;
    }

    public int getRefinedSpeed(){
        return getSpeed()*(1+roadClass/100);
    }
}
