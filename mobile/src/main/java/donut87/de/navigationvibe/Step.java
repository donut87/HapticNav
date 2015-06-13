package donut87.de.navigationvibe;

import java.util.Vector;

enum StepDirection {
    UNKNOWN,
    LEFT,
    RIGHT
}

/**
 * Created by nrj on 13/06/15.
 */
public class Step {

    public String instruction = "";
    public Vector2 coordinate = new Vector2(0.0, 0.0);

    public Step(String instruction, Vector2 coordinate) {
        this.instruction = instruction;
        this.coordinate = coordinate;
    }

    public StepDirection direction() {
        if (this.instruction.contains("<b>left</b>")) {
            return StepDirection.LEFT;
        }
        else if (this.instruction.contains("<b>right</b>")) {
            return StepDirection.RIGHT;
        }
        return StepDirection.UNKNOWN;
    }
}
