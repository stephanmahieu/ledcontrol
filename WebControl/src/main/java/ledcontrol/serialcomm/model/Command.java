package ledcontrol.serialcomm.model;

import java.util.List;

public class Command {
    String name;
    List<String> param;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParam() {
        return param;
    }

    public void setParam(List<String> param) {
        this.param = param;
    }
}
