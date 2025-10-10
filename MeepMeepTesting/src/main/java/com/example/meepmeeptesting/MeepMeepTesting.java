package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PosePathSeqBuilder;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(0, 0, 0))
                .lineToX(30)
                .turn(Math.toRadians(90))
                .lineToY(30)
                .turn(Math.toRadians(90))
                .lineToX(0)
                .turn(Math.toRadians(90))
                .lineToY(0)
                        .setTangent(Math.toRadians(60))
                        .lineToX(-30)
                .setReversed(true)  // Reversed trajectory has no hooks on the start and end, and is smooth
                .splineTo(new Vector2d(-48.0, -24.0), -Math.PI / 2)
                .setReversed(false)
                .splineTo(new Vector2d(-48.0, 0.0), Math.PI)
                .strafeTo(new Vector2d(48, -48))
                .strafeToConstantHeading(new Vector2d(48, -48))
                .setTangent(0)
                .splineTo(new Vector2d(48, 48), Math.PI / 2)
                        .splineTo(new Vector2d(0,0),0)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}