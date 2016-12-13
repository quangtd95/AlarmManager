package td.quang.alarmmanager.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {
    private int id;
    private int h,m;
    private String name;
    private int sound;
    private String day;
    private int status;
    private int repeat;
    private int skip;
    private int remain;
    private int rH;
    private int rM;
    public Alarm(){

    }

    protected Alarm(Parcel in) {
        id = in.readInt();
        h = in.readInt();
        m = in.readInt();
        name = in.readString();
        sound = in.readInt();
        day = in.readString();
        status = in.readInt();
        repeat = in.readInt();
        skip = in.readInt();
        remain = in.readInt();
        rH = in.readInt();
        rM = in.readInt();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public int getrH() {
        return rH;
    }

    public void setrH(int rH) {
        this.rH = rH;
    }

    public int getrM() {
        return rM;
    }

    public void setrM(int rM) {
        this.rM = rM;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(h);
        parcel.writeInt(m);
        parcel.writeString(name);
        parcel.writeInt(sound);
        parcel.writeString(day);
        parcel.writeInt(status);
        parcel.writeInt(repeat);
        parcel.writeInt(skip);
        parcel.writeInt(remain);
        parcel.writeInt(rH);
        parcel.writeInt(rM);
    }
}
