package org.pionerds.ftc.teamcode.Hardware;

import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class AprilTagRelativeDistance {
    private double x1 = 0;
    private double y1 = 0;
    private double z1 = 0;
    private double Range1 = 0;

    public AprilTagRelativeDistance(Position position){
        x1 = position.x;
        y1 = position.y;
        z1 = position.z;
        Range1 = Math.sqrt(Math.pow(position.x, 2)+Math.pow(position.y, 2));
    }

    public double x(){
        return x1;
    }

    public double y(){
        return y1;
    }

    public double z(){
        return z1;
    }

    public double Range(){
        return Range1;
    }

}
